package com.ndt2101.ezimarket.service.impl;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch._types.query_dsl.FuzzyQuery;
import co.elastic.clients.elasticsearch.core.search.Hit;
import co.elastic.clients.json.jackson.JacksonJsonpMapper;
import co.elastic.clients.transport.ElasticsearchTransport;
import co.elastic.clients.transport.rest_client.RestClientTransport;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.auth.Credentials;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.storage.*;
import com.ndt2101.ezimarket.base.BaseDTO;
import com.ndt2101.ezimarket.base.BasePagination;
import com.ndt2101.ezimarket.dto.SaleProgramDTO;
import com.ndt2101.ezimarket.dto.pagination.PaginateDTO;
import com.ndt2101.ezimarket.dto.product.ProductPayLoadDTO;
import com.ndt2101.ezimarket.dto.product.ProductResponseDTO;
import com.ndt2101.ezimarket.elasticsearch.dto.ProductDTO;
import com.ndt2101.ezimarket.elasticsearch.elasticsearchrepository.ELSProductRepository;
import com.ndt2101.ezimarket.elasticsearch.model.Product;
import com.ndt2101.ezimarket.exception.NotFoundException;
import com.ndt2101.ezimarket.model.*;
import com.ndt2101.ezimarket.repository.*;
import com.ndt2101.ezimarket.service.ProductService;
import com.ndt2101.ezimarket.specification.GenericSpecification;
import com.ndt2101.ezimarket.utils.FileHandle;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpHost;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.unit.Fuzziness;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.DefaultTransactionDefinition;
import org.springframework.web.multipart.MultipartFile;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.io.*;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@Slf4j
public class ProductServiceImpl extends BasePagination<ProductEntity, ProductRepository> implements ProductService {

    @Autowired
    private ProductRepository productRepository;
    @Autowired
    private CategoryRepository categoryRepository;
    @Autowired
    private ModelMapper mapper;
    @Autowired
    private ShopRepository shopRepository;
    @Autowired
    private ImageRepository imageRepository;
    @PersistenceContext
    private EntityManager entityManager;
    @Autowired
    private PlatformTransactionManager transactionManager;
    @Autowired
    private ProductTypeRepository productTypeRepository;

    @Autowired
    private ELSProductRepository elsProductRepository;
    @Autowired
    private SaleProgramRepository saleProgramRepository;
    @Autowired
    private FileHandle fileHandle;


    // Create the low-level client
    RestHighLevelClient client = new RestHighLevelClient(
            RestClient.builder(
                    new HttpHost("localhost", 9200, "http")
            )
    );

    @Autowired
    public ProductServiceImpl(ProductRepository repository) {
        super(repository);
    }

    @Override
    public List<ProductDTO> fuzzySearch(String value) throws IOException {

        List<ProductDTO> result = new ArrayList<>();

        QueryBuilder queryBuilder = QueryBuilders.boolQuery()
                .should(QueryBuilders.wildcardQuery("name", "*" + value + "*"))
                .should(QueryBuilders.fuzzyQuery("name", value).fuzziness(Fuzziness.AUTO).transpositions(false));

        SearchRequest searchRequest = new SearchRequest("product")
                .source(new SearchSourceBuilder()
                        .query(queryBuilder)
                );

        ObjectMapper objectMapper = new ObjectMapper();
        SearchResponse searchResponse = client.search(searchRequest, RequestOptions.DEFAULT);
        for (SearchHit hit : searchResponse.getHits().getHits()) {
            ProductDTO yourPojo = objectMapper.readValue(hit.getSourceAsString(), ProductDTO.class);
            result.add(yourPojo);
        }
        return result;
    }

    @Override
    @Transactional
    public String update(ProductPayLoadDTO productPayLoadDTO, Long productId) {
        List<Long> deletedIds = productPayLoadDTO.getProductTypeDTOs().stream()
                .filter(productTypeDTO -> productTypeDTO.getType().isBlank())
                .map(BaseDTO::getId)
                .toList();
        List<ProductTypeEntity> productTypeEntities = productTypeRepository.findAllById(deletedIds);
        productTypeEntities.forEach(productTypeEntity -> {
            productTypeEntity.setProduct(null);
        });
        deleteProductType(deletedIds);
        productPayLoadDTO.getProductTypeDTOs().removeIf(productTypeDTO -> productTypeDTO.getType().isBlank());
        deleteImage(productId, productPayLoadDTO.getShopId());
        create(productPayLoadDTO);
        return "Update product successfully";
    }

    @Transactional
    void deleteProductType(List<Long> deletedIds) {
        TransactionStatus transaction = transactionManager.getTransaction(new DefaultTransactionDefinition());
        Query query = entityManager.createQuery("delete from ProductTypeEntity p where p.id in :deletedIds");
        query.setParameter("deletedIds", deletedIds);
        query.executeUpdate();
        transactionManager.commit(transaction);
    }

    @Override
    public String delete(Long productId) {
        ProductEntity productEntity = productRepository.findById(productId).orElseThrow(() -> new NotFoundException("Product not found"));
        Long shopId = productEntity.getShop().getId();
        deleteImage(productId, shopId);
        productRepository.delete(productEntity);
        return "Delete product successfully";
    }

    @Override
    public ProductResponseDTO getProductDetail(Long productId) {
        ProductEntity productEntity = productRepository.findById(productId).orElseThrow(() -> new NotFoundException("Product not found"));
        return mapAndHandleSaleProgram(productEntity);
    }

    @Override
    public PaginateDTO<ProductResponseDTO> getList(Integer page, Integer perPage, GenericSpecification<ProductEntity> specification) {
        PaginateDTO<ProductEntity> productEntityPaginateDTO = this.paginate(page, perPage, specification);
        List<ProductResponseDTO> productResponseDTOs = productEntityPaginateDTO.getPageData().stream()
                .map(this::mapAndHandleSaleProgram)
                .toList();
        Page<ProductResponseDTO> pageData = new PageImpl<>(productResponseDTOs, PageRequest.of(page, perPage), perPage);
        return new PaginateDTO<>(pageData, productEntityPaginateDTO.getPagination());
    }

    private void deleteImage(Long productId, Long shopId) {
        List<ImageEntity> deletedImages = imageRepository.findByProduct(
                productRepository.findById(productId)
                        .orElseThrow(() -> new NotFoundException("Product not found")));
        deletedImages
                .forEach(imageEntity -> {
                    BlobId blobId = BlobId.of("ezi-market.appspot.com", "images/" + shopId + "/" + productId + "/" + imageEntity.getName());
                    Credentials credentials = null;
                    try {
                        credentials = GoogleCredentials.fromStream(new FileInputStream("src/main/resources/ezi-market-firebase-adminsdk-18xex-fa8c704037.json"));
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                    Storage storage = StorageOptions.newBuilder().setCredentials(credentials).build().getService();
                    boolean result = storage.delete(blobId);
                    log.info("delete image" + imageEntity.getName() + ": {}", result);
                });
        imageRepository.deleteAll(deletedImages);
    }

    @Override
    public String create(ProductPayLoadDTO productPayLoad) {
        CategoryEntity categoryEntity = categoryRepository.findById(productPayLoad.getCategoryId()).orElseThrow(() -> new NotFoundException("Category with id " + productPayLoad.getCategoryId() + " not found!"));
        List<String> imageNames = new ArrayList<>();
        ShopEntity shopEntity = shopRepository.findById(productPayLoad.getShopId()).orElseThrow(() -> new NotFoundException("Shop not found"));
        ProductEntity productEntity = mapper.map(productPayLoad, ProductEntity.class);
        productEntity.setCategory(categoryEntity);
        productEntity.setShop(shopEntity);

        if (productPayLoad.getId() != null) {
            SaleProgramEntity saleProgram = productRepository.findById(productPayLoad.getId()).get().getSaleProgram();
            if (saleProgram != null) {
                productEntity.setSaleProgram(saleProgram);
            }
        }

        ProductEntity savedProductEntity = productRepository.save(productEntity);
        List<ProductTypeEntity> productTypeEntities = productPayLoad.getProductTypeDTOs().stream().map(productTypeDTO -> {
            ProductTypeEntity productTypeEntity = mapper.map(productTypeDTO, ProductTypeEntity.class);
            productTypeEntity.setProduct(savedProductEntity);
            return productTypeEntity;
        }).toList();
        productTypeRepository.saveAll(productTypeEntities);
        List<String> imageUrls = uploadImages(productPayLoad, imageNames, productEntity.getId());
        for (int i = 0; i < imageNames.size(); i++) {
            imageRepository.save(new ImageEntity(imageNames.get(i), imageUrls.get(i), productEntity, null));
        }
        elsProductRepository.save(new Product(productEntity.getId(), productEntity.getName()));
        return "Create product successfully";
    }

    private List<String> uploadImages(ProductPayLoadDTO productPayLoad, List<String> fileNames, Long id) {
        List<String> imageUrls = new ArrayList<>();
        productPayLoad.getImages().forEach(multipartFile -> {
            try {
                String fileName = multipartFile.getOriginalFilename();                        // to get original file name
                fileName = UUID.randomUUID().toString().concat(fileHandle.getExtension(fileName));  // to generated random string values for file name.
                fileNames.add(fileName);

                File file = fileHandle.convertToFile(multipartFile, fileName);                      // to convert multipartFile to File
                String path = "images/" + productPayLoad.getShopId() + "/" + id + "/" + fileName;
                imageUrls.add(fileHandle.uploadFile(file, path));                                   // to get uploaded file link
                file.delete();                                                                // to delete the copy of uploaded file stored in the project folder
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        return imageUrls;
    }

    private ProductResponseDTO mapAndHandleSaleProgram(ProductEntity productEntity) {
        SaleProgramEntity saleProgram = productEntity.getSaleProgram();
        ProductResponseDTO productResponse = mapper.map(productEntity, ProductResponseDTO.class);
        if (saleProgram != null) {
            if (saleProgram.getEndTime() < System.currentTimeMillis()) {
                List<ProductEntity> productEntities = saleProgram.getProducts()
                        .stream().map(product -> {
                            product.setSaleProgram(null);
                            return product;
                        }).toList();
                productRepository.saveAll(productEntities);
                saleProgramRepository.delete(saleProgram);
            } else {
                productResponse.getProductTypes().forEach(productTypeDTO -> {
                    productTypeDTO.setDiscountPrice(productTypeDTO.getPrice() - Math.round(productTypeDTO.getPrice() * (double) saleProgram.getDiscount()));
                });
            }
        }
        productResponse.setImages(productEntity.getImageEntities().stream().map(ImageEntity::getUrl).toList());
        productResponse.getShop().setAvatar(productEntity.getShop().getUserLoginData().getAvatarUrl());
        return productResponse;
    }
}

package com.ndt2101.ezimarket.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ndt2101.ezimarket.base.BaseDTO;
import com.ndt2101.ezimarket.base.BasePagination;
import com.ndt2101.ezimarket.constant.Common;
import com.ndt2101.ezimarket.dto.ImageDTO;
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

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.io.IOException;
import java.util.*;

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
    @Autowired
    private PostRepository postRepository;
    @Autowired
    private ProductReportRepository productReportRepository;
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
        deleteImage(productPayLoadDTO);
        create(productPayLoadDTO);
        return "Update product successfully";
    }

    @Transactional
    public void deleteProductType(List<Long> deletedIds) {
        TransactionStatus transaction = transactionManager.getTransaction(new DefaultTransactionDefinition());
        Query query = entityManager.createQuery("delete from ProductTypeEntity p where p.id in :deletedIds");
        query.setParameter("deletedIds", deletedIds);
        query.executeUpdate();
        transactionManager.commit(transaction);
    }

    @Override
    public String delete(Long productId) {
        ProductEntity productEntity = productRepository.findById(productId).orElseThrow(() -> new NotFoundException("Product not found"));
        imageRepository.deleteAll(productEntity.getImageEntities());
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

    @Override
    public String report(Long productId) {
        if (!productReportRepository.existsByProduct_Id(productId)) {
            ProductReportEntity productReportEntity = productReportRepository.save(new ProductReportEntity(productRepository.findById(productId).orElseThrow(Common.productNotFound)));
            if (productReportEntity.getId() != null) {
                return "Báo cáo sản phẩm thành công, admin sẽ xem và kiểm duyệt. Cảm ơn bạn đã góp phần xây dựng một Ezi Market văn minh!";
            } else {
                return null;
            }
        } else {
            return "Sản phẩm đang trong quá trình kiểm duyệt";
        }
    }

    @Override
    public List<ProductResponseDTO> getReportedProducts() {
        return productReportRepository.findAll().stream()
                .map(productReportEntity -> mapAndHandleSaleProgram(productReportEntity.getProduct()))
                .toList();
    }

    @Override
    public String handleReport(Long productId, String status) {
        ProductEntity productEntity = productRepository.findById(productId).orElseThrow(Common.productNotFound);
        if (status.equals("approve")) {
            productEntity.setStatus("banned");
            productEntity.getPosts().forEach(postEntity -> {
                postEntity.setProduct(null);
            });
            postRepository.saveAll(productEntity.getPosts());
            productEntity.setPosts(null);
            productRepository.save(productEntity);
        }
        productReportRepository.deleteById(productEntity.getReportEntity().getId());
        return "Xử lý thành công";
    }

    @Override
    public String updateView(Long productId) {
        ProductEntity productEntity = productRepository.findById(productId).orElseThrow(Common.productNotFound);
        productEntity.setView(productEntity.getView() + 1);
        productEntity = productRepository.save(productEntity);
        return productEntity.getView().toString();
    }

    private void deleteImage(ProductPayLoadDTO productPayLoadDTO) {
        List<ImageEntity> oldImages = imageRepository.findByProduct(
                productRepository.findById(productPayLoadDTO.getId())
                        .orElseThrow(() -> new NotFoundException("Product not found")));

        Set<ImageEntity> neededDelete = new HashSet<>(oldImages);
        Set<Long> neededSaveIds = new HashSet<>(productPayLoadDTO.getImages());

        productPayLoadDTO.getImages().forEach(imageId -> {
            neededDelete.removeIf(imageEntity -> {
                if (Objects.equals(imageEntity.getId(), imageId)) {
                    neededSaveIds.remove(imageId);
                    return true;
                } else {
                    return false;
                }
            });
        });
        imageRepository.deleteAll(neededDelete);
        productPayLoadDTO.setImages(neededSaveIds.stream().toList());
    }

    @Override
    public String create(ProductPayLoadDTO productPayLoad) {
        CategoryEntity categoryEntity = categoryRepository.findById(productPayLoad.getCategoryId()).orElseThrow(() -> new NotFoundException("Category with id " + productPayLoad.getCategoryId() + " not found!"));
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
        List<ImageEntity> imageEntities = imageRepository.findAllById(productPayLoad.getImages());
        imageEntities.forEach(imageEntity -> {
            imageEntity.setProduct(savedProductEntity);
        });
        imageRepository.saveAll(imageEntities);
        List<ProductTypeEntity> productTypeEntities = productPayLoad.getProductTypeDTOs().stream().map(productTypeDTO -> {
            ProductTypeEntity productTypeEntity = mapper.map(productTypeDTO, ProductTypeEntity.class);
            productTypeEntity.setProduct(savedProductEntity);
            return productTypeEntity;
        }).toList();
        productTypeRepository.saveAll(productTypeEntities);
        elsProductRepository.save(new Product(productEntity.getId(), productEntity.getName()));
        return "Create product successfully";
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
        List<ImageDTO> imageDTOs = productEntity.getImageEntities().stream().map(imageEntity -> mapper.map(imageEntity, ImageDTO.class)).toList();
        productResponse.setImages(imageDTOs);
        productResponse.getShop().setAvatar(productEntity.getShop().getUserLoginData().getAvatarUrl());
        return productResponse;
    }
}

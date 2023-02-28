package com.ndt2101.ezimarket.service.impl;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch._types.query_dsl.FuzzyQuery;
import co.elastic.clients.elasticsearch.core.SearchResponse;
import co.elastic.clients.elasticsearch.core.search.Hit;
import co.elastic.clients.json.jackson.JacksonJsonpMapper;
import co.elastic.clients.transport.ElasticsearchTransport;
import co.elastic.clients.transport.rest_client.RestClientTransport;
import com.google.auth.Credentials;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.storage.*;
import com.google.firebase.cloud.StorageClient;
import com.ndt2101.ezimarket.base.BaseDTO;
import com.ndt2101.ezimarket.dto.product.ProductPayLoadDTO;
import com.ndt2101.ezimarket.dto.product.ProductResponseDTO;
import com.ndt2101.ezimarket.dto.product.ProductTypeDTO;
import com.ndt2101.ezimarket.elasticsearch.dto.ProductDTO;
import com.ndt2101.ezimarket.elasticsearch.elasticsearchrepository.ELSProductRepository;
import com.ndt2101.ezimarket.elasticsearch.model.Product;
import com.ndt2101.ezimarket.exception.NotFoundException;
import com.ndt2101.ezimarket.model.*;
import com.ndt2101.ezimarket.repository.*;
import com.ndt2101.ezimarket.service.ProductService;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpHost;
import org.elasticsearch.client.RestClient;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeMap;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

@Service
@Slf4j
public class ProductServiceImpl implements ProductService {

    private static final String DOWNLOAD_URL = "https://firebasestorage.googleapis.com/v0/b/ezi-market.appspot.com/o/%s?alt=media";
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
    private ProductTypeRepository productTypeRepository;

    @Autowired
    private ELSProductRepository elsProductRepository;
    @Autowired
    private SaleProgramRepository saleProgramRepository;


    // Create the low-level client
    RestClient restClient = RestClient.builder(new HttpHost("localhost", 9200)).build();

    // Create the transport with a Jackson mapper
    ElasticsearchTransport transport = new RestClientTransport(restClient, new JacksonJsonpMapper());

    // And create the API client
    ElasticsearchClient client = new ElasticsearchClient(transport);

    @Override
    public List<ProductDTO> fuzzySearch(String value) throws IOException {

        FuzzyQuery fuzzyQuery = new FuzzyQuery.Builder().field("name").value(value).transpositions(true).fuzziness("auto").build();
        SearchResponse<ProductDTO> fuzzySearch = client.search(s -> s.index("product").query(q -> q.fuzzy(fuzzyQuery)), ProductDTO.class);

        List<ProductDTO> result = new ArrayList<>();

        for (Hit<ProductDTO> hit : fuzzySearch.hits().hits()) {
            result.add(hit.source());
        }
        return result;
    }

    @Override
    public String update(ProductPayLoadDTO productPayLoadDTO, Long productId) {
        productTypeRepository.deleteAllById(
                productPayLoadDTO.getProductTypeDTOs().stream()
                        .filter(productTypeDTO -> productTypeDTO.getType().isBlank())
                        .map(BaseDTO::getId)
                        .toList());
        productPayLoadDTO.getProductTypeDTOs().removeIf(productTypeDTO -> productTypeDTO.getType().isBlank());
        deleteImage(productId, productPayLoadDTO.getShopId());
        create(productPayLoadDTO);
        return "Update product successfully";
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
        SaleProgramEntity saleProgram;
        ProductEntity productEntity = productRepository.findById(productId).orElseThrow(() -> new NotFoundException("Product not found"));
        saleProgram = productEntity.getSaleProgram();
//        TypeMap<ProductEntity, ProductResponseDTO> propertyMapper = mapper.createTypeMap(ProductEntity.class, ProductResponseDTO.class);
//        propertyMapper.addMappings(mapper -> mapper.skip(ProductResponseDTO::setImages));
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
            imageRepository.save(new ImageEntity(imageNames.get(i), imageUrls.get(i), productEntity));
        }
        elsProductRepository.save(new Product(productEntity.getId(), productEntity.getName()));
        return "Create product successfully";
    }

    private List<String> uploadImages(ProductPayLoadDTO productPayLoad, List<String> fileNames, Long id) {
        List<String> imageUrls = new ArrayList<>();
        productPayLoad.getImages().forEach(multipartFile -> {
            try {
                String fileName = multipartFile.getOriginalFilename();                        // to get original file name
                fileName = UUID.randomUUID().toString().concat(this.getExtension(fileName));  // to generated random string values for file name.
                fileNames.add(fileName);

                File file = this.convertToFile(multipartFile, fileName);                      // to convert multipartFile to File
                imageUrls.add(this.uploadFile(file, fileName, productPayLoad.getShopId(), id));                                   // to get uploaded file link
                file.delete();                                                                // to delete the copy of uploaded file stored in the project folder
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        return imageUrls;
    }

    private String uploadFile(File file, String fileName, Long shopId, Long id) throws IOException {
        BlobId blobId = BlobId.of("ezi-market.appspot.com", "images/" + shopId + "/" + id + "/" + fileName);
        BlobInfo blobInfo = BlobInfo.newBuilder(blobId).setContentType("image/png").build();
        Credentials credentials = GoogleCredentials.fromStream(new FileInputStream("src/main/resources/ezi-market-firebase-adminsdk-18xex-fa8c704037.json"));
        Storage storage = StorageOptions.newBuilder().setCredentials(credentials).build().getService();
        storage.create(blobInfo, Files.readAllBytes(file.toPath()));
        return String.format(DOWNLOAD_URL, URLEncoder.encode("images/" + shopId + "/" + id + "/" + fileName, StandardCharsets.UTF_8));
    }

    private File convertToFile(MultipartFile multipartFile, String fileName) throws IOException {
        File tempFile = new File(fileName);
        try (FileOutputStream fos = new FileOutputStream(tempFile)) {
            fos.write(multipartFile.getBytes());
            fos.close();
        }
        return tempFile;
    }

    private String getExtension(String fileName) {
        return fileName.substring(fileName.lastIndexOf("."));
    }
}

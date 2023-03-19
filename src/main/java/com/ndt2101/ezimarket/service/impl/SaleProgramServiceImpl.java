package com.ndt2101.ezimarket.service.impl;

import com.ndt2101.ezimarket.base.BasePagination;
import com.ndt2101.ezimarket.dto.ImageDTO;
import com.ndt2101.ezimarket.dto.SaleProgramDTO;
import com.ndt2101.ezimarket.dto.pagination.PaginateDTO;
import com.ndt2101.ezimarket.dto.product.ProductResponseDTO;
import com.ndt2101.ezimarket.elasticsearch.dto.ProductDTO;
import com.ndt2101.ezimarket.exception.NotFoundException;
import com.ndt2101.ezimarket.model.ProductEntity;
import com.ndt2101.ezimarket.model.SaleProgramEntity;
import com.ndt2101.ezimarket.model.ShopEntity;
import com.ndt2101.ezimarket.repository.ProductRepository;
import com.ndt2101.ezimarket.repository.SaleProgramRepository;
import com.ndt2101.ezimarket.repository.ShopRepository;
import com.ndt2101.ezimarket.service.SaleProgramService;
import com.ndt2101.ezimarket.specification.GenericSpecification;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.sql.Date;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
public class SaleProgramServiceImpl extends BasePagination<SaleProgramEntity, SaleProgramRepository> implements SaleProgramService {

    @Autowired
    private ModelMapper mapper;
    @Autowired
    private SaleProgramRepository saleProgramRepository;
    @PersistenceContext
    private EntityManager entityManager;
    @Autowired
    private PlatformTransactionManager transactionManager;
    @Autowired
    private ProductRepository productRepository;
    @Autowired
    private ShopRepository shopRepository;

    @Autowired
    public SaleProgramServiceImpl(SaleProgramRepository saleProgramRepository) {
        super(saleProgramRepository);
    }

    @Override
    public String create(SaleProgramDTO saleProgramDTO) {

        List<Long> productIds = saleProgramDTO.getProducts().stream().map(ProductResponseDTO::getId).toList();
        List<ProductEntity> productEntities = productRepository.findAllById(productIds);

        ShopEntity shopEntity = shopRepository.findById(saleProgramDTO.getShopId()).orElseThrow(() -> new NotFoundException("Shop not found"));

        SaleProgramEntity saleProgramEntity = mapper.map(saleProgramDTO, SaleProgramEntity.class);

        productEntities.forEach(productEntity -> productEntity.setSaleProgram(saleProgramEntity));

        saleProgramEntity.setShop(shopEntity);
        saleProgramEntity.setProducts(productEntities);

        SaleProgramEntity savedSaleProgram = saleProgramRepository.save(saleProgramEntity);
        return "Create sale program successfully";
    }

    @Override
    public String update(SaleProgramDTO saleProgramDTO, Long id) {
        List<Long> productIds = saleProgramDTO.getProducts().stream().map(ProductResponseDTO::getId).toList();

        List<ProductEntity> productEntities = productRepository.findAllById(productIds);
        ShopEntity shopEntity = shopRepository.findById(saleProgramDTO.getShopId()).orElseThrow(() -> new NotFoundException("Shop not found"));
        SaleProgramEntity current = saleProgramRepository.findById(id).orElseThrow(() -> new NotFoundException("Sale program not found"));

        Set<ProductEntity> currentProducts = new HashSet<>(current.getProducts());
        List<ProductEntity> updateProducts = new ArrayList<>(productEntities.stream().toList());
        productEntities.forEach(productEntity -> {
            if (currentProducts.contains(productEntity)) {
                updateProducts.remove(productEntity); // ket qua cuoi cung con nhung product can them vao chuong trinh
                currentProducts.remove(productEntity); // ket qua cuoi cung con nhung product can xoa khoi chuong trinh
            }
        });

        SaleProgramEntity updated = mapper.map(saleProgramDTO, SaleProgramEntity.class);

        updateProducts.forEach(productEntity -> productEntity.setSaleProgram(updated));

        updated.setShop(shopEntity);
        updated.setProducts(updateProducts);
        mapper.map(updated, current);
        current.setUpdatedTime(new Date(System.currentTimeMillis()));

        SaleProgramEntity savedSaleProgram = saleProgramRepository.save(current);

        currentProducts.forEach(productEntity -> productEntity.setSaleProgram(null));
        productRepository.saveAll(currentProducts);
        return "Update sale program successfully";
    }

    @Override
    public SaleProgramDTO getById(Long id) {
        SaleProgramEntity saleProgramEntity = saleProgramRepository.findById(id).orElseThrow(() -> new NotFoundException("Sale program not found"));
        SaleProgramDTO saleProgramResponse = mapper.map(saleProgramEntity, SaleProgramDTO.class);
        List<ProductResponseDTO> productResponseDTOs = saleProgramEntity.getProducts().stream().map(productEntity -> {
            ProductResponseDTO productResponseDTO = mapper.map(productEntity, ProductResponseDTO.class);
            productResponseDTO.setImages(new ArrayList<>(List.of(mapper.map(productEntity.getImageEntities().get(0), ImageDTO.class))));
            return productResponseDTO;
        }).toList();
        saleProgramResponse.setProducts(productResponseDTOs);
        return saleProgramResponse;
    }

    @Override
    public String delete(Long id) {
        SaleProgramEntity saleProgram = saleProgramRepository.findById(id).orElseThrow(() -> new NotFoundException("Sale program not found"));

        List<ProductEntity> productEntities = saleProgram.getProducts().stream().map(productEntity -> {
            productEntity.setSaleProgram(null);
            return productEntity;
        }).toList();
        productRepository.saveAll(productEntities);

        saleProgramRepository.delete(saleProgram);
        return "Delete sale program successfully";
    }

    @Override
    public PaginateDTO<SaleProgramDTO> getSalePrograms(int page, int perPage, GenericSpecification<SaleProgramEntity> specification) {
        PaginateDTO<SaleProgramEntity> saleProgramEntityPaginateDTO = this.paginate(page, perPage, specification);
        List<SaleProgramDTO> saleProgramDTOs = saleProgramEntityPaginateDTO.getPageData().stream()
                .map(saleProgramEntity -> {
                    SaleProgramDTO saleProgramDTO = mapper.map(saleProgramEntity, SaleProgramDTO.class);
                    saleProgramDTO.setShopId(saleProgramEntity.getShop().getId());
                    List<ProductResponseDTO> productDTOs = new ArrayList<>();

                    saleProgramEntity.getProducts().forEach(productEntity -> {
                        ProductResponseDTO productResponseDTO = mapper.map(productEntity, ProductResponseDTO.class);
                        List<ImageDTO> imageDTOs = new ArrayList<>();
                        ImageDTO imageDTO = new ImageDTO();
                        imageDTO.setId(productEntity.getImageEntities().get(0).getId());
                        imageDTO.setUrl(productEntity.getImageEntities().get(0).getUrl());
                        imageDTOs.add(imageDTO);
                        productResponseDTO.setImages(imageDTOs);
                        productDTOs.add(productResponseDTO);
                    });
                    saleProgramDTO.setProducts(productDTOs);
                    return saleProgramDTO;
                })
                .toList();
        Page<SaleProgramDTO> pageData = new PageImpl<>(saleProgramDTOs, PageRequest.of(page, perPage), perPage);
        return new PaginateDTO<>(pageData, saleProgramEntityPaginateDTO.getPagination());
    }

    @Scheduled(cron = "0 0 0 * * *")
    void automaticallyDelete() {
        TransactionStatus transaction = transactionManager.getTransaction(new DefaultTransactionDefinition());
        try {
            String updateSpql = "UPDATE ProductEntity p SET p.saleProgram = null WHERE p.saleProgram in (SELECT s.id FROM SaleProgramEntity s where s.endTime < FUNCTION('UNIX_TIMESTAMP') * 1000)";
            entityManager.createQuery(updateSpql).executeUpdate();
            String spql = "DELETE FROM SaleProgramEntity where endTime < FUNCTION('UNIX_TIMESTAMP') * 1000";
            entityManager.createQuery(spql).executeUpdate();
            transactionManager.commit(transaction);
        } catch (Exception e) {
            transactionManager.rollback(transaction);
            throw e;
        }
    }


}

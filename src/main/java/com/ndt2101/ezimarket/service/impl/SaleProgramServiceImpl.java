package com.ndt2101.ezimarket.service.impl;

import com.ndt2101.ezimarket.dto.SaleProgramDTO;
import com.ndt2101.ezimarket.dto.product.ProductResponseDTO;
import com.ndt2101.ezimarket.exception.NotFoundException;
import com.ndt2101.ezimarket.model.ProductEntity;
import com.ndt2101.ezimarket.model.SaleProgramEntity;
import com.ndt2101.ezimarket.model.ShopEntity;
import com.ndt2101.ezimarket.repository.ProductRepository;
import com.ndt2101.ezimarket.repository.SaleProgramRepository;
import com.ndt2101.ezimarket.repository.ShopRepository;
import com.ndt2101.ezimarket.service.SaleProgramService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.DefaultTransactionDefinition;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.sql.Date;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
public class SaleProgramServiceImpl implements SaleProgramService {

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

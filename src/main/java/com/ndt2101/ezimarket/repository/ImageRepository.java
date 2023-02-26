package com.ndt2101.ezimarket.repository;

import com.ndt2101.ezimarket.model.ImageEntity;
import com.ndt2101.ezimarket.model.ProductEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ImageRepository extends JpaRepository<ImageEntity, Long> {
    List<ImageEntity> findByProduct(ProductEntity product);
}

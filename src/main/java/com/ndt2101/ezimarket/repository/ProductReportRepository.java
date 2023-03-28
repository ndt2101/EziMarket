package com.ndt2101.ezimarket.repository;

import com.ndt2101.ezimarket.model.ProductReportEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductReportRepository extends JpaRepository<ProductReportEntity, Long> {
    Boolean existsByProduct_Id(Long productId);
}

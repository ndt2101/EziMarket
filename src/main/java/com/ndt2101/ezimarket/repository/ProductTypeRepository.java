package com.ndt2101.ezimarket.repository;

import com.ndt2101.ezimarket.model.ProductTypeEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;

public interface ProductTypeRepository extends JpaRepository<ProductTypeEntity, Long> {
    void deleteAllByIdIn(Collection<Long> id);
}

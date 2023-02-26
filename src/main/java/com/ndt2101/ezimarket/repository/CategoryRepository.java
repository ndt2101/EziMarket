package com.ndt2101.ezimarket.repository;

import com.ndt2101.ezimarket.model.CategoryEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CategoryRepository extends JpaRepository<CategoryEntity, Long> {

}

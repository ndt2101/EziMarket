package com.ndt2101.ezimarket.repository;

import com.ndt2101.ezimarket.model.ShopEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface ShopRepository extends JpaRepository<ShopEntity, Long>, JpaSpecificationExecutor<ShopEntity> {

}

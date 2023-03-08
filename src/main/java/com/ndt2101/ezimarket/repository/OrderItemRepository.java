package com.ndt2101.ezimarket.repository;

import com.ndt2101.ezimarket.model.OrderItemEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface OrderItemRepository extends JpaRepository<OrderItemEntity, Long>, JpaSpecificationExecutor<OrderItemEntity> {
}

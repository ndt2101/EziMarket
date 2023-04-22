package com.ndt2101.ezimarket.repository;

import com.ndt2101.ezimarket.model.OrderEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface OrderRepository extends JpaRepository<OrderEntity, Long>, JpaSpecificationExecutor<OrderEntity> {
    @Query(value = "select month(created_time) as month, count(*) as total from orders where year(created_time) = 2023 and status != 'canceled' and shop_id = :shopId group by date_format(created_time, '%Y-%m'), created_time", nativeQuery = true)
    List<Map<String, Long>> countOrderViaMonth(Long shopId);

    @Query(value = "select month(created_time) as month, sum(total_price - (select price from shipping_method as sp where id = o.shipping_method_id)) as total from orders as o where year(created_time) = 2023 and status = 'received' and shop_id = :shopId group by date_format(created_time, '%Y-%m'), created_time", nativeQuery = true)
    List<Map<String, Long>>  incomeViaMonth(Long shopId);
}

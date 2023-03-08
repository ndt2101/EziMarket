package com.ndt2101.ezimarket.model;

import com.ndt2101.ezimarket.base.BaseEntity;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

@Table(name = "orders_items")
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class OrderItemEntity extends BaseEntity {

    @ManyToOne
    @JoinColumn(name = "product_type_id")
    private ProductTypeEntity productType;

    @ManyToOne
    @JoinColumn(name = "order_id")
    private OrderEntity order;

    @Column
    private Long quantity;
}

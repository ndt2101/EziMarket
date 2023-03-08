package com.ndt2101.ezimarket.model;

import com.ndt2101.ezimarket.base.BaseEntity;
import com.ndt2101.ezimarket.model.paypal.Payment;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Table(name = "orders")
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class OrderEntity extends BaseEntity {
    @ManyToOne
    @JoinColumn(name = "user_id")
    private UserLoginDataEntity user;

    @ManyToOne
    @JoinColumn(name = "shop_id")
    private ShopEntity shop;

    @Column
    private Long totalPrice;

    @ManyToOne
    @JoinColumn(name = "address_id")
    private AddressEntity shipTo;

    @Column
    private String status;

    @Column
    private String noteToShop;

    @OneToOne(mappedBy = "order")
    private Payment payment;

    @OneToOne
    @JoinColumn(name = "shipping_method_id")
    private ShippingMethod shippingMethod;

    @OneToMany(mappedBy = "order", fetch = FetchType.LAZY)
    private Set<OrderItemEntity> orderItems = new HashSet<>();
}

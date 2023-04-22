package com.ndt2101.ezimarket.model;

import com.ndt2101.ezimarket.base.BaseEntity;
//import com.ndt2101.ezimarket.model.paypal.Payment;
import com.ndt2101.ezimarket.model.paypal.PaymentMethod;
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

//    @OneToOne(mappedBy = "order")
//    private Payment payment;

    @OneToOne(cascade = CascadeType.REMOVE)
    @JoinColumn(name = "shipping_method_id")
    private ShippingMethod shippingMethod;

    @OneToMany(mappedBy = "order", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<OrderItemEntity> orderItems = new HashSet<>();

    @ManyToOne
    @JoinColumn(name = "payemet_method_id")
    private PaymentMethod paymentMethod;

    @Column
    private String code;

    public OrderEntity clone(OrderEntity orderEntity) {
        OrderEntity clone = new OrderEntity();
        clone.setTotalPrice(orderEntity.totalPrice);
        clone.setCode(orderEntity.code);
        clone.setStatus(orderEntity.status);
        clone.setPaymentMethod(orderEntity.paymentMethod);
//        clone.setPayment(orderEntity.payment);
        clone.setShippingMethod(orderEntity.shippingMethod);
        clone.setShipTo(orderEntity.shipTo);
        clone.setNoteToShop(orderEntity.noteToShop);
        clone.setOrderItems(orderEntity.orderItems);
        clone.setShop(orderEntity.shop);
        clone.setUser(orderEntity.user);
        clone.setId(orderEntity.getId());
        clone.setUpdatedTime(orderEntity.getUpdatedTime());
        clone.setCreatedTime(orderEntity.getCreatedTime());
        clone.setCreatedBy(orderEntity.getCreatedBy());
        clone.setUpdatedBy(orderEntity.getUpdatedBy());

        return clone;
    }
}

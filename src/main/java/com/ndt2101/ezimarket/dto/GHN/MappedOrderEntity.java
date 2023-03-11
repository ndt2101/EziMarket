package com.ndt2101.ezimarket.dto.GHN;

import com.ndt2101.ezimarket.model.*;
import com.ndt2101.ezimarket.model.paypal.Payment;
import com.ndt2101.ezimarket.model.paypal.PaymentMethod;
import lombok.*;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class MappedOrderEntity {

    private Long id;

    private UserLoginDataEntity user;

    private ShopEntity shop;

    private Long totalPrice;

    private AddressEntity shipTo;

    private String status;

    private String noteToShop;

    private Payment payment;

    private ShippingMethod shippingMethod;

    private Set<MappedOrderItemEntity> orderItems = new HashSet<>();

    private PaymentMethod paymentMethod;
    private String code;
}

package com.ndt2101.ezimarket.dto;

import com.ndt2101.ezimarket.base.BaseDTO;
import com.ndt2101.ezimarket.model.ShippingMethod;
import com.ndt2101.ezimarket.model.paypal.Payment;
import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class OrderDTO extends BaseDTO {
    private UserDTO userDTO;
    private ShopDTO shop;
    private Long totalPrice;
    private AddressDTO shipTo;
    private String status;
    private String noteToShop;
    private Payment payment;
    private Long voucherId;
    private ShippingMethod shippingMethod;
}

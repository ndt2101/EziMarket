package com.ndt2101.ezimarket.dto.GHN;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.checkerframework.checker.units.qual.A;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class ShippingCalculateResponse {
    private Integer code;
    private String message;
    private ShippingCalculateResponseData data;
}

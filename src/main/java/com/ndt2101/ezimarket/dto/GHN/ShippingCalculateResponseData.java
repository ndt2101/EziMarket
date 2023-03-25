package com.ndt2101.ezimarket.dto.GHN;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class ShippingCalculateResponseData {
    private Long total;
    private Long service_fee;
    private Long r2s_fee;
}

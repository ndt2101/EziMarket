package com.ndt2101.ezimarket.dto.GHN;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class ShippingCalculate {
    private Long from_district_id;
    private Long service_id;
    private Long service_type_id;
    private Long to_district_id;
    private String to_ward_code;
    private Integer height;
    private Integer width;
    private Integer length;
    private Integer weight;
    protected Long insurance_value;
}

package com.ndt2101.ezimarket.dto.GHN;

import lombok.*;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class OrderDetail {
    private String order_code;
    private String sort_code;
    private String trans_type;
    private String ward_encode;
    private String district_encode;
    private FeeDetail fee;
    private int total_fee;
    private String expected_delivery_time;

    // Constructor, getters, and setters
}
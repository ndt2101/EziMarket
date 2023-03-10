package com.ndt2101.ezimarket.dto.GHN;

import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
@Builder
public class Store {
    private Long shop_id;
    private Long district_id;
    private String ward_code;
    private String name;
    private String phone;
    private String address;
}

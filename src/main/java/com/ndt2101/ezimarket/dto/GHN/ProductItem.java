package com.ndt2101.ezimarket.dto.GHN;

import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class ProductItem {
    private String name;
    private String code;
    private int quantity;
    private int price;
    private int weight;

}

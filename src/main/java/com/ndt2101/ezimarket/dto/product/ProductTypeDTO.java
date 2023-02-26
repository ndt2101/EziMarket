package com.ndt2101.ezimarket.dto.product;

import com.ndt2101.ezimarket.base.BaseDTO;
import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class ProductTypeDTO extends BaseDTO {
    private String type;
    private Long price;
    private Integer quantity;
    private Long discountPrice;
}
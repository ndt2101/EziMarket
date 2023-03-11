package com.ndt2101.ezimarket.dto;

import com.ndt2101.ezimarket.base.BaseDTO;
import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class OrderItemDTO extends BaseDTO {
    private Long userId;
    private Long productTypeId;
    private Long itemQuantity;
}

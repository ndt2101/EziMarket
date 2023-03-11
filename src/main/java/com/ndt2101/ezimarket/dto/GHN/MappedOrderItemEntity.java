package com.ndt2101.ezimarket.dto.GHN;

import com.ndt2101.ezimarket.model.ProductTypeEntity;
import lombok.*;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class MappedOrderItemEntity {

    private Long id;
    private MappedProductTypeEntity productType;

    private MappedOrderEntity order;


    private Long itemQuantity;
}

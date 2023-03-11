package com.ndt2101.ezimarket.dto.GHN;

import com.ndt2101.ezimarket.model.OrderItemEntity;
import com.ndt2101.ezimarket.model.ProductEntity;
import lombok.*;

import javax.persistence.*;
import javax.validation.constraints.Min;
import java.util.Set;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class MappedProductTypeEntity {
    private Long id;

    private String type;

    private Long price;

    private Long quantity;
    private Long discountPrice = price;

    private MappedProductEntity product;


    private Set<MappedOrderItemEntity> orderItems;
}

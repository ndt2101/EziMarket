package com.ndt2101.ezimarket.dto;

import com.ndt2101.ezimarket.base.BaseDTO;
import com.ndt2101.ezimarket.dto.product.ProductTypeDTO;
import com.ndt2101.ezimarket.model.OrderEntity;
import com.ndt2101.ezimarket.model.ProductTypeEntity;
import lombok.*;

import javax.persistence.Column;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class OrderItemDTO extends BaseDTO {
    private Long userId;
    private Long productTypeId;
    private Long quantity;
}

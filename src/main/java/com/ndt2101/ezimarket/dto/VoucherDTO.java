package com.ndt2101.ezimarket.dto;

import com.ndt2101.ezimarket.base.BaseDTO;
import com.ndt2101.ezimarket.model.ShopEntity;
import lombok.*;

import javax.persistence.Column;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class VoucherDTO extends BaseDTO {
    private Float discount;
    private Long endTime;
    private int quantity;
    private Long priceCondition;
    private int saved;
    private String img;
    private Long shopId;
}

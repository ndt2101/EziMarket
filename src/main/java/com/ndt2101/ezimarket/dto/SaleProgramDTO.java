package com.ndt2101.ezimarket.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.ndt2101.ezimarket.base.BaseDTO;
import com.ndt2101.ezimarket.dto.product.ProductResponseDTO;
import lombok.*;

import java.sql.Date;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class SaleProgramDTO extends BaseDTO {
    private String name;
    private Long endTime;
    private Float discount;
    private Long shopId;
    private List<ProductResponseDTO> products;
}

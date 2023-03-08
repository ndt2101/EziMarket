package com.ndt2101.ezimarket.dto;

import com.ndt2101.ezimarket.base.BaseDTO;
import com.ndt2101.ezimarket.dto.product.ProductResponseDTO;
import com.ndt2101.ezimarket.elasticsearch.dto.ProductDTO;
import lombok.*;

import javax.persistence.Column;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class ShopDTO extends BaseDTO {
    private String name;
    private String description;
    private Float rate;
    private String avatar;
    private List<ProductResponseDTO> productDTOList;
}

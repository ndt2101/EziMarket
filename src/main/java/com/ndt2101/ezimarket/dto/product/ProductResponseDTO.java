package com.ndt2101.ezimarket.dto.product;

import com.ndt2101.ezimarket.base.BaseDTO;
import com.ndt2101.ezimarket.dto.CategoryDTO;
import com.ndt2101.ezimarket.dto.ImageDTO;
import com.ndt2101.ezimarket.dto.ShopDTO;
import lombok.*;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class ProductResponseDTO extends BaseDTO {
    private String name;
    private String description;
    private CategoryDTO category;
    private List<ProductTypeDTO> productTypes;
    private String status;
    private Long view;
    private Float rate;
    private Long soldNumber;
    private ShopDTO shop;
    private List<ImageDTO> images;
    private Float weight;
    private Float width;
    private Float height;
    private Float length;
}

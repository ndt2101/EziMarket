package com.ndt2101.ezimarket.dto.product;

import com.ndt2101.ezimarket.base.BaseDTO;
import lombok.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class ProductPayLoadDTO extends BaseDTO {
    private String name;
    private String description;
    private Long categoryId;
    private Long shopId;
    private List<ProductTypeDTO> productTypeDTOs;
    private String status;
    private Long view;
    private Float rate;
    private Long soldNumber;
    private List<MultipartFile> images;
}
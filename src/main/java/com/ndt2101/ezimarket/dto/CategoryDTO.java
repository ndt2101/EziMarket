package com.ndt2101.ezimarket.dto;

import com.ndt2101.ezimarket.base.BaseDTO;
import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class CategoryDTO extends BaseDTO {
    private String title;
    private Long parentId;
    private String image;
    private String coverImage;
}
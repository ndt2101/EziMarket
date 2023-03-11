package com.ndt2101.ezimarket.dto.GHN;

import com.ndt2101.ezimarket.model.*;
import lombok.*;

import java.util.List;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class MappedProductEntity {
    private Long id;
    private String name;
    private String description;
    private CategoryEntity category;
    private String status;
    private Long view;
    private Float rate;
    private Long soldNumber;
    private ShopEntity shop;
    private float weight;
    private float width;
    private float height;
    private float length;
    private List<MappedProductTypeEntity> productTypes;
    private List<ImageEntity> imageEntities;
    private SaleProgramEntity saleProgram;
    private List<PostEntity> posts;
    private List<CommentEntity> comments;
}

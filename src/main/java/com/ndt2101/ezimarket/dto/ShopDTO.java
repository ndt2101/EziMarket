package com.ndt2101.ezimarket.dto;

import com.ndt2101.ezimarket.base.BaseDTO;
import lombok.*;

import javax.persistence.Column;

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
}

package com.ndt2101.ezimarket.dto;

import lombok.*;

import javax.persistence.Column;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class ShopDTO {
    private String name;
    private String description;
    private Float rate;
}

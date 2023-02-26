package com.ndt2101.ezimarket.elasticsearch.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class ProductDTO {
    @JsonIgnore
    private Object _class;
    private Long id;
    private String name;

}

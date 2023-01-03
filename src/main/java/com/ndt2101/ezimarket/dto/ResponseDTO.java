package com.ndt2101.ezimarket.dto;

import lombok.*;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ResponseDTO<T> {
    private Integer status;
    private String message;
    private T data;
}

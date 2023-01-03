package com.ndt2101.ezimarket.exception;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class InvalidResponse {
//    private Integer status;
    private String field;
    private String message;
}

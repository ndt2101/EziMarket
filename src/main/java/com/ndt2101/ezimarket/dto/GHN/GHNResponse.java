package com.ndt2101.ezimarket.dto.GHN;

import lombok.*;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class GHNResponse<T> {
    private Integer code;
    private String message;
    private T data;
}

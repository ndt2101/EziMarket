package com.ndt2101.ezimarket.dto.GHN;

import lombok.*;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class OrderResult {
    private String orderCode;
    private boolean result;
    private String message;
}
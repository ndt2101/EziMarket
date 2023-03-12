package com.ndt2101.ezimarket.dto.GHN;

import lombok.*;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class CancelOrderPayload {
    private List<String> order_codes;
}

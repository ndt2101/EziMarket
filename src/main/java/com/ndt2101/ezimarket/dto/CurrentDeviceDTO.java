package com.ndt2101.ezimarket.dto;

import com.ndt2101.ezimarket.base.BaseDTO;
import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class CurrentDeviceDTO extends BaseDTO {
    private Long userId;
    private String token;   
}

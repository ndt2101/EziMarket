package com.ndt2101.ezimarket.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.Size;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class ResetUserPasswordDTO {
    @Size(min = 6, max = 15)
    private String loginName;
}

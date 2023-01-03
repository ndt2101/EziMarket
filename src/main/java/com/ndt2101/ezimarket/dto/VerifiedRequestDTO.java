package com.ndt2101.ezimarket.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
@AllArgsConstructor
public class VerifiedRequestDTO {
    @NotBlank
    private String verifiedToken;
    @NotBlank
    private String loginName;
}

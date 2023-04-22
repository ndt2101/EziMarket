package com.ndt2101.ezimarket.dto;

import lombok.*;

import javax.validation.constraints.NotBlank;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class VerifiedRequestDTO {
    @NotBlank
    private String verifiedToken;
    @NotBlank
    private String loginName;
}

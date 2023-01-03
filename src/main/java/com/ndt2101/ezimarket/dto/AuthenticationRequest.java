package com.ndt2101.ezimarket.dto;

import lombok.*;

import javax.validation.constraints.NotBlank;

@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
public class AuthenticationRequest {

    @NotBlank
    private String username;

    @NotBlank
    private String password;
}

package com.ndt2101.ezimarket.dto;

import lombok.*;

import javax.validation.constraints.Email;

@AllArgsConstructor
@Getter
@Setter
@NoArgsConstructor
public class ResendVerifiedTokenDTO {
    @Email
    private String email;
}

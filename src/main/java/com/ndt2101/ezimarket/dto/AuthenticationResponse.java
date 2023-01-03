package com.ndt2101.ezimarket.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.ndt2101.ezimarket.model.RoleEntity;
import lombok.*;

import javax.validation.constraints.Email;
import javax.validation.constraints.Min;
import javax.validation.constraints.Size;
import java.sql.Date;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class AuthenticationResponse {
    private Long id;
    private String firstName;
    private String lastName;
    private RoleEntity role;
    private String emailAddress;
    private String accessToken;
    private String avatarUrl;
}

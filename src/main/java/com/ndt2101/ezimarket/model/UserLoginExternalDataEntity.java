package com.ndt2101.ezimarket.model;

import lombok.*;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.validation.constraints.NotBlank;

@Table(name = "LoginExternalData")
@Entity
@AllArgsConstructor
@NoArgsConstructor
//@Data
@Getter
@Setter
public class UserLoginExternalDataEntity extends UserAccountEntity{

    @Column
    @NotBlank
    private String externalProviderToken;

}

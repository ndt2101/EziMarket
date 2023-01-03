package com.ndt2101.ezimarket.model;

import com.ndt2101.ezimarket.base.BaseEntity;
import lombok.*;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.validation.constraints.NotBlank;
import java.util.Set;

@Entity
@Table(name = "EmailValidationStatus")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class EmailValidationStatusEntity extends BaseEntity {

    @Column
    @NotBlank
    private String statusDescription;

    @OneToMany(mappedBy = "emailValidationStatus")
    private Set<UserLoginDataEntity> userLoginDataSet;
}

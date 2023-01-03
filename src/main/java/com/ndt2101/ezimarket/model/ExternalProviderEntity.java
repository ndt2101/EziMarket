package com.ndt2101.ezimarket.model;

import com.ndt2101.ezimarket.base.BaseEntity;
import lombok.*;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.validation.constraints.NotBlank;

@Entity
@Table(name = "ExternalProvider")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
//@Data
public class ExternalProviderEntity extends BaseEntity {

    @Column
    @NotBlank
    private String providerName;

    @Column
    @NotBlank
    private String wsEndPoint;
}

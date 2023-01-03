package com.ndt2101.ezimarket.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.ndt2101.ezimarket.base.BaseEntity;
import lombok.*;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import java.util.Set;

@Table(name = "Permission")
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
//@Data
public class PermissionEntity extends BaseEntity {
    @Column
    @NotBlank
    private String description;

    @JsonIgnore
    @ManyToMany(mappedBy = "permission", fetch = FetchType.LAZY)
    private Set<RoleEntity> role;

}

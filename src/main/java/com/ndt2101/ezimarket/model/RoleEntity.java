package com.ndt2101.ezimarket.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.ndt2101.ezimarket.base.BaseEntity;
import lombok.*;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import java.util.Set;

@Entity
@Table(name = "Role")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
//@Data
public class RoleEntity extends BaseEntity {

    @Column
    @NotBlank
    private String description;

    @JsonIgnore
    @OneToMany(mappedBy = "role", fetch = FetchType.LAZY)
    private Set<UserLoginDataEntity> userLoginData;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "granted_permission",
            joinColumns = @JoinColumn(name = "roleId"),
            inverseJoinColumns = @JoinColumn(name = "permissionId"))
    private Set<PermissionEntity> permission;
}

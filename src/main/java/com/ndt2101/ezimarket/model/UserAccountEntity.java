package com.ndt2101.ezimarket.model;

import com.ndt2101.ezimarket.base.BaseEntityForSequence;
import lombok.*;
import lombok.experimental.SuperBuilder;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.sql.Date;

@MappedSuperclass
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@SuperBuilder
public class UserAccountEntity extends BaseEntityForSequence {

    @Column
    @NotBlank
    @Size(min = 6, max = 25, message = "First name is out of character")
    private String firstName;

    @Column
    @NotBlank
    @Size(min = 6, max = 25, message = "Last name is out of character")
    private String lastName;

//    @Column
//    private Date dateOfBirth;

    @Column
    private String phone;

    @ManyToOne
    @JoinColumn(name = "role_id")
    private RoleEntity role;
}

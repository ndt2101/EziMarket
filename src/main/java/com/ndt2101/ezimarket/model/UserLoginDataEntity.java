package com.ndt2101.ezimarket.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;
import lombok.experimental.SuperBuilder;

import javax.persistence.*;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.sql.Timestamp;
import java.util.Date;

@Entity
@Table(name = "LoginData")
@NoArgsConstructor
@AllArgsConstructor
//@Data
@Getter
@Setter
@SuperBuilder
public class UserLoginDataEntity extends UserAccountEntity {
    @Column(unique = true)
    @Size(min = 6, max = 15, message = "Login name is out of character")
    private String loginName;

    @Column
    private String password;

    @Column(unique = true)
    @Email(message = "Email is incorrect")
    private String emailAddress;

    @Column
    private String confirmationToken;

    @Column
    private Date confirmationTokenGeneratedTime;

    @Column
    private String avatarUrl;

    @ManyToOne
    @JoinColumn(name = "emailValidationStatusId")
    private EmailValidationStatusEntity emailValidationStatus;
}

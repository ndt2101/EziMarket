package com.ndt2101.ezimarket.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.ndt2101.ezimarket.model.paypal.Payer;
import lombok.*;
import lombok.experimental.SuperBuilder;

import javax.persistence.*;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.sql.Timestamp;
import java.util.Date;
import java.util.List;
import java.util.Set;

@Entity
@Table(name = "LoginData")
@NoArgsConstructor
@AllArgsConstructor
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

    @OneToOne(mappedBy = "userLoginData")
    private ShopEntity shop;

    @OneToMany(mappedBy = "to", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<FollowerEntity> followers;

    @OneToOne(orphanRemoval = true)
    @JoinColumn(name = "address_id")
    private AddressEntity address;

    @ManyToMany(mappedBy = "users")
    private Set<VoucherEntity> vouchers;

    @OneToMany(mappedBy = "user", cascade = CascadeType.REMOVE, fetch = FetchType.LAZY)
    private List<CommentEntity> comments;

    @OneToMany(mappedBy = "user", cascade = CascadeType.REMOVE, fetch = FetchType.LAZY)
    private List<CommentEntity> reviews;

    @OneToOne(mappedBy = "userLoginDataEntity", fetch = FetchType.LAZY)
    private Payer payer;

    @Column
    private String currentDevice;

//    TODO: khi xóa user cũng xử lý tương tự xóa voucher để thực hiện xóa các liên kết user_voucher mà không xóa voucher
}

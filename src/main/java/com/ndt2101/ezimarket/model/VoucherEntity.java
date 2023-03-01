package com.ndt2101.ezimarket.model;

import com.ndt2101.ezimarket.base.BaseEntity;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import javax.persistence.*;
import java.util.Set;


@Entity
@Table(name = "voucher")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class VoucherEntity extends BaseEntity {
    @Column
    private Float discount;
    @Column
    private Long endTime;
    @Column
    private int quantity;
    @Column
    private Long priceCondition;
    @Column
    private int saved;
    @ManyToOne
    @JoinColumn(name = "shop_id")
    private ShopEntity shop;
    @ManyToMany
    @JoinTable(name = "user_voucher", joinColumns = @JoinColumn(name = "voucher_id"), inverseJoinColumns = @JoinColumn(name = "user_id"))
    private Set<UserLoginDataEntity> users;

//    TODO: order
}

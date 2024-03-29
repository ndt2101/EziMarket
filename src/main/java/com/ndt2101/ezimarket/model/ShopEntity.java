package com.ndt2101.ezimarket.model;

import com.ndt2101.ezimarket.base.BaseEntity;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.util.List;

@Entity
@Table(name = "Shop")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class ShopEntity extends BaseEntity {

    @Column
    private String name;

    @Column
    private String description;

    @Column
    private Float rate;

    @Column(name = "ghn_store_id")
    private Integer GHNStoreId;

    @OneToOne()
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    private UserLoginDataEntity userLoginData;

    @OneToMany(mappedBy = "shop", cascade = CascadeType.REMOVE, orphanRemoval = true)
    private List<ProductEntity> productEntities;

    @OneToMany(mappedBy = "shop", cascade = CascadeType.REMOVE, orphanRemoval = true)
    private List<VoucherEntity> vouchers;

    @OneToMany(mappedBy = "shop", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<PostEntity> post;

    @OneToMany(mappedBy = "from", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<FollowerEntity> followings;

    @OneToMany(mappedBy = "shop", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<OrderEntity> orders;

}

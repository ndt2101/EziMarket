package com.ndt2101.ezimarket.model;

import com.ndt2101.ezimarket.base.BaseEntity;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.util.List;
import java.util.Set;

@Table(name = "Post")
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class PostEntity extends BaseEntity {
    @ManyToOne
    @JoinColumn(name = "shop_id")
    private ShopEntity shop;
    @Column
    private String postContentText;
    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "image_id")
    private ImageEntity image;
    @ManyToOne
    @JoinColumn(name = "product_id")
    private ProductEntity product;
    @ManyToOne
    @JoinColumn(name = "voucher_id")
    private VoucherEntity voucher;

    @ManyToMany
    @JoinTable(name = "user_like", joinColumns = @JoinColumn(name = "post_id"), inverseJoinColumns = @JoinColumn(name = "user_id"))
    private Set<UserLoginDataEntity> likes;

    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<CommentEntity> comments;
}

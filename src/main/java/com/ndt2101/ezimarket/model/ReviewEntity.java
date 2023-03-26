package com.ndt2101.ezimarket.model;

import com.ndt2101.ezimarket.base.BaseEntity;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

@Table(name = "review")
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class ReviewEntity extends BaseEntity {

    @ManyToOne
    @JoinColumn(name = "product_id")
    private ProductEntity product;

    @Column
    private String type;

    @Column
    private Float rate;

    @Column
    private String content;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private UserLoginDataEntity user;


}

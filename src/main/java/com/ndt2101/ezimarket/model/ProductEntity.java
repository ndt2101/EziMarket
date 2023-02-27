package com.ndt2101.ezimarket.model;

import com.ndt2101.ezimarket.base.BaseEntity;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import java.util.List;

@Table(name = "Product")
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class ProductEntity extends BaseEntity {
    @Column
    @NotBlank
    private String name;
    @Column
    @NotBlank
    private String description;
    @ManyToOne
    @JoinColumn(name = "category_id")
    private CategoryEntity category;
    @Column
    @NotBlank
    private String status;
    @Column
    private Long view;
    @Column
    private Float rate;
    @Column
    private Long soldNumber;
    @ManyToOne
    @JoinColumn(name = "shop_id")
    private ShopEntity shop;
    @Column
    private float weight;
    @Column
    private float width;
    @Column
    private float height;
    @Column
    private float length;
    @OneToMany(mappedBy = "product", cascade = {CascadeType.PERSIST,CascadeType.REMOVE}, fetch = FetchType.EAGER)
    private List<ProductTypeEntity> productTypes;

    @OneToMany(mappedBy = "product")
    private List<ImageEntity> imageEntities;
}

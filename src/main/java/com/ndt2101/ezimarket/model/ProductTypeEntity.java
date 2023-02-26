package com.ndt2101.ezimarket.model;

import com.ndt2101.ezimarket.base.BaseEntity;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;

@Table(name = "ProductType")
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class ProductTypeEntity extends BaseEntity {
    @Column
    private String type = "Nguyên bản";
    @Column
    @Min(0)
    private Long price;
    @Column
    @Min(0)
    private Long quantity;
    @Column
    private Long discountPrice = price;

    @ManyToOne()
    @JoinColumn(name = "product_id")
    private ProductEntity product;
}
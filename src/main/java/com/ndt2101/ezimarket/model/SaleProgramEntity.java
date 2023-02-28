package com.ndt2101.ezimarket.model;

import com.ndt2101.ezimarket.base.BaseEntity;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.sql.Date;
import java.util.List;

@Entity
@Table(name = "sale_program")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class SaleProgramEntity extends BaseEntity {
    @Column
    private String name;
    @Column
    private Long endTime;
    @Column
    private Float discount;
    @ManyToOne
    @JoinColumn(name = "shop_id")
    private ShopEntity shop;
    @OneToMany(mappedBy = "saleProgram", cascade = CascadeType.PERSIST)
    private List<ProductEntity> products;
}

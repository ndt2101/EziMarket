package com.ndt2101.ezimarket.model;

import com.ndt2101.ezimarket.base.BaseEntity;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "product_report")
public class ProductReportEntity extends BaseEntity {

    @OneToOne
    @JoinColumn(name = "product_id")
    private ProductEntity product;
}

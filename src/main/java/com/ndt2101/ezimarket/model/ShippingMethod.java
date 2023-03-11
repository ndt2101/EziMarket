package com.ndt2101.ezimarket.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.ndt2101.ezimarket.base.BaseEntity;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.util.Date;
import java.util.List;

@Entity
@Table(name = "shipping_method")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class ShippingMethod extends BaseEntity {
    @Column
    private String name = "Giao hàng nhanh";
    @OneToOne(mappedBy = "shippingMethod", fetch = FetchType.LAZY)
    private OrderEntity order;
    @Column
    private Long price;
    @Column
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private Date receivedDay;
}

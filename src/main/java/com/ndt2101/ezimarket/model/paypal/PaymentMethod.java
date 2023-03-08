package com.ndt2101.ezimarket.model.paypal;
import com.ndt2101.ezimarket.base.BaseEntity;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name = "payment_method")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class PaymentMethod extends BaseEntity {
    @Column
    private String name;
}
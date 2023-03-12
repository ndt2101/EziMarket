package com.ndt2101.ezimarket.model.paypal;

import com.ndt2101.ezimarket.base.BaseEntity;
import com.ndt2101.ezimarket.model.OrderEntity;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Table(name = "transaction")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class Transaction extends BaseEntity {
    @Column
    private Long amount;
    @Column
    private String currency;
    @OneToOne(mappedBy = "transaction")
    private Payment payment;
    @Column
    private String authorizationId;
    @Column
    private String captureId;

    @OneToOne(mappedBy = "transaction")
    private Refund refund;
}

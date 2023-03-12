package com.ndt2101.ezimarket.model.paypal;

import com.ndt2101.ezimarket.base.BaseEntity;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Table(name = "refund")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class Refund extends BaseEntity {
    @Column
    private Long amount;
    @Column
    private String currency;
    @Column
    private String reason;
    @OneToOne
    @JoinColumn(name = "transaction_id")
    private Transaction transaction;
}

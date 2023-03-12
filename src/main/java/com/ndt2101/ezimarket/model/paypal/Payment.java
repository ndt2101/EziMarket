package com.ndt2101.ezimarket.model.paypal;

import com.ndt2101.ezimarket.model.OrderEntity;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.util.List;

@Table(name = "payment")
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class Payment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private String id;
    @Column
    private String creatTime;

    @Column
    private String updateTime;

    @Column
    private String state;

    @Column
    private String intent;

    @ManyToOne
    @JoinColumn(name = "from_payer_id")
    private Payer from;

    @ManyToOne
    @JoinColumn(name = "to_payer_id")
    private Payer to;

    @OneToOne
    @JoinColumn(name = "transaction_id")
    private Transaction transaction;

    @OneToOne
    @JoinColumn(name = "order_id")
    private OrderEntity order;
}

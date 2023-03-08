package com.ndt2101.ezimarket.model.paypal;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.util.List;

@Entity
@Table(name = "payer_info")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class Payer {
    @Id
    private String payerId;
    @Column
    private String email;
    @Column
    private String firstName;
    @Column
    private String lastName;
    @OneToMany(mappedBy = "from")
    private List<Payment> pays;
    @OneToMany(mappedBy = "to")
    private List<Payment> receives;
}

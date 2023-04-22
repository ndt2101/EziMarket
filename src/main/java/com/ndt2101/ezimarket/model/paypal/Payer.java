//package com.ndt2101.ezimarket.model.paypal;
//
//import com.ndt2101.ezimarket.model.UserLoginDataEntity;
//import lombok.AllArgsConstructor;
//import lombok.Getter;
//import lombok.NoArgsConstructor;
//import lombok.Setter;
//
//import javax.persistence.*;
//import java.util.List;
//
//@Entity
//@Table(name = "payer_info")
//@AllArgsConstructor
//@NoArgsConstructor
//@Getter
//@Setter
//public class Payer {
//    @Id
//    @GeneratedValue(strategy = GenerationType.IDENTITY)
//    private String payerId;
//    @Column
//    private String email;
//    @Column
//    private String firstName;
//    @Column
//    private String lastName;
//    @OneToMany(mappedBy = "from", fetch = FetchType.LAZY)
//    private List<Payment> pays;
//    @OneToMany(mappedBy = "to", fetch = FetchType.LAZY)
//    private List<Payment> receives;
//    @OneToOne(fetch = FetchType.LAZY)
//    @JoinColumn(name = "user_id")
//    private UserLoginDataEntity userLoginDataEntity;
//}

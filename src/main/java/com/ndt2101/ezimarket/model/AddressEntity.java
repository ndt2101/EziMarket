package com.ndt2101.ezimarket.model;

import com.ndt2101.ezimarket.base.BaseEntity;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import javax.persistence.*;

@Entity
@Table(name = "detail_address")
@NoArgsConstructor
@AllArgsConstructor
//@Data
@Getter
@Setter
public class AddressEntity extends BaseEntity {
    @Column()
    private String detailAddress;

    @Column()
    private String name;

    @Column String phone;

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "province_id")
    private ProvinceEntity province;

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "district_id")
    private DistrictEntity district;

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "ward_id")
    private WardEntity ward;

    @OneToOne(mappedBy = "address", cascade = CascadeType.ALL)
    private UserLoginDataEntity userLoginData;
}

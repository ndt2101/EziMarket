package com.ndt2101.ezimarket.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.util.List;

@Entity
@Table(name = "provinces")
@NoArgsConstructor
@AllArgsConstructor
//@Data
@Getter
@Setter
@SuperBuilder
public class ProvinceEntity {
    @Id
    @Column
    private Long ProvinceID;
    @Column
    private String ProvinceName;
    @Column
    private int CountryID;
    @Column
    private String Code;
    @Column
    private int IsEnable;
    @Column
    private int RegionID;
    @Column
    private boolean CanUpdateCOD;
    @Column
    private int Status;

    @OneToMany(mappedBy = "province")
    private List<AddressEntity> addressEntities;
}

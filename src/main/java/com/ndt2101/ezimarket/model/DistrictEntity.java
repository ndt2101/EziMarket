package com.ndt2101.ezimarket.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import javax.persistence.*;
import java.util.List;

@Entity
@Table(name = "districts")
@NoArgsConstructor
@AllArgsConstructor
//@Data
@Getter
@Setter
@SuperBuilder
public class DistrictEntity {
    @Id
    @Column
    private Long DistrictID;
    @Column
    private Long ProvinceID;
    @Column
    private String DistrictName;
    @Column
    private String Code;
    @Column
    private int Type;
    @Column
    private int SupportType;
    @Column
    private int IsEnable;
    @Column
    private boolean CanUpdateCOD;
    @Column
    private int Status;

    @OneToMany(mappedBy = "district")
    private List<AddressEntity> addressEntities;
}

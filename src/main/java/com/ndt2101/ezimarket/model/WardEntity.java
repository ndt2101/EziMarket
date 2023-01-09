package com.ndt2101.ezimarket.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import javax.persistence.*;
import java.util.List;

@Entity
@Table(name = "wards")
@NoArgsConstructor
@AllArgsConstructor
//@Data
@Getter
@Setter
@SuperBuilder
public class WardEntity {
    @Id
    @Column
    private String WardCode;
    @Column
    private Long DistrictID;
    @Column
    private String WardName;
    @Column
    private boolean CanUpdateCOD;
    @Column
    private int SupportType;
    @Column
    private int Status;
    @OneToMany(mappedBy = "ward")
    private List<AddressEntity> addressEntities;
}

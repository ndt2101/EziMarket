package com.ndt2101.ezimarket.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import javax.persistence.Column;
import javax.persistence.Id;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class DistrictDTO {
    @JsonProperty("DistrictID")
    private Long DistrictID;
    @JsonProperty("ProvinceID")
    private Long ProvinceID;
    @JsonProperty("DistrictName")
    private String DistrictName;
    @JsonProperty("Code")
    private String Code;
    @JsonProperty("Type")
    private int Type;
    @JsonProperty("SupportType")
    private int SupportType;
    @JsonProperty("IsEnable")
    private int IsEnable;
    @JsonProperty("CanUpdateCOD")
    private boolean CanUpdateCOD;
    @JsonProperty("Status")
    private int Status;
}

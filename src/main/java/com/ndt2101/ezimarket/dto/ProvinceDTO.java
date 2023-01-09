package com.ndt2101.ezimarket.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class ProvinceDTO {
    @JsonProperty("ProvinceID")
    private Long ProvinceID;
    @JsonProperty("ProvinceName")
    private String ProvinceName;
    @JsonProperty("CountryID")
    private int CountryID;
    @JsonProperty("Code")
    private String Code;
    @JsonProperty("IsEnable")
    private int IsEnable;
    @JsonProperty("RegionID")
    private int RegionID;
    @JsonProperty("CanUpdateCOD")
    private boolean CanUpdateCOD;
    @JsonProperty("Status")
    private int Status;
}

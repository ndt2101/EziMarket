package com.ndt2101.ezimarket.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import javax.persistence.Column;
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class WardDTO {
    @JsonProperty("WardCode")
    private String WardCode;
    @JsonProperty("DistrictID")
    private Long DistrictID;
    @JsonProperty("WardName")
    private String WardName;
    @JsonProperty("CanUpdateCOD")
    private boolean CanUpdateCOD;
    @JsonProperty("SupportType")
    private int SupportType;
    @JsonProperty("Status")
    private int Status;
}

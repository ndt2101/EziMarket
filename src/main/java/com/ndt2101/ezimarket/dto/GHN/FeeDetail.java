package com.ndt2101.ezimarket.dto.GHN;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class FeeDetail {
    private int main_service;
    private int insurance;
    private int cod_fee;
    private int station_do;
    private int station_pu;
    @JsonProperty(value ="return")
    private int returnNum;
    private int r2s;
    private int coupon;
    private int document_return;
    private int double_check;
    private int pick_remote_areas_fee;
    private int deliver_remote_areas_fee;
    private int cod_failed_fee;

    // Constructor, getters, and setters
}
package com.ndt2101.ezimarket.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.ndt2101.ezimarket.base.BaseDTO;
import com.ndt2101.ezimarket.model.WardEntity;
import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class AddressDTO extends BaseDTO {
    private String phone;
    private String name;
    private String detailAddress;
    @JsonProperty("province")
    private ProvinceDTO province;
    @JsonProperty("district")
    private DistrictDTO district;
    @JsonProperty("ward")
    private WardDTO ward;

}

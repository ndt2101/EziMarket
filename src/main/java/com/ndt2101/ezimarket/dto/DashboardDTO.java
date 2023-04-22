package com.ndt2101.ezimarket.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;
import java.util.Map;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class DashboardDTO {
    private List<Long> orderCount;
    private List<Map<String, Long>> orderViaMonth;
    private List<Map<String, Long>>  incomeViaMonth;
}

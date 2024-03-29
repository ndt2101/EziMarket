package com.ndt2101.ezimarket.dto.pagination;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class Pagination {
    private Integer page;
    private Integer perPage;
    private Integer lastPage;
    private Long total;
}

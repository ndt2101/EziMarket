package com.ndt2101.ezimarket.service;

import com.ndt2101.ezimarket.dto.SaleProgramDTO;

public interface SaleProgramService {
    String create(SaleProgramDTO saleProgramDTO);
    String update(SaleProgramDTO saleProgramDTO, Long id);

    String delete(Long id);
}

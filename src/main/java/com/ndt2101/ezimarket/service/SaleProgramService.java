package com.ndt2101.ezimarket.service;

import com.ndt2101.ezimarket.dto.SaleProgramDTO;
import com.ndt2101.ezimarket.dto.pagination.PaginateDTO;
import com.ndt2101.ezimarket.dto.pagination.PaginationDTO;
import com.ndt2101.ezimarket.model.SaleProgramEntity;
import com.ndt2101.ezimarket.specification.GenericSpecification;

import java.util.List;

public interface SaleProgramService {
    String create(SaleProgramDTO saleProgramDTO);
    String update(SaleProgramDTO saleProgramDTO, Long id);
    SaleProgramDTO getById(Long id);
    String delete(Long id);
    PaginateDTO<SaleProgramDTO> getSalePrograms(int page, int perPage);
}

package com.ndt2101.ezimarket.service;

import com.ndt2101.ezimarket.dto.VoucherDTO;
import com.ndt2101.ezimarket.dto.pagination.PaginateDTO;
import com.ndt2101.ezimarket.model.VoucherEntity;
import com.ndt2101.ezimarket.specification.GenericSpecification;

public interface VoucherService {
    VoucherDTO create(VoucherDTO voucherDTO);
    VoucherDTO update(VoucherDTO voucherDTO, Long id);
    String delete(Long id);
    VoucherDTO getDetail(Long id);
    PaginateDTO<VoucherDTO> getVouchers(int page, int perPage, GenericSpecification<VoucherEntity> specification);
    VoucherDTO saveVoucherFromPost(Long userId, Long voucherId);
}

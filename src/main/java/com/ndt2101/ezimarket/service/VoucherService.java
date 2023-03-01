package com.ndt2101.ezimarket.service;

import com.ndt2101.ezimarket.dto.VoucherDTO;

public interface VoucherService {
    VoucherDTO create(VoucherDTO voucherDTO);
    VoucherDTO update(VoucherDTO voucherDTO, Long id);
    String delete(Long id);
}

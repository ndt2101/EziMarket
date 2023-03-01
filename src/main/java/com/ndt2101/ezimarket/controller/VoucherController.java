package com.ndt2101.ezimarket.controller;

import com.ndt2101.ezimarket.base.BaseController;
import com.ndt2101.ezimarket.dto.VoucherDTO;
import com.ndt2101.ezimarket.service.VoucherService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/voucher/")
public class VoucherController extends BaseController<Object> {
    @Autowired
    private VoucherService voucherService;

    @PostMapping
    public ResponseEntity<?> create(@RequestBody VoucherDTO voucherDTO) {
        return successfulResponse(voucherService.create(voucherDTO));
    }

    @PutMapping("{id}")
    public ResponseEntity<?> create(@PathVariable(name = "id") Long voucherId, @RequestBody VoucherDTO voucherDTO) {
        return successfulResponse(voucherService.update(voucherDTO, voucherId));
    }

    @DeleteMapping("{id}")
    public ResponseEntity<?> create(@PathVariable(name = "id") Long voucherId) {
        return successfulResponse(voucherService.delete(voucherId));
    }
}

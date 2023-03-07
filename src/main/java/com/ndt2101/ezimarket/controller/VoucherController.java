package com.ndt2101.ezimarket.controller;

import com.ndt2101.ezimarket.base.BaseController;
import com.ndt2101.ezimarket.dto.SaleProgramDTO;
import com.ndt2101.ezimarket.dto.VoucherDTO;
import com.ndt2101.ezimarket.dto.pagination.PaginateDTO;
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
    public ResponseEntity<?> update(@PathVariable(name = "id") Long voucherId, @RequestBody VoucherDTO voucherDTO) {
        return successfulResponse(voucherService.update(voucherDTO, voucherId));
    }

    @DeleteMapping("{id}")
    public ResponseEntity<?> delete(@PathVariable(name = "id") Long voucherId) {
        return successfulResponse(voucherService.delete(voucherId));
    }

    @GetMapping("{id}")
    public ResponseEntity<?> getDetail(@PathVariable(name = "id") Long id) {
        return successfulResponse(voucherService.getDetail(id));
    }

    @GetMapping()
    public ResponseEntity<?> getSaleProgram(
            @RequestParam(name = "page", required = false) Integer page,
            @RequestParam(name = "perPage", required = false) Integer perPage) {
        PaginateDTO<VoucherDTO> voucherDTOPaginateDTO = voucherService.getVouchers(page, perPage);
        return this.resPagination(voucherDTOPaginateDTO);
    }

    @PutMapping("{voucherId}/{userId}")
    public ResponseEntity<?> saveForUser(@PathVariable Long voucherId, @PathVariable Long userId) {
        return this.successfulResponse(voucherService.saveVoucherFromPost(userId, voucherId));
    }
}

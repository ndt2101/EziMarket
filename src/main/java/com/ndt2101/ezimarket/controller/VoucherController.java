package com.ndt2101.ezimarket.controller;

import com.ndt2101.ezimarket.base.BaseController;
import com.ndt2101.ezimarket.dto.SaleProgramDTO;
import com.ndt2101.ezimarket.dto.VoucherDTO;
import com.ndt2101.ezimarket.dto.pagination.PaginateDTO;
import com.ndt2101.ezimarket.model.VoucherEntity;
import com.ndt2101.ezimarket.service.VoucherService;
import com.ndt2101.ezimarket.specification.GenericSpecification;
import com.ndt2101.ezimarket.specification.JoinCriteria;
import com.ndt2101.ezimarket.specification.SearchOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.persistence.criteria.JoinType;

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
    public ResponseEntity<?> getVouchers(
            @RequestParam(name = "shopId", required = false) Long shopId,
            @RequestParam(name = "userId", required = false) Long userId,
            @RequestParam(name = "page", required = false) Integer page,
            @RequestParam(name = "perPage", required = false) Integer perPage) {
        GenericSpecification<VoucherEntity> specification = new GenericSpecification<VoucherEntity>();
        if (shopId != null) {
            specification.buildJoin(new JoinCriteria(SearchOperation.EQUAL, "shop", "id", shopId, JoinType.INNER));
        }
        if (userId != null) {
            specification.buildJoin(new JoinCriteria(SearchOperation.EQUAL, "users", "id", userId, JoinType.INNER));
        }
        PaginateDTO<VoucherDTO> voucherDTOPaginateDTO = voucherService.getVouchers(page, perPage, specification);
        return this.resPagination(voucherDTOPaginateDTO);
    }

    @PutMapping("{voucherId}/{userId}")
    public ResponseEntity<?> saveForUser(@PathVariable Long voucherId, @PathVariable Long userId) {
        return this.successfulResponse(voucherService.saveVoucherFromPost(userId, voucherId));
    }
}

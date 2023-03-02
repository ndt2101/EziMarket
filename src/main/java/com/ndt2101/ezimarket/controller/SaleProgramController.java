package com.ndt2101.ezimarket.controller;

import com.ndt2101.ezimarket.base.BaseController;
import com.ndt2101.ezimarket.dto.SaleProgramDTO;
import com.ndt2101.ezimarket.dto.pagination.PaginateDTO;
import com.ndt2101.ezimarket.dto.pagination.PaginationDTO;
import com.ndt2101.ezimarket.model.SaleProgramEntity;
import com.ndt2101.ezimarket.service.SaleProgramService;
import com.ndt2101.ezimarket.specification.GenericSpecification;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@RestController
@RequestMapping("/api/sale-program/")
public class SaleProgramController extends BaseController<Object> {
    @Autowired
    private SaleProgramService saleProgramService;

    @PostMapping
    public ResponseEntity<?> create(@RequestBody SaleProgramDTO saleProgramDTO) {
        return successfulResponse(saleProgramService.create(saleProgramDTO));
    }

    @PutMapping("{id}")
    public ResponseEntity<?> update(@PathVariable(name = "id") Long id, @RequestBody SaleProgramDTO saleProgramDTO) {
        return successfulResponse(saleProgramService.update(saleProgramDTO, id));
    }

    @DeleteMapping("{id}")
    public ResponseEntity<?> delete(@PathVariable(name = "id") Long saleProgramId) {
        return successfulResponse(saleProgramService.delete(saleProgramId));
    }

    @GetMapping("{id}")
    public ResponseEntity<?> getDetail(@PathVariable(name = "id") Long id) {
        return successfulResponse(saleProgramService.getById(id));
    }

    @GetMapping()
    public ResponseEntity<?> getSaleProgram(
            @RequestParam(name = "page", required = false) Integer page,
            @RequestParam(name = "perPage", required = false) Integer perPage) {
        PaginateDTO<SaleProgramDTO> saleProgramEntities = saleProgramService.getSalePrograms(page, perPage);
        return this.resPagination(saleProgramEntities);
    }
}

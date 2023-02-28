package com.ndt2101.ezimarket.controller;

import com.ndt2101.ezimarket.base.BaseController;
import com.ndt2101.ezimarket.dto.SaleProgramDTO;
import com.ndt2101.ezimarket.service.SaleProgramService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/sale-program/")
public class SaleProgramController extends BaseController<Object> {
    @Autowired
    private SaleProgramService saleProgramService;

    @PostMapping
    public ResponseEntity<?> create(@RequestBody SaleProgramDTO saleProgramDTO) {
        return successfulResponse(saleProgramService.create(saleProgramDTO));
    }
}

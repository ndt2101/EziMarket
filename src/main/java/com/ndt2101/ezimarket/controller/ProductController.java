package com.ndt2101.ezimarket.controller;

import com.ndt2101.ezimarket.base.BaseController;
import com.ndt2101.ezimarket.dto.product.ProductPayLoadDTO;
import com.ndt2101.ezimarket.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@RestController
@RequestMapping("/api/product/")
public class ProductController extends BaseController<Object> {

    @Autowired
    private ProductService productService;

    @PostMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> createProduct(@ModelAttribute ProductPayLoadDTO productPayLoad) {
        return successfulResponse(productService.create(productPayLoad));
    }

    @PutMapping("{id}")
    public ResponseEntity<?> updateProduct(@ModelAttribute ProductPayLoadDTO productPayLoad, @PathVariable Long id) {
        return successfulResponse(productService.update(productPayLoad, id));
    }
}
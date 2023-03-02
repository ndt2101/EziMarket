package com.ndt2101.ezimarket.controller;

import com.ndt2101.ezimarket.base.BaseController;
import com.ndt2101.ezimarket.dto.pagination.PaginateDTO;
import com.ndt2101.ezimarket.dto.product.ProductPayLoadDTO;
import com.ndt2101.ezimarket.dto.product.ProductResponseDTO;
import com.ndt2101.ezimarket.model.ProductEntity;
import com.ndt2101.ezimarket.service.ProductService;
import com.ndt2101.ezimarket.specification.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.persistence.criteria.JoinType;
import javax.servlet.http.HttpServletRequest;
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

    @DeleteMapping("{id}")
    public ResponseEntity<?> deleteProduct(@PathVariable(name = "id") Long productId) {
        return successfulResponse(productService.delete(productId));
    }

    @GetMapping("{id}")
    public ResponseEntity<?> getProductDetail(@PathVariable(name = "id") Long productId) {
        return successfulResponse(productService.getProductDetail(productId));
    }

    @GetMapping("shop/{id}")
    public ResponseEntity<?> getShopProduct(
            @PathVariable(name = "id") Long shopId,
            @RequestParam(name = "page", required = false) Integer page,
            @RequestParam(name = "perPage", required = false) Integer perPage,
            HttpServletRequest request) {
        GenericSpecification<ProductEntity> specification = new GenericSpecification<ProductEntity>().getBasicQuery(request);
        specification.buildJoin(new JoinCriteria(SearchOperation.EQUAL, "shop", "id", shopId, JoinType.LEFT));
        specification.buildSort("name", SortType.ASC);
        PaginateDTO<ProductResponseDTO> productResponseDTOPaginateDTO = productService.getList(page, perPage, specification);
        return this.resPagination(productResponseDTOPaginateDTO);
    }
}
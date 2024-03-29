package com.ndt2101.ezimarket.controller;

import com.ndt2101.ezimarket.base.BaseController;
import com.ndt2101.ezimarket.dto.pagination.PaginateDTO;
import com.ndt2101.ezimarket.dto.product.ProductPayLoadDTO;
import com.ndt2101.ezimarket.dto.product.ProductResponseDTO;
import com.ndt2101.ezimarket.elasticsearch.dto.ProductDTO;
import com.ndt2101.ezimarket.exception.NotFoundException;
import com.ndt2101.ezimarket.model.ProductEntity;
import com.ndt2101.ezimarket.repository.ProductRepository;
import com.ndt2101.ezimarket.service.ProductService;
import com.ndt2101.ezimarket.specification.*;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.persistence.criteria.JoinType;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/product/")
public class ProductController extends BaseController<Object> {

    @Autowired
    private ProductService productService;
    @Autowired
    private ProductRepository productRepository;

    @PostMapping()
    public ResponseEntity<?> createProduct(@RequestBody ProductPayLoadDTO productPayLoad) {
        return successfulResponse(productService.create(productPayLoad));
    }

    @PutMapping("{id}")
    public ResponseEntity<?> updateProduct(@RequestBody ProductPayLoadDTO productPayLoad, @PathVariable Long id) {
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

    @GetMapping
    public ResponseEntity<?> getProducts(
            @RequestParam(name = "name", required = false) String name,
            @RequestParam(name = "price", required = false) String priceRange,
            @RequestParam(name = "category", required = false) Long categoryId,
            @RequestParam(name = "saleProgram", required = false) Long saleProgram,
            @RequestParam(name = "page", required = false) Integer page,
            @RequestParam(name = "perPage", required = false) Integer perPage,
            HttpServletRequest request) throws IOException {
        GenericSpecification<ProductEntity> specification = new GenericSpecification<ProductEntity>().getBasicQuery(request);
        specification.buildSort("view", SortType.DESC);

        if (priceRange != null) {
            JoinCriteria joinCriteria = new JoinCriteria(SearchOperation.BETWEEN, "productTypes", "price", priceRange, JoinType.LEFT);
            specification.buildJoin(joinCriteria);
        }

        if (categoryId != null) {
            JoinCriteria joinCriteria = new JoinCriteria(SearchOperation.EQUAL, "category", "id", categoryId, JoinType.LEFT);
            specification.buildJoin(joinCriteria);
        }

        if (saleProgram != null) {
            JoinCriteria joinCriteria = new JoinCriteria(SearchOperation.GREATER_THAN, "saleProgram", "discount", saleProgram.floatValue()/100, JoinType.INNER);
            specification.buildJoin(joinCriteria);
            specification.buildSort("updatedTime", SortType.DESC);
        }

        if (name != null) {
            List<ProductDTO> productDTOs = productService.fuzzySearch(name);
            List<Long> ids = productDTOs.stream().map(ProductDTO::getId).toList();
            if (ids.size() > 0) {
                specification.add(new SearchCriteria(
                        "id",
                        ids,
                        SearchOperation.IN
                ));
            } else {
                throw new NotFoundException("Product not found");
            }
        }
        PaginateDTO<ProductResponseDTO> productResponseDTOPaginateDTO = productService.getList(page, perPage, specification);
        return this.resPagination(productResponseDTOPaginateDTO);
    }

    @GetMapping("count/shop/{id}")
    ResponseEntity<?> getShopProductTotal(@PathVariable("id") Long shopId) {
        return successfulResponse(productRepository.countProductEntitiesByShop_Id(shopId));
    }

    @PostMapping("report/{id}")
    ResponseEntity<?> report(@PathVariable("id") Long productId) {
        return successfulResponse(productService.report(productId));
    }
    @GetMapping("report")
    ResponseEntity<?> getAllReportedProduct() {
        return successfulResponse(productService.getReportedProducts());
    }

    @PutMapping("report/{id}/{status}")
    ResponseEntity<?> handleReport(@PathVariable("id") Long reportId, @PathVariable("status") String status) {
        return successfulResponse(productService.handleReport(reportId, status));
    }

    @PutMapping("view/{id}")
    ResponseEntity<?> updateView(@PathVariable("id") Long productId) {
        return successfulResponse(productService.updateView(productId));
    }
}
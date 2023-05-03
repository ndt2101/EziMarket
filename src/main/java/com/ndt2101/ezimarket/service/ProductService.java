package com.ndt2101.ezimarket.service;

import com.ndt2101.ezimarket.dto.pagination.PaginateDTO;
import com.ndt2101.ezimarket.dto.product.ProductPayLoadDTO;
import com.ndt2101.ezimarket.dto.product.ProductResponseDTO;
import com.ndt2101.ezimarket.elasticsearch.dto.ProductDTO;
import com.ndt2101.ezimarket.model.ProductEntity;
import com.ndt2101.ezimarket.model.ProductReportEntity;
import com.ndt2101.ezimarket.specification.GenericSpecification;

import java.io.IOException;
import java.util.List;

public interface ProductService {
    String create(ProductPayLoadDTO productPayLoad);
    List<ProductDTO> fuzzySearch(String value) throws IOException;

    String update(ProductPayLoadDTO productPayLoad, Long productId);

    String delete(Long productId);

    ProductResponseDTO getProductDetail(Long productId);

    PaginateDTO<ProductResponseDTO> getList(Integer page, Integer perPage, GenericSpecification<ProductEntity> specification);

    String report(Long productId);

    List<ProductResponseDTO> getReportedProducts();

    String handleReport(Long productId, String status);

    String updateView(Long productId);
}

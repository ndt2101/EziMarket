package com.ndt2101.ezimarket.service;

import com.ndt2101.ezimarket.dto.product.ProductPayLoadDTO;
import com.ndt2101.ezimarket.dto.product.ProductResponseDTO;
import com.ndt2101.ezimarket.elasticsearch.dto.ProductDTO;
import org.springframework.web.bind.annotation.ModelAttribute;

import java.io.IOException;
import java.util.List;

public interface ProductService {
    String create(ProductPayLoadDTO productPayLoad);
    List<ProductDTO> fuzzySearch(String value) throws IOException;

    String update(ProductPayLoadDTO productPayLoad, Long productId);

    String delete(Long productId);

    ProductResponseDTO getProductDetail(Long productId);
}

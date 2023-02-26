package com.ndt2101.ezimarket.elasticsearch.elasticsearchrepository;

import com.ndt2101.ezimarket.elasticsearch.model.Product;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

public interface ELSProductRepository extends ElasticsearchRepository<Product, Long> {

}


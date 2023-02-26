package com.ndt2101.ezimarket;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.elasticsearch.repository.config.EnableElasticsearchRepositories;

@SpringBootApplication
@EnableElasticsearchRepositories(basePackages = "com.ndt2101.ezimarket.elasticsearch.elasticsearchrepository")
public class EziMarketApplication {

    public static void main(String[] args) {
        SpringApplication.run(EziMarketApplication.class, args);
    }

}

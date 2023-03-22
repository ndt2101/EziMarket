package com.ndt2101.ezimarket;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.elasticsearch.repository.config.EnableElasticsearchRepositories;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

@SpringBootApplication
@EnableScheduling
@EnableWebMvc
@EnableElasticsearchRepositories(basePackages = "com.ndt2101.ezimarket.elasticsearch.elasticsearchrepository")
public class EziMarketApplication {

    public static void main(String[] args) {
        SpringApplication.run(EziMarketApplication.class, args);
    }

}

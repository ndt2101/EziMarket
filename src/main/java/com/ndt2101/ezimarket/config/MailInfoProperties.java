package com.ndt2101.ezimarket.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties("spring.mail")
@Getter
@Setter
public class MailInfoProperties {
    private String username;
    private String password;
}

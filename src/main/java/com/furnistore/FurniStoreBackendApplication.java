package com.furnistore;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;

@SpringBootApplication
@EnableMethodSecurity(proxyTargetClass = true) // <--- Add this parameter
public class FurniStoreBackendApplication {
    public static void main(String[] args) {
        SpringApplication.run(FurniStoreBackendApplication.class, args);
    }
}
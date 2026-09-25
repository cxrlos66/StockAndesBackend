package com.example.StockAndesBackend.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;

@Configuration
public class OpenApiConfig {
    @Bean
    public OpenAPI inventarioApi() {
        return new OpenAPI().info(new Info()
                .title("Inventario API")
                .version("v1")
                .description("API REST de StockAndes para el control de inventarios"));
    }
}

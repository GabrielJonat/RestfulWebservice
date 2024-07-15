package com.example.demo.config;

import io.swagger.v3.oas.models.ExternalDocumentation;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI customizedOpenAPI() {
        return new OpenAPI()
                .info(new Info().title("Desafio 03 API")
                .version("v1")
                .description("Documentação da API para o Desafio 03")
                .license(new License().name("Apache 2.0").url("http://springdoc.org")))
                .externalDocs(new ExternalDocumentation()
                .description("Documentação Completa")
                .url("http://localhost:8080/v3/api-docs"));
    }
}

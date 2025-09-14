package com.teste.produto.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.Contact;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Sistema de Gerenciamento de Produtos")
                        .description("API REST para gerenciamento de produtos e categorias")
                        .version("1.0.0")
                        .contact(new Contact()
                                .name("Teste Java Junior")
                                .email("teste@exemplo.com")));
    }
}
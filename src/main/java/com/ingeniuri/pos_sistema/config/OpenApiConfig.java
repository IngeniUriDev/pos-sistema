package com.ingeniuri.pos_sistema.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("API Sistema POS")
                        .version("1.0.0")
                        .description("Documentación de la API para el Sistema de Punto de Venta. Incluye módulos de autenticación, inventario y ventas.")
                        .contact(new Contact()
                                .name("Uriel Rojas")
                                .email("urielr.g.57@gmail.com")
                                .url("https://github.com/IngeniUriDev")));
    }
}
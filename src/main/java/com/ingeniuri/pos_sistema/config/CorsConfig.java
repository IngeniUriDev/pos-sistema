package com.ingeniuri.pos_sistema.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

import java.util.List;

/**
 * Configuración de CORS (Cross-Origin Resource Sharing).
 *
 * ¿Por qué es necesario?
 * Los navegadores bloquean peticiones entre diferentes dominios/puertos
 * por seguridad. CORS le dice al backend: "Acepta peticiones desde React".
 *
 * ️ En producción, cambia el origen a tu dominio real (ej: https://miapp.com)
 */
@Configuration
public class CorsConfig {

    @Bean
    public CorsFilter corsFilter() {
        CorsConfiguration config = new CorsConfiguration();

        // Orígenes permitidos (en desarrollo, localhost:5173 es Vite)
        config.setAllowedOrigins(List.of(
                "http://localhost:5173",  // Vite dev server
                "http://localhost:3000"   // Por si uso otro puerto
        ));

        // Métodos HTTP permitidos
        config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));

        // Headers permitidos
        config.setAllowedHeaders(List.of("*"));

        // Permitir credenciales (cookies, authorization headers)
        config.setAllowCredentials(true);

        // Tiempo que el navegador puede cachear la respuesta preflight
        config.setMaxAge(3600L);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/api/**", config);

        return new CorsFilter(source);
    }
}
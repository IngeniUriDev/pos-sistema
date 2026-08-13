package com.ingeniuri.pos_sistema.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO para enviar la respuesta de autenticación (token JWT).
 * Patrón: Builder (para construir objetos de forma legible)
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AuthResponse {
    private String token;
    private String mensaje;
}
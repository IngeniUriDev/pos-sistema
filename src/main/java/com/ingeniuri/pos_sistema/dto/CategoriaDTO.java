package com.ingeniuri.pos_sistema.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * DTO para recibir y enviar datos de Categoría.
 * Patrón: DTO (Data Transfer Object)
 *
 * ¿Por qué no usar la entidad directamente?
 * - Evita exponer campos internos (como createdAt)
 * - Permite validar datos de entrada
 * - Desacopla la capa web de la capa de persistencia
 */
@Data
public class CategoriaDTO {

    private Long id; // Solo para respuestas (no se valida en creación)

    @NotBlank(message = "El nombre es obligatorio")
    @Size(min = 2, max = 50, message = "El nombre debe tener entre 2 y 50 caracteres")
    private String nombre;

    @Size(max = 255, message = "La descripción no puede exceder 255 caracteres")
    private String descripcion;
}
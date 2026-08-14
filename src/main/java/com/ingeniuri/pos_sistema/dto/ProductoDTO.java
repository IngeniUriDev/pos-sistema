package com.ingeniuri.pos_sistema.dto;

import jakarta.validation.constraints.*;
import lombok.Data;
import java.math.BigDecimal;

/**
 * DTO para recibir y enviar datos de Producto.
 */
@Data
public class ProductoDTO {

    private Long id;

    @NotBlank(message = "El nombre es obligatorio")
    @Size(min = 2, max = 100, message = "El nombre debe tener entre 2 y 100 caracteres")
    private String nombre;

    private String descripcion;

    @NotNull(message = "El precio es obligatorio")
    @DecimalMin(value = "0.01", message = "El precio debe ser mayor a 0")
    private BigDecimal precio;

    @Min(value = 0, message = "El stock no puede ser negativo")
    private Integer stock;

    @NotNull(message = "El ID de categoría es obligatorio")
    private Long categoriaId; // Usamos el ID en lugar del objeto completo

    // Campos de solo lectura (para respuestas)
    private String categoriaNombre;
    private String createdAt;
    private String updatedAt;
}
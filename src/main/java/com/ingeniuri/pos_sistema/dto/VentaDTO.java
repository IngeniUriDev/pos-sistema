package com.ingeniuri.pos_sistema.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import java.math.BigDecimal;
import java.util.List;

/**
 * DTO para crear una nueva venta.
 */
@Data
public class VentaDTO {

    private Long clienteId; // Opcional

    @NotNull(message = "El método de pago es obligatorio")
    private String metodoPago; // EFECTIVO, TARJETA_CREDITO, etc.

    private String referenciaPago;

    @NotEmpty(message = "La venta debe tener al menos un producto")
    @Valid
    private List<DetalleVentaDTO> productos;

    // Campos de solo lectura (respuesta)
    private Long id;
    private String fechaVenta;
    private String vendedorNombre;
    private BigDecimal subtotal;
    private BigDecimal impuesto;
    private BigDecimal total;
    private List<DetalleVentaDTO> detalles;
}
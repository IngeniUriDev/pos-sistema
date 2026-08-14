package com.ingeniuri.pos_sistema.service;

import com.ingeniuri.pos_sistema.dto.DetalleVentaDTO;
import com.ingeniuri.pos_sistema.dto.VentaDTO;
import com.ingeniuri.pos_sistema.entity.*;
import com.ingeniuri.pos_sistema.exception.BadRequestException;
import com.ingeniuri.pos_sistema.exception.ResourceNotFoundException;
import com.ingeniuri.pos_sistema.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Servicio para la lógica de negocio de Ventas.
 * Patrón: Service Layer con transacciones
 *
 * @Transactional garantiza que si algo falla, se revierte todo (rollback)
 */
@Service
@RequiredArgsConstructor
@Transactional
public class VentaService {

    private final VentaRepository ventaRepository;
    private final ProductoRepository productoRepository;
    private final ClienteRepository clienteRepository;
    private final UsuarioRepository usuarioRepository;

    private static final BigDecimal TASA_IMPUESTO = new BigDecimal("0.16"); // 16% IVA

    /**
     * Crea una nueva venta completa.
     * Valida stock, calcula totales y actualiza inventario.
     */
    public VentaDTO crearVenta(VentaDTO dto) {
        // 1. Obtener el usuario autenticado (vendedor)
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        Usuario vendedor = usuarioRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado: " + username));

        // 2. Crear la venta
        Venta venta = new Venta();
        venta.setVendedor(vendedor);
        venta.setMetodoPago(MetodoPago.valueOf(dto.getMetodoPago()));
        venta.setReferenciaPago(dto.getReferenciaPago());

        // 3. Si hay cliente, asociarlo
        if (dto.getClienteId() != null) {
            Cliente cliente = clienteRepository.findById(dto.getClienteId())
                    .orElseThrow(() -> new ResourceNotFoundException("Cliente no encontrado con ID: " + dto.getClienteId()));
            venta.setCliente(cliente);
        }

        // 4. Procesar cada producto del carrito
        List<DetalleVenta> detalles = new ArrayList<>();
        BigDecimal subtotal = BigDecimal.ZERO;

        for (DetalleVentaDTO detalleDTO : dto.getProductos()) {
            // Buscar el producto
            Producto producto = productoRepository.findById(detalleDTO.getProductoId())
                    .orElseThrow(() -> new ResourceNotFoundException(
                            "Producto no encontrado con ID: " + detalleDTO.getProductoId()));

            // Validar stock suficiente
            if (producto.getStock() < detalleDTO.getCantidad()) {
                throw new BadRequestException(
                        "Stock insuficiente para " + producto.getNombre() +
                                ". Disponible: " + producto.getStock() + ", Solicitado: " + detalleDTO.getCantidad());
            }

            // Crear el detalle
            DetalleVenta detalle = new DetalleVenta();
            detalle.setVenta(venta);
            detalle.setProducto(producto);
            detalle.setCantidad(detalleDTO.getCantidad());
            detalle.setPrecioUnitario(producto.getPrecio());
            detalle.setSubtotal(producto.getPrecio().multiply(new BigDecimal(detalleDTO.getCantidad())));

            detalles.add(detalle);
            subtotal = subtotal.add(detalle.getSubtotal());

            // Actualizar stock del producto
            producto.setStock(producto.getStock() - detalleDTO.getCantidad());
            productoRepository.save(producto);
        }

        // 5. Calcular impuesto y total
        BigDecimal impuesto = subtotal.multiply(TASA_IMPUESTO).setScale(2, RoundingMode.HALF_UP);
        BigDecimal total = subtotal.add(impuesto);

        venta.setSubtotal(subtotal);
        venta.setImpuesto(impuesto);
        venta.setTotal(total);
        venta.setDetalles(detalles);

        // 6. Guardar la venta (cascade = ALL guarda los detalles automáticamente)
        Venta ventaGuardada = ventaRepository.save(venta);

        // 7. Convertir a DTO para respuesta
        return convertirADTO(ventaGuardada);
    }

    /**
     * Obtiene todas las ventas.
     */
    @Transactional(readOnly = true)
    public List<VentaDTO> obtenerTodas() {
        return ventaRepository.findAll().stream()
                .map(this::convertirADTO)
                .toList();
    }

    /**
     * Obtiene una venta por ID.
     */
    @Transactional(readOnly = true)
    public VentaDTO obtenerPorId(Long id) {
        Venta venta = ventaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Venta no encontrada con ID: " + id));
        return convertirADTO(venta);
    }

    /**
     * Obtiene ventas del día actual.
     */
    @Transactional(readOnly = true)
    public List<VentaDTO> obtenerVentasDelDia() {
        return ventaRepository.findVentasDelDia().stream()
                .map(this::convertirADTO)
                .toList();
    }

    /**
     * Obtiene ventas por rango de fechas.
     */
    @Transactional(readOnly = true)
    public List<VentaDTO> obtenerVentasPorRango(LocalDateTime inicio, LocalDateTime fin) {
        return ventaRepository.findByFechaVentaBetween(inicio, fin).stream()
                .map(this::convertirADTO)
                .toList();
    }

    /**
     * Método auxiliar: Convierte Entidad → DTO
     */
    private VentaDTO convertirADTO(Venta venta) {
        VentaDTO dto = new VentaDTO();
        dto.setId(venta.getId());
        dto.setFechaVenta(venta.getFechaVenta().toString());
        dto.setVendedorNombre(venta.getVendedor().getNombreCompleto());
        dto.setSubtotal(venta.getSubtotal());
        dto.setImpuesto(venta.getImpuesto());
        dto.setTotal(venta.getTotal());
        dto.setMetodoPago(venta.getMetodoPago().name());
        dto.setReferenciaPago(venta.getReferenciaPago());

        if (venta.getCliente() != null) {
            dto.setClienteId(venta.getCliente().getId());
        }

        // Convertir detalles
        List<DetalleVentaDTO> detallesDTO = venta.getDetalles().stream().map(detalle -> {
            DetalleVentaDTO detalleDTO = new DetalleVentaDTO();
            detalleDTO.setProductoId(detalle.getProducto().getId());
            detalleDTO.setCantidad(detalle.getCantidad());
            return detalleDTO;
        }).toList();

        dto.setDetalles(detallesDTO);

        return dto;
    }
}
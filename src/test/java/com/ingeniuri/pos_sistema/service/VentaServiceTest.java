package com.ingeniuri.pos_sistema.service;

import com.ingeniuri.pos_sistema.dto.DetalleVentaDTO;
import com.ingeniuri.pos_sistema.dto.VentaDTO;
import com.ingeniuri.pos_sistema.entity.*;
import com.ingeniuri.pos_sistema.exception.BadRequestException;
import com.ingeniuri.pos_sistema.repository.ClienteRepository;
import com.ingeniuri.pos_sistema.repository.ProductoRepository;
import com.ingeniuri.pos_sistema.repository.UsuarioRepository;
import com.ingeniuri.pos_sistema.repository.VentaRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class VentaServiceTest {

    @Mock
    private VentaRepository ventaRepository;

    @Mock
    private ProductoRepository productoRepository;

    @Mock
    private ClienteRepository clienteRepository;

    @Mock
    private UsuarioRepository usuarioRepository;

    @Mock
    private SecurityContext securityContext;

    @Mock
    private Authentication authentication;

    @InjectMocks
    private VentaService ventaService;

    private Usuario vendedor;
    private Producto producto;
    private VentaDTO ventaDTO;

    @BeforeEach
    void setUp() {
        // Usamos lenient() porque no todos los tests (ej: obtenerTodas) usan el contexto de seguridad
        lenient().when(securityContext.getAuthentication()).thenReturn(authentication);
        lenient().when(authentication.getName()).thenReturn("admin");
        SecurityContextHolder.setContext(securityContext);

        vendedor = new Usuario();
        vendedor.setId(1L);
        vendedor.setUsername("admin");
        vendedor.setNombreCompleto("Administrador");

        producto = new Producto();
        producto.setId(1L);
        producto.setNombre("Laptop Dell");
        producto.setPrecio(new BigDecimal("1000.00"));
        producto.setStock(10);

        ventaDTO = new VentaDTO();
        ventaDTO.setMetodoPago("EFECTIVO");

        DetalleVentaDTO detalle = new DetalleVentaDTO();
        detalle.setProductoId(1L);
        detalle.setCantidad(2);
        ventaDTO.setProductos(List.of(detalle));
    }

    @Test
    @DisplayName("Debería crear una venta exitosamente con cálculo de IVA")
    void crearVenta_DeberiaRetornarVentaConTotalesCorrectos() {
        when(usuarioRepository.findByUsername("admin")).thenReturn(Optional.of(vendedor));
        when(productoRepository.findById(1L)).thenReturn(Optional.of(producto));
        when(productoRepository.save(any(Producto.class))).thenReturn(producto);

        // Simulamos que al guardar, se asigna la fecha
        when(ventaRepository.save(any(Venta.class))).thenAnswer(invocation -> {
            Venta venta = invocation.getArgument(0);
            venta.setFechaVenta(LocalDateTime.now());
            return venta;
        });

        VentaDTO resultado = ventaService.crearVenta(ventaDTO);

        assertNotNull(resultado);
        assertEquals(new BigDecimal("2000.00"), resultado.getSubtotal());
        assertEquals(new BigDecimal("320.00"), resultado.getImpuesto());
        assertEquals(new BigDecimal("2320.00"), resultado.getTotal());
        assertEquals("EFECTIVO", resultado.getMetodoPago());

        verify(productoRepository).save(argThat(p -> p.getStock() == 8));
        verify(ventaRepository, times(1)).save(any(Venta.class));
    }

    @Test
    @DisplayName("Debería lanzar excepción cuando no hay stock suficiente")
    void crearVenta_StockInsuficiente_DeberiaLanzarExcepcion() {
        producto.setStock(1);

        when(usuarioRepository.findByUsername("admin")).thenReturn(Optional.of(vendedor));
        when(productoRepository.findById(1L)).thenReturn(Optional.of(producto));

        BadRequestException exception = assertThrows(
                BadRequestException.class,
                () -> ventaService.crearVenta(ventaDTO)
        );

        assertTrue(exception.getMessage().contains("Stock insuficiente"));
        verify(ventaRepository, never()).save(any());
    }

    @Test
    @DisplayName("Debería crear venta con cliente cuando se proporciona clienteId")
    void crearVenta_ConCliente_DeberiaAsociarCliente() {
        Cliente cliente = new Cliente();
        cliente.setId(1L);
        cliente.setNombre("Juan Pérez");

        ventaDTO.setClienteId(1L);

        when(usuarioRepository.findByUsername("admin")).thenReturn(Optional.of(vendedor));
        when(clienteRepository.findById(1L)).thenReturn(Optional.of(cliente));
        when(productoRepository.findById(1L)).thenReturn(Optional.of(producto));
        when(productoRepository.save(any(Producto.class))).thenReturn(producto);

        // Simulamos que al guardar, se asigna la fecha
        when(ventaRepository.save(any(Venta.class))).thenAnswer(invocation -> {
            Venta venta = invocation.getArgument(0);
            venta.setFechaVenta(LocalDateTime.now());
            return venta;
        });

        VentaDTO resultado = ventaService.crearVenta(ventaDTO);

        assertNotNull(resultado);
        assertEquals(1L, resultado.getClienteId());

        verify(clienteRepository, times(1)).findById(1L);
    }

    @Test
    @DisplayName("Debería obtener todas las ventas")
    void obtenerTodas_DeberiaRetornarListaDeVentas() {
        Venta venta = new Venta();
        venta.setId(1L);
        venta.setTotal(new BigDecimal("2320.00"));
        venta.setVendedor(vendedor);
        venta.setFechaVenta(LocalDateTime.now());
        venta.setMetodoPago(MetodoPago.EFECTIVO); // ← ¡AGREGA ESTA LÍNEA!

        when(ventaRepository.findAll()).thenReturn(List.of(venta));

        List<VentaDTO> resultado = ventaService.obtenerTodas();

        assertNotNull(resultado);
        assertEquals(1, resultado.size());
        assertEquals(new BigDecimal("2320.00"), resultado.get(0).getTotal());
    }
}
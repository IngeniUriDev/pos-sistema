package com.ingeniuri.pos_sistema.service;

import com.ingeniuri.pos_sistema.dto.ProductoDTO;
import com.ingeniuri.pos_sistema.entity.Categoria;
import com.ingeniuri.pos_sistema.entity.Producto;
import com.ingeniuri.pos_sistema.exception.BadRequestException;
import com.ingeniuri.pos_sistema.exception.ResourceNotFoundException;
import com.ingeniuri.pos_sistema.repository.CategoriaRepository;
import com.ingeniuri.pos_sistema.repository.ProductoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Test unitario para ProductoService.
 *
 * Buenas prácticas aplicadas:
 * - @ExtendWith(MockitoExtension): Inicializa los mocks automáticamente
 * - @Mock: Crea objetos falsos de las dependencias
 * - @InjectMocks: Inyecta los mocks en el servicio a probar
 * - Cada test verifica UN solo comportamiento
 */
@ExtendWith(MockitoExtension.class)
class ProductoServiceTest {

    // Mocks: Simulamos las dependencias del servicio
    @Mock
    private ProductoRepository productoRepository;

    @Mock
    private CategoriaRepository categoriaRepository;

    // InjectMocks: Crea el servicio real e inyecta los mocks
    @InjectMocks
    private ProductoService productoService;

    // Datos de prueba reutilizables
    private Categoria categoria;
    private Producto producto;
    private ProductoDTO productoDTO;

    /**
     * Se ejecuta ANTES de cada test.
     * Inicializa los datos de prueba para evitar repetición (DRY).
     */
    @BeforeEach
    void setUp() {
        categoria = new Categoria();
        categoria.setId(1L);
        categoria.setNombre("Electrónica");

        producto = new Producto();
        producto.setId(1L);
        producto.setNombre("Laptop Dell");
        producto.setPrecio(new BigDecimal("1299.99"));
        producto.setStock(10);
        producto.setCategoria(categoria);
        producto.setCreatedAt(LocalDateTime.now());
        producto.setUpdatedAt(LocalDateTime.now());

        productoDTO = new ProductoDTO();
        productoDTO.setNombre("Laptop Dell");
        productoDTO.setPrecio(new BigDecimal("1299.99"));
        productoDTO.setStock(10);
        productoDTO.setCategoriaId(1L);
    }
    @Test
    @DisplayName("Debería crear un producto exitosamente")
    void crearProducto_DeberiaRetornarProductoDTO() {
        // ARRANGE: Configurar el comportamiento de los mocks
        when(categoriaRepository.findById(1L)).thenReturn(Optional.of(categoria));
        when(productoRepository.save(any(Producto.class))).thenReturn(producto);

        // ACT: Ejecutar el método a probar
        ProductoDTO resultado = productoService.crearProducto(productoDTO);

        // ASSERT: Verificar el resultado
        assertNotNull(resultado);
        assertEquals("Laptop Dell", resultado.getNombre());
        assertEquals(new BigDecimal("1299.99"), resultado.getPrecio());
        assertEquals(10, resultado.getStock());

        // VERIFY: Verificar que se llamaron los métodos esperados
        verify(categoriaRepository, times(1)).findById(1L);
        verify(productoRepository, times(1)).save(any(Producto.class));
    }

    @Test
    @DisplayName("Debería lanzar excepción cuando la categoría no existe")
    void crearProducto_CategoriaNoExiste_DeberiaLanzarExcepcion() {
        // ARRANGE: Simular que la categoría no existe
        when(categoriaRepository.findById(99L)).thenReturn(Optional.empty());

        productoDTO.setCategoriaId(99L);

        // ACT & ASSERT: Ejecutar y verificar que lanza excepción
        ResourceNotFoundException exception = assertThrows(
                ResourceNotFoundException.class,
                () -> productoService.crearProducto(productoDTO)
        );

        assertEquals("Categoría no encontrada con ID: 99", exception.getMessage());
        verify(productoRepository, never()).save(any()); // Nunca debería guardar
    }

    @Test
    @DisplayName("Debería obtener todos los productos")
    void obtenerTodos_DeberiaRetornarListaDeProductos() {
        // ARRANGE
        when(productoRepository.findAll()).thenReturn(List.of(producto));

        // ACT
        List<ProductoDTO> resultado = productoService.obtenerTodos();

        // ASSERT
        assertNotNull(resultado);
        assertEquals(1, resultado.size());
        assertEquals("Laptop Dell", resultado.get(0).getNombre());

        verify(productoRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("Debería obtener producto por ID")
    void obtenerPorId_ProductoExiste_DeberiaRetornarProductoDTO() {
        // ARRANGE
        when(productoRepository.findById(1L)).thenReturn(Optional.of(producto));

        // ACT
        ProductoDTO resultado = productoService.obtenerPorId(1L);

        // ASSERT
        assertNotNull(resultado);
        assertEquals(1L, resultado.getId());
        assertEquals("Laptop Dell", resultado.getNombre());

        verify(productoRepository, times(1)).findById(1L);
    }

    @Test
    @DisplayName("Debería lanzar excepción cuando el producto no existe")
    void obtenerPorId_ProductoNoExiste_DeberiaLanzarExcepcion() {
        // ARRANGE
        when(productoRepository.findById(99L)).thenReturn(Optional.empty());

        // ACT & ASSERT
        ResourceNotFoundException exception = assertThrows(
                ResourceNotFoundException.class,
                () -> productoService.obtenerPorId(99L)
        );

        assertEquals("Producto no encontrado con ID: 99", exception.getMessage());
    }

    @Test
    @DisplayName("Debería actualizar un producto existente")
    void actualizarProducto_ProductoExiste_DeberiaRetornarProductoActualizado() {
        // ARRANGE
        when(productoRepository.findById(1L)).thenReturn(Optional.of(producto));
        when(categoriaRepository.findById(1L)).thenReturn(Optional.of(categoria));
        when(productoRepository.save(any(Producto.class))).thenReturn(producto);

        productoDTO.setNombre("Laptop Dell Actualizada");
        productoDTO.setPrecio(new BigDecimal("1499.99"));

        // ACT
        ProductoDTO resultado = productoService.actualizarProducto(1L, productoDTO);

        // ASSERT
        assertNotNull(resultado);
        assertEquals("Laptop Dell Actualizada", resultado.getNombre());
        assertEquals(new BigDecimal("1499.99"), resultado.getPrecio());

        verify(productoRepository, times(1)).save(any(Producto.class));
    }

    @Test
    @DisplayName("Debería eliminar un producto existente")
    void eliminarProducto_ProductoExiste_DeberiaEliminar() {
        // ARRANGE
        when(productoRepository.existsById(1L)).thenReturn(true);
        doNothing().when(productoRepository).deleteById(1L);

        // ACT
        assertDoesNotThrow(() -> productoService.eliminarProducto(1L));

        // VERIFY
        verify(productoRepository, times(1)).deleteById(1L);
    }

    @Test
    @DisplayName("Debería lanzar excepción al eliminar producto inexistente")
    void eliminarProducto_ProductoNoExiste_DeberiaLanzarExcepcion() {
        // ARRANGE
        when(productoRepository.existsById(99L)).thenReturn(false);

        // ACT & ASSERT
        assertThrows(
                ResourceNotFoundException.class,
                () -> productoService.eliminarProducto(99L)
        );

        verify(productoRepository, never()).deleteById(any());
    }
}
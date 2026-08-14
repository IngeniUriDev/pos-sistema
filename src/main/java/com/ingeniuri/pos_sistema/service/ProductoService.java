package com.ingeniuri.pos_sistema.service;

import com.ingeniuri.pos_sistema.dto.ProductoDTO;
import com.ingeniuri.pos_sistema.entity.Categoria;
import com.ingeniuri.pos_sistema.entity.Producto;
import com.ingeniuri.pos_sistema.exception.BadRequestException;
import com.ingeniuri.pos_sistema.exception.ResourceNotFoundException;
import com.ingeniuri.pos_sistema.repository.CategoriaRepository;
import com.ingeniuri.pos_sistema.repository.ProductoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Servicio para la lógica de negocio de Productos.
 * Patrón: Service Layer
 *
 * Maneja la relación Producto-Categoría y validaciones de negocio.
 */
@Service
@RequiredArgsConstructor
@Transactional
public class ProductoService {

    private final ProductoRepository productoRepository;
    private final CategoriaRepository categoriaRepository;

    /**
     * Crea un nuevo producto.
     * Valida que la categoría exista.
     */
    public ProductoDTO crearProducto(ProductoDTO dto) {
        // 1. Validar que la categoría exista
        Categoria categoria = categoriaRepository.findById(dto.getCategoriaId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Categoría no encontrada con ID: " + dto.getCategoriaId()));

        // 2. Convertir DTO a Entidad
        Producto producto = new Producto();
        producto.setNombre(dto.getNombre());
        producto.setDescripcion(dto.getDescripcion());
        producto.setPrecio(dto.getPrecio());
        producto.setStock(dto.getStock() != null ? dto.getStock() : 0);
        producto.setCategoria(categoria);

        // 3. Guardar en BD
        Producto guardado = productoRepository.save(producto);

        // 4. Convertir a DTO para respuesta
        return convertirADTO(guardado);
    }

    /**
     * Obtiene todos los productos.
     */
    @Transactional(readOnly = true)
    public List<ProductoDTO> obtenerTodos() {
        return productoRepository.findAll().stream()
                .map(this::convertirADTO)
                .toList();
    }

    /**
     * Obtiene un producto por ID.
     */
    @Transactional(readOnly = true)
    public ProductoDTO obtenerPorId(Long id) {
        Producto producto = productoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Producto no encontrado con ID: " + id));
        return convertirADTO(producto);
    }

    /**
     * Actualiza un producto existente.
     */
    public ProductoDTO actualizarProducto(Long id, ProductoDTO dto) {
        Producto producto = productoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Producto no encontrado con ID: " + id));

        // Validar que la nueva categoría exista (si cambió)
        Categoria categoria = categoriaRepository.findById(dto.getCategoriaId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Categoría no encontrada con ID: " + dto.getCategoriaId()));

        // Actualizar campos
        producto.setNombre(dto.getNombre());
        producto.setDescripcion(dto.getDescripcion());
        producto.setPrecio(dto.getPrecio());
        producto.setStock(dto.getStock() != null ? dto.getStock() : 0);
        producto.setCategoria(categoria);

        Producto actualizado = productoRepository.save(producto);
        return convertirADTO(actualizado);
    }

    /**
     * Elimina un producto.
     */
    public void eliminarProducto(Long id) {
        if (!productoRepository.existsById(id)) {
            throw new ResourceNotFoundException("Producto no encontrado con ID: " + id);
        }
        productoRepository.deleteById(id);
    }

    /**
     * Busca productos por nombre (búsqueda parcial, ignora mayúsculas).
     */
    @Transactional(readOnly = true)
    public List<ProductoDTO> buscarPorNombre(String nombre) {
        return productoRepository.findByNombreContainingIgnoreCase(nombre).stream()
                .map(this::convertirADTO)
                .toList();
    }

    /**
     * Busca productos por categoría.
     */
    @Transactional(readOnly = true)
    public List<ProductoDTO> buscarPorCategoria(Long categoriaId) {
        return productoRepository.findByCategoriaId(categoriaId).stream()
                .map(this::convertirADTO)
                .toList();
    }

    /**
     * Obtiene productos con stock bajo (para alertas).
     */
    @Transactional(readOnly = true)
    public List<ProductoDTO> obtenerStockBajo(Integer stockMaximo) {
        return productoRepository.findByStockLessThanEqual(stockMaximo).stream()
                .map(this::convertirADTO)
                .toList();
    }

    /**
     * Método auxiliar: Convierte Entidad → DTO.
     * Incluye datos de la categoría relacionada.
     */
    private ProductoDTO convertirADTO(Producto producto) {
        ProductoDTO dto = new ProductoDTO();
        dto.setId(producto.getId());
        dto.setNombre(producto.getNombre());
        dto.setDescripcion(producto.getDescripcion());
        dto.setPrecio(producto.getPrecio());
        dto.setStock(producto.getStock());
        dto.setCategoriaId(producto.getCategoria().getId());
        dto.setCategoriaNombre(producto.getCategoria().getNombre());
        dto.setCreatedAt(producto.getCreatedAt().toString());
        dto.setUpdatedAt(producto.getUpdatedAt().toString());
        return dto;
    }
}
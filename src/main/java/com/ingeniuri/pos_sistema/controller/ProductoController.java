package com.ingeniuri.pos_sistema.controller;

import com.ingeniuri.pos_sistema.dto.ProductoDTO;
import com.ingeniuri.pos_sistema.service.ProductoService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controller para endpoints de Productos.
 * Sigue convenciones REST y está protegido por roles.
 */
@RestController
@RequestMapping("/api/productos")
@RequiredArgsConstructor
public class ProductoController {

    private final ProductoService productoService;

    /**
     * POST /api/productos - Crear nuevo producto
     * Solo ADMIN y VENDEDOR pueden crear productos
     */
    @PostMapping
    public ResponseEntity<ProductoDTO> crear(@Valid @RequestBody ProductoDTO dto) {
        ProductoDTO creado = productoService.crearProducto(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(creado);
    }

    /**
     * GET /api/productos - Obtener todos los productos
     * Autenticado (cualquier rol)
     */
    @GetMapping
    public ResponseEntity<List<ProductoDTO>> obtenerTodos() {
        List<ProductoDTO> productos = productoService.obtenerTodos();
        return ResponseEntity.ok(productos);
    }

    /**
     * GET /api/productos/{id} - Obtener producto por ID
     */
    @GetMapping("/{id}")
    public ResponseEntity<ProductoDTO> obtenerPorId(@PathVariable Long id) {
        ProductoDTO producto = productoService.obtenerPorId(id);
        return ResponseEntity.ok(producto);
    }

    /**
     * PUT /api/productos/{id} - Actualizar producto
     * Solo ADMIN y VENDEDOR
     */
    @PutMapping("/{id}")
    public ResponseEntity<ProductoDTO> actualizar(
            @PathVariable Long id,
            @Valid @RequestBody ProductoDTO dto) {
        ProductoDTO actualizado = productoService.actualizarProducto(id, dto);
        return ResponseEntity.ok(actualizado);
    }

    /**
     * DELETE /api/productos/{id} - Eliminar producto
     * Solo ADMIN
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        productoService.eliminarProducto(id);
        return ResponseEntity.noContent().build();
    }

    /**
     * GET /api/productos/buscar?nombre=xxx - Buscar por nombre
     */
    @GetMapping("/buscar")
    public ResponseEntity<List<ProductoDTO>> buscarPorNombre(@RequestParam String nombre) {
        List<ProductoDTO> productos = productoService.buscarPorNombre(nombre);
        return ResponseEntity.ok(productos);
    }

    /**
     * GET /api/productos/categoria/{categoriaId} - Buscar por categoría
     */
    @GetMapping("/categoria/{categoriaId}")
    public ResponseEntity<List<ProductoDTO>> buscarPorCategoria(@PathVariable Long categoriaId) {
        List<ProductoDTO> productos = productoService.buscarPorCategoria(categoriaId);
        return ResponseEntity.ok(productos);
    }

    /**
     * GET /api/productos/stock-bajo?maximo=10 - Productos con stock bajo
     * Solo ADMIN y VENDEDOR
     */
    @GetMapping("/stock-bajo")
    public ResponseEntity<List<ProductoDTO>> obtenerStockBajo(
            @RequestParam(defaultValue = "10") Integer maximo) {
        List<ProductoDTO> productos = productoService.obtenerStockBajo(maximo);
        return ResponseEntity.ok(productos);
    }
}
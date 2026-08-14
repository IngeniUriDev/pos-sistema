package com.ingeniuri.pos_sistema.controller;

import com.ingeniuri.pos_sistema.dto.CategoriaDTO;
import com.ingeniuri.pos_sistema.service.CategoriaService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controller para endpoints de Categorías.
 * Patrón: REST Controller
 *
 * Sigue las convenciones REST:
 * - GET: Leer
 * - POST: Crear
 * - PUT: Actualizar
 * - DELETE: Eliminar
 */
@RestController
@RequestMapping("/api/categorias")
@RequiredArgsConstructor
public class CategoriaController {

    private final CategoriaService categoriaService;

    /**
     * POST /api/categorias - Crear nueva categoría
     * Solo ADMIN puede crear categorías
     */
    @PostMapping
    public ResponseEntity<CategoriaDTO> crear(@Valid @RequestBody CategoriaDTO dto) {
        CategoriaDTO creada = categoriaService.crearCategoria(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(creada);
    }

    /**
     * GET /api/categorias - Obtener todas las categorías
     * Público (cualquier usuario autenticado)
     */
    @GetMapping
    public ResponseEntity<List<CategoriaDTO>> obtenerTodas() {
        List<CategoriaDTO> categorias = categoriaService.obtenerTodas();
        return ResponseEntity.ok(categorias);
    }

    /**
     * GET /api/categorias/{id} - Obtener categoría por ID
     */
    @GetMapping("/{id}")
    public ResponseEntity<CategoriaDTO> obtenerPorId(@PathVariable Long id) {
        CategoriaDTO categoria = categoriaService.obtenerPorId(id);
        return ResponseEntity.ok(categoria);
    }

    /**
     * PUT /api/categorias/{id} - Actualizar categoría
     * Solo ADMIN puede actualizar
     */
    @PutMapping("/{id}")
    public ResponseEntity<CategoriaDTO> actualizar(@PathVariable Long id, @Valid @RequestBody CategoriaDTO dto) {
        CategoriaDTO actualizada = categoriaService.actualizarCategoria(id, dto);
        return ResponseEntity.ok(actualizada);
    }

    /**
     * DELETE /api/categorias/{id} - Eliminar categoría
     * Solo ADMIN puede eliminar
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        categoriaService.eliminarCategoria(id);
        return ResponseEntity.noContent().build(); // 204 No Content
    }
}
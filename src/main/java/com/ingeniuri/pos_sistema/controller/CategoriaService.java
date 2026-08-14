package com.ingeniuri.pos_sistema.service;

import com.ingeniuri.pos_sistema.dto.CategoriaDTO;
import com.ingeniuri.pos_sistema.entity.Categoria;
import com.ingeniuri.pos_sistema.exception.BadRequestException;
import com.ingeniuri.pos_sistema.exception.ResourceNotFoundException;
import com.ingeniuri.pos_sistema.repository.CategoriaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Servicio para la lógica de negocio de Categorías.
 * Patrón: Service Layer
 *
 * @Transactional garantiza que todas las operaciones de BD
 * sean atómicas (todo o nada).
 */
@Service
@RequiredArgsConstructor
@Transactional
public class CategoriaService {

    private final CategoriaRepository categoriaRepository;

    /**
     * Crea una nueva categoría.
     */
    public CategoriaDTO crearCategoria(CategoriaDTO dto) {
        // Validar que no exista ya
        if (categoriaRepository.existsByNombreIgnoreCase(dto.getNombre())) {
            throw new BadRequestException("Ya existe una categoría con el nombre: " + dto.getNombre());
        }

        // Convertir DTO a Entidad
        Categoria categoria = new Categoria();
        categoria.setNombre(dto.getNombre());
        categoria.setDescripcion(dto.getDescripcion());

        // Guardar en BD
        Categoria guardada = categoriaRepository.save(categoria);

        // Convertir Entidad a DTO para respuesta
        return convertirADTO(guardada);
    }

    /**
     * Obtiene todas las categorías.
     */
    @Transactional(readOnly = true)
    public List<CategoriaDTO> obtenerTodas() {
        return categoriaRepository.findAll().stream()
                .map(this::convertirADTO)
                .toList();
    }

    /**
     * Obtiene una categoría por ID.
     */
    @Transactional(readOnly = true)
    public CategoriaDTO obtenerPorId(Long id) {
        Categoria categoria = categoriaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Categoría no encontrada con ID: " + id));
        return convertirADTO(categoria);
    }

    /**
     * Actualiza una categoría existente.
     */
    public CategoriaDTO actualizarCategoria(Long id, CategoriaDTO dto) {
        Categoria categoria = categoriaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Categoría no encontrada con ID: " + id));

        // Validar que el nuevo nombre no esté en uso por otra categoría
        if (categoriaRepository.existsByNombreIgnoreCase(dto.getNombre())) {
            Categoria existente = categoriaRepository.findByNombreIgnoreCase(dto.getNombre()).orElse(null);
            if (existente != null && !existente.getId().equals(id)) {
                throw new BadRequestException("Ya existe otra categoría con el nombre: " + dto.getNombre());
            }
        }

        categoria.setNombre(dto.getNombre());
        categoria.setDescripcion(dto.getDescripcion());

        Categoria actualizada = categoriaRepository.save(categoria);
        return convertirADTO(actualizada);
    }

    /**
     * Elimina una categoría.
     */
    public void eliminarCategoria(Long id) {
        if (!categoriaRepository.existsById(id)) {
            throw new ResourceNotFoundException("Categoría no encontrada con ID: " + id);
        }
        categoriaRepository.deleteById(id);
    }

    /**
     * Método auxiliar para convertir Entidad a DTO.
     */
    private CategoriaDTO convertirADTO(Categoria categoria) {
        CategoriaDTO dto = new CategoriaDTO();
        dto.setId(categoria.getId());
        dto.setNombre(categoria.getNombre());
        dto.setDescripcion(categoria.getDescripcion());
        return dto;
    }
}
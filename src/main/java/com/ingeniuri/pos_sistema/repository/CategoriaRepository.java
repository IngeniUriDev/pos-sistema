package com.ingeniuri.pos_sistema.repository;

import com.ingeniuri.pos_sistema.entity.Categoria;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

/**
 * Repositorio para la entidad Categoria.
 * Patrón: Repository Pattern
 *
 * Spring Data JPA implementa automáticamente los métodos CRUD.
 * Los métodos personalizados se definen por convención de nombres.
 */
@Repository
public interface CategoriaRepository extends JpaRepository<Categoria, Long> {

    // Busca una categoría por nombre (ignora mayúsculas/minúsculas)
    Optional<Categoria> findByNombreIgnoreCase(String nombre);

    // Verifica si existe una categoría con ese nombre
    boolean existsByNombreIgnoreCase(String nombre);
}
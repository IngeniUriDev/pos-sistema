package com.ingeniuri.pos_sistema.repository;

import com.ingeniuri.pos_sistema.entity.Producto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;

/**
 * Repositorio para la entidad Producto.
 * Incluye consultas personalizadas con JPQL.
 */
@Repository
public interface ProductoRepository extends JpaRepository<Producto, Long> {

    // Buscar productos por categoría
    List<Producto> findByCategoriaId(Long categoriaId);

    // Buscar productos por nombre (contiene, ignora mayúsculas)
    List<Producto> findByNombreContainingIgnoreCase(String nombre);

    // Buscar productos con stock bajo (menor o igual al valor dado)
    List<Producto> findByStockLessThanEqual(Integer stockMaximo);

    // Consulta personalizada con JPQL para buscar por nombre de categoría
    @Query("SELECT p FROM Producto p WHERE p.categoria.nombre = :categoriaNombre")
    List<Producto> findByCategoriaNombre(@Param("categoriaNombre") String categoriaNombre);
}
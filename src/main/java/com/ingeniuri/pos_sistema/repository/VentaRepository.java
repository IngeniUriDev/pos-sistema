package com.ingeniuri.pos_sistema.repository;

import com.ingeniuri.pos_sistema.entity.Venta;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface VentaRepository extends JpaRepository<Venta, Long> {

    // Buscar ventas por rango de fechas
    List<Venta> findByFechaVentaBetween(LocalDateTime inicio, LocalDateTime fin);

    // Buscar ventas por vendedor
    List<Venta> findByVendedorId(Long usuarioId);

    // Consulta NATIVA para obtener ventas del día (PostgreSQL entiende DATE() perfectamente)
    @Query(value = "SELECT * FROM ventas v WHERE DATE(v.fecha_venta) = CURRENT_DATE", nativeQuery = true)
    List<Venta> findVentasDelDia();

    // Consulta para obtener total vendido en un período
    @Query("SELECT SUM(v.total) FROM Venta v WHERE v.fechaVenta BETWEEN :inicio AND :fin")
    Double getTotalVendidoEnPeriodo(@Param("inicio") LocalDateTime inicio, @Param("fin") LocalDateTime fin);
}
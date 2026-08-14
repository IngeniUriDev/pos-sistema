package com.ingeniuri.pos_sistema.controller;

import com.ingeniuri.pos_sistema.dto.VentaDTO;
import com.ingeniuri.pos_sistema.service.VentaService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Controller para endpoints de Ventas.
 */
@RestController
@RequestMapping("/api/ventas")
@RequiredArgsConstructor
public class VentaController {

    private final VentaService ventaService;

    /**
     * POST /api/ventas - Crear nueva venta
     * Solo ADMIN y VENDEDOR
     */
    @PostMapping
    public ResponseEntity<VentaDTO> crear(@Valid @RequestBody VentaDTO dto) {
        VentaDTO creada = ventaService.crearVenta(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(creada);
    }

    /**
     * GET /api/ventas - Obtener todas las ventas
     * Solo ADMIN
     */
    @GetMapping
    public ResponseEntity<List<VentaDTO>> obtenerTodas() {
        List<VentaDTO> ventas = ventaService.obtenerTodas();
        return ResponseEntity.ok(ventas);
    }

    /**
     * GET /api/ventas/{id} - Obtener venta por ID
     */
    @GetMapping("/{id}")
    public ResponseEntity<VentaDTO> obtenerPorId(@PathVariable Long id) {
        VentaDTO venta = ventaService.obtenerPorId(id);
        return ResponseEntity.ok(venta);
    }

    /**
     * GET /api/ventas/hoy - Obtener ventas del día
     */
    @GetMapping("/hoy")
    public ResponseEntity<List<VentaDTO>> obtenerVentasDelDia() {
        List<VentaDTO> ventas = ventaService.obtenerVentasDelDia();
        return ResponseEntity.ok(ventas);
    }

    /**
     * GET /api/ventas/rango?inicio=2026-08-01T00:00:00&fin=2026-08-31T23:59:59
     * Obtener ventas por rango de fechas
     */
    @GetMapping("/rango")
    public ResponseEntity<List<VentaDTO>> obtenerVentasPorRango(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime inicio,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fin) {
        List<VentaDTO> ventas = ventaService.obtenerVentasPorRango(inicio, fin);
        return ResponseEntity.ok(ventas);
    }
}
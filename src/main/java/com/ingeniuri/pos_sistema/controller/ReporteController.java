package com.ingeniuri.pos_sistema.controller;

import com.ingeniuri.pos_sistema.service.ReporteService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Controller para endpoints de Reportes.
 */
@RestController
@RequestMapping("/api/reportes")
@RequiredArgsConstructor
@Tag(name = "Reportes", description = "Endpoints para generar reportes en Excel")
public class ReporteController {

    private final ReporteService reporteService;

    @GetMapping("/ventas/excel")
    @Operation(summary = "Descargar reporte de ventas en Excel", description = "Genera y descarga un archivo .xlsx con el historial completo de ventas.")
    public ResponseEntity<byte[]> descargarReporteVentas() {
        try {
            byte[] excelData = reporteService.generarReporteVentasExcel();

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
            headers.setContentDispositionFormData("attachment", "reporte_ventas.xlsx");

            return ResponseEntity.ok()
                    .headers(headers)
                    .body(excelData);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }
}
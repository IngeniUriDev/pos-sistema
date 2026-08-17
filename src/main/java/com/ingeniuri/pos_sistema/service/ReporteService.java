package com.ingeniuri.pos_sistema.service;

import com.ingeniuri.pos_sistema.entity.Venta;
import com.ingeniuri.pos_sistema.repository.VentaRepository;
import lombok.RequiredArgsConstructor;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * Servicio para generar reportes en Excel.
 */
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ReporteService {

    private final VentaRepository ventaRepository;
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    /**
     * Genera un archivo Excel con el historial de ventas.
     * @return Arreglo de bytes del archivo Excel
     */
    public byte[] generarReporteVentasExcel() throws IOException {
        List<Venta> ventas = ventaRepository.findAll();

        // 1. Crear un nuevo libro de Excel
        try (Workbook workbook = new XSSFWorkbook();
             ByteArrayOutputStream out = new ByteArrayOutputStream()) {

            // 2. Crear una hoja
            Sheet sheet = workbook.createSheet("Historial de Ventas");

            // 3. Crear estilo para los encabezados
            CellStyle headerStyle = workbook.createCellStyle();
            Font headerFont = workbook.createFont();
            headerFont.setBold(true);
            headerStyle.setFont(headerFont);

            // 4. Crear fila de encabezados
            Row headerRow = sheet.createRow(0);
            String[] columns = {"ID Venta", "Fecha", "Vendedor", "Método de Pago", "Subtotal", "Impuesto (16%)", "TOTAL"};

            for (int i = 0; i < columns.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(columns[i]);
                cell.setCellStyle(headerStyle);
                sheet.autoSizeColumn(i); // Ajustar ancho de columna
            }

            // 5. Llenar los datos
            int rowNum = 1;
            for (Venta venta : ventas) {
                Row row = sheet.createRow(rowNum++);
                row.createCell(0).setCellValue(venta.getId());
                row.createCell(1).setCellValue(venta.getFechaVenta().format(DATE_FORMATTER));
                row.createCell(2).setCellValue(venta.getVendedor().getNombreCompleto());
                row.createCell(3).setCellValue(venta.getMetodoPago().name());
                row.createCell(4).setCellValue(venta.getSubtotal().doubleValue());
                row.createCell(5).setCellValue(venta.getImpuesto().doubleValue());
                row.createCell(6).setCellValue(venta.getTotal().doubleValue());
            }

            // 6. Escribir el libro en el flujo de salida
            workbook.write(out);
            return out.toByteArray();
        }
    }
}
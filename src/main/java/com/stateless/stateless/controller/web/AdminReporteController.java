package com.stateless.stateless.controller.web;

import com.stateless.stateless.model.Venta;
import com.stateless.stateless.repository.VentaRepository;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.io.ByteArrayOutputStream;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/admin/reportes")
@PreAuthorize("hasRole('ADMIN') or hasAuthority('ROLE_ADMIN') or hasAuthority('ADMIN')")
public class AdminReporteController {

    @Autowired 
    private VentaRepository ventaRepository;

    // 0. Redirección automática si dan clic en "/admin/reportes" o "/admin/reportes/"
    @GetMapping({"", "/"})
    public String indexRedireccion() {
        return "redirect:/admin/reportes/ventas";
    }

    // 1. Dashboard Web con Filtro de Fechas
    @GetMapping("/ventas")
    public String reporteFinanciero(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            Model model) {

        List<Venta> todas = ventaRepository.findAll();

        List<Venta> ventas = todas.stream().filter(v -> {
            if (v.getCreatedAt() == null) return true;
            LocalDate fechaVenta = v.getCreatedAt().toLocalDate();
            if (startDate != null && fechaVenta.isBefore(startDate)) return false;
            if (endDate != null && fechaVenta.isAfter(endDate)) return false;
            return true;
        }).collect(Collectors.toList());

        // 1. Total Ingresos Brutos
        BigDecimal totalIngresos = ventas.stream()
                .filter(v -> v.getTotal() != null)
                .map(Venta::getTotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        // 2. Base Neta (Subtotal sin IVA de 19%) -> Total / 1.19
        BigDecimal subtotalNeto = totalIngresos.divide(new BigDecimal("1.19"), 0, RoundingMode.HALF_UP);

        // 3. Recaudo IVA (19%) -> Total - SubtotalNeto
        BigDecimal totalIva = totalIngresos.subtract(subtotalNeto);

        // 4. Métricas de conteo y promedio
        int cantidadPedidos = ventas.size();
        BigDecimal ticketPromedio = cantidadPedidos > 0 
                ? totalIngresos.divide(new BigDecimal(cantidadPedidos), 0, RoundingMode.HALF_UP) 
                : BigDecimal.ZERO;

        model.addAttribute("ventas", ventas);
        model.addAttribute("totalIngresos", totalIngresos);
        model.addAttribute("subtotalNeto", subtotalNeto);
        model.addAttribute("totalIva", totalIva);
        model.addAttribute("cantidadPedidos", cantidadPedidos);
        model.addAttribute("ticketPromedio", ticketPromedio);
        model.addAttribute("startDate", startDate);
        model.addAttribute("endDate", endDate);

        return "admin/reportes/ventas";
    }

    // 2. Exportación a Excel (.xlsx)
    @GetMapping("/ventas/excel")
    public ResponseEntity<byte[]> descargarExcel(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {

        try (Workbook workbook = new XSSFWorkbook(); ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            Sheet sheet = workbook.createSheet("Reporte Financiero STATELESS");

            Font headerFont = workbook.createFont();
            headerFont.setBold(true);
            headerFont.setColor(IndexedColors.WHITE.getIndex());
            headerFont.setFontHeightInPoints((short) 10);

            CellStyle headerStyle = workbook.createCellStyle();
            headerStyle.setFillForegroundColor(IndexedColors.BLACK.getIndex());
            headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
            headerStyle.setFont(headerFont);
            headerStyle.setAlignment(HorizontalAlignment.CENTER);

            DataFormat format = workbook.createDataFormat();
            CellStyle currencyStyle = workbook.createCellStyle();
            currencyStyle.setDataFormat(format.getFormat("$#,##0"));

            String[] columns = {"ID Venta", "Fecha", "Cliente", "Email", "Método de Pago", "Base Neta (COP)", "IVA 19% (COP)", "Total Pagado (COP)", "Estado"};
            Row headerRow = sheet.createRow(0);
            for (int i = 0; i < columns.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(columns[i]);
                cell.setCellStyle(headerStyle);
            }

            List<Venta> todas = ventaRepository.findAll();
            List<Venta> ventas = todas.stream().filter(v -> {
                if (v.getCreatedAt() == null) return true;
                LocalDate f = v.getCreatedAt().toLocalDate();
                if (startDate != null && f.isBefore(startDate)) return false;
                if (endDate != null && f.isAfter(endDate)) return false;
                return true;
            }).collect(Collectors.toList());

            int rowIdx = 1;
            for (Venta v : ventas) {
                Row row = sheet.createRow(rowIdx++);

                row.createCell(0).setCellValue(v.getId());
                row.createCell(1).setCellValue(v.getCreatedAt() != null ? v.getCreatedAt().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")) : "N/A");
                row.createCell(2).setCellValue(v.getUsuario() != null && v.getUsuario().getName() != null ? v.getUsuario().getName() : "Cliente");
                row.createCell(3).setCellValue(v.getUsuario() != null ? v.getUsuario().getEmail() : "N/A");
                row.createCell(4).setCellValue(v.getMetodoPago() != null ? v.getMetodoPago().toUpperCase() : "TARJETA");

                BigDecimal total = v.getTotal() != null ? v.getTotal() : BigDecimal.ZERO;
                BigDecimal base = total.divide(new BigDecimal("1.19"), 0, RoundingMode.HALF_UP);
                BigDecimal iva = total.subtract(base);

                Cell cBase = row.createCell(5);
                cBase.setCellValue(base.doubleValue());
                cBase.setCellStyle(currencyStyle);

                Cell cIva = row.createCell(6);
                cIva.setCellValue(iva.doubleValue());
                cIva.setCellStyle(currencyStyle);

                Cell cTotal = row.createCell(7);
                cTotal.setCellValue(total.doubleValue());
                cTotal.setCellStyle(currencyStyle);

                row.createCell(8).setCellValue(v.getEstado() != null ? v.getEstado().toUpperCase() : "APROBADO");
            }

            for (int i = 0; i < columns.length; i++) {
                sheet.autoSizeColumn(i);
            }

            workbook.write(out);

            HttpHeaders headersResponse = new HttpHeaders();
            headersResponse.setContentType(MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"));
            headersResponse.setContentDispositionFormData("attachment", "reporte-ventas-stateless.xlsx");

            return new ResponseEntity<>(out.toByteArray(), headersResponse, HttpStatus.OK);

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}
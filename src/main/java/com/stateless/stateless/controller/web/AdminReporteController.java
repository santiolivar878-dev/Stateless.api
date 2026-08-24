package com.stateless.stateless.controller.web;

import com.stateless.stateless.service.ReporteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.*;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Controller
@RequestMapping("/admin/reportes")
@PreAuthorize("hasRole('ADMIN')")
public class AdminReporteController {

    @Autowired private ReporteService reporteService;

    @GetMapping("/ventas")
    public String reporteVentas(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate desde,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate hasta,
            Model model) {

        // Lógica de fechas por defecto (Laravel: startOfMonth / now)
        LocalDate fechaInicio = (desde != null) ? desde : LocalDate.now().withDayOfMonth(1);
        LocalDate fechaFin = (hasta != null) ? hasta : LocalDate.now();

        model.addAllAttributes(reporteService.generarDataVentas(
                fechaInicio.atStartOfDay(), 
                fechaFin.atTime(LocalTime.MAX)));
        
        model.addAttribute("desde", fechaInicio);
        model.addAttribute("hasta", fechaFin);

        return "admin/reportes/ventas";
    }

    @GetMapping("/ventas/excel")
    public ResponseEntity<byte[]> descargarExcel(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate desde,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate hasta) throws IOException {

        byte[] excelContent = reporteService.exportarVentasExcel(desde.atStartOfDay(), hasta.atTime(LocalTime.MAX));

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"));
        headers.setContentDispositionFormData("attachment", "reporte-ventas.xlsx");

        return new ResponseEntity<>(excelContent, headers, HttpStatus.OK);
    }
}
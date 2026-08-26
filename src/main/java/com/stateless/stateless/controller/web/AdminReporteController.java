package com.stateless.stateless.controller.web;

import com.stateless.stateless.service.ReporteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDate;
import java.time.LocalTime;

@Controller
@RequestMapping("/admin/reportes")
@PreAuthorize("hasAnyRole('ADMIN', 'EMPLEADO')")
public class AdminReporteController {

    @Autowired private ReporteService reporteService;

    // Menú principal de reportes (El de los 4 cuadros de tu imagen)
    @GetMapping
    public String index() {
        return "admin/reportes/index";
    }

    // Detalle de métricas con filtros
    @GetMapping("/metricas")
    public String verMetricas(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate desde,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate hasta,
            Model model) {

        LocalDate fechaInicio = (desde != null) ? desde : LocalDate.now().minusMonths(1);
        LocalDate fechaFin = (hasta != null) ? hasta : LocalDate.now();

        model.addAllAttributes(reporteService.obtenerResumenGeneral(
                fechaInicio.atStartOfDay(), 
                fechaFin.atTime(LocalTime.MAX)));
        
        model.addAttribute("desde", fechaInicio);
        model.addAttribute("hasta", fechaFin);

        return "admin/reportes/ventas"; // Reutilizamos esta vista para el dashboard detallado
    }
}
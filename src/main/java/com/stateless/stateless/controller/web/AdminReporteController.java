package com.stateless.stateless.controller.web;

import com.stateless.stateless.model.Producto;
import com.stateless.stateless.repository.ProductoRepository; // IMPORTANTE: Faltaba este
import com.stateless.stateless.service.ReporteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List; // IMPORTANTE: Faltaba este

@Controller
@RequestMapping("/admin/reportes")
@PreAuthorize("hasAnyRole('ADMIN', 'EMPLEADO')")
public class AdminReporteController {

    @Autowired 
    private ReporteService reporteService;

    @Autowired 
    private ProductoRepository productoRepository; // IMPORTANTE: Inyección del repositorio

    // 1. Menú principal de reportes
    @GetMapping
    public String index() {
        return "admin/reportes/index";
    }

    // 2. Reporte de Inventario (Corregido con CamelCase)
    @GetMapping("/inventario")
    public String reporteInventario(Model model) {
        List<Producto> productos = productoRepository.findAll();
        
        long stockBajo = productos.stream()
                .filter(p -> p.getStockActual() != null && p.getStockActual() <= p.getStockMinimo())
                .count();
        
        long sinStock = productos.stream()
                .filter(p -> p.getStockActual() != null && p.getStockActual() == 0)
                .count();
        
        BigDecimal valorInventario = productos.stream()
                .filter(p -> p.getPrecio() != null && p.getStockActual() != null)
                .map(p -> p.getPrecio().multiply(BigDecimal.valueOf(p.getStockActual())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        model.addAttribute("productos", productos);
        model.addAttribute("totalProductos", productos.size());
        model.addAttribute("stockBajo", stockBajo);
        model.addAttribute("sinStock", sinStock);
        model.addAttribute("valorInventario", valorInventario);

        return "admin/reportes/inventario";
    }

    // 3. Detalle de métricas de Ventas con filtros
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

        return "admin/reportes/ventas";
    }
}
package com.stateless.stateless.controller.web;

import com.stateless.stateless.model.Producto;
import com.stateless.stateless.repository.ProductoRepository;
import com.stateless.stateless.service.ReporteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.*;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Controller
@RequestMapping("/admin/reportes")
@PreAuthorize("hasRole('ADMIN')")
public class AdminReporteController {

    @Autowired private ReporteService reporteService;
    @Autowired private ProductoRepository productoRepository;

    @GetMapping("/ventas")
    public String reporteVentas(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate desde,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate hasta,
            Model model) {

        LocalDate inicio = (desde != null) ? desde : LocalDate.now().withDayOfMonth(1);
        LocalDate fin = (hasta != null) ? hasta : LocalDate.now();

        model.addAllAttributes(reporteService.generarDataVentas(inicio.atStartOfDay(), fin.atTime(LocalTime.MAX)));
        model.addAttribute("desde", inicio);
        model.addAttribute("hasta", fin);

        return "admin/reportes/ventas";
    }

    @GetMapping("/inventario")
    public String reporteInventario(Model model) {
        List<Producto> productos = productoRepository.findAll();
        long stockBajo = productos.stream().filter(p -> p.getStockActual() <= p.getStockMinimo()).count();
        
        BigDecimal valor = productos.stream()
                .map(p -> p.getPrecio().multiply(BigDecimal.valueOf(p.getStockActual())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        model.addAttribute("productos", productos);
        model.addAttribute("totalProductos", productos.size());
        model.addAttribute("stockBajo", stockBajo);
        model.addAttribute("valorInventario", valor);
        return "admin/reportes/inventario";
    }
}
package com.stateless.stateless.controller.web;

import com.stateless.stateless.model.Venta;
import com.stateless.stateless.repository.ProductoRepository;
import com.stateless.stateless.repository.UserRepository;
import com.stateless.stateless.repository.VentaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/admin")
@PreAuthorize("hasAnyRole('ADMIN', 'EMPLEADO')") // Ambos roles entran al panel
public class AdminController {

    @Autowired private VentaRepository ventaRepository;
    @Autowired private ProductoRepository productoRepository;
    @Autowired private UserRepository userRepository;

    private static final DateTimeFormatter LABEL_FORMAT = DateTimeFormatter.ofPattern("dd/MM");

    @GetMapping("/dashboard")
    public String dashboard(Model model) {
        List<Venta> todasLasVentas = ventaRepository.findAll();

        // ---------- 1. KPIs ----------
        BigDecimal totalVentas = todasLasVentas.stream()
                .map(Venta::getTotal)
                .filter(t -> t != null)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        long totalPedidos = todasLasVentas.size();

        long totalClientes = todasLasVentas.stream()
                .map(Venta::getUsuario)
                .filter(u -> u != null)
                .map(u -> u.getId())
                .distinct()
                .count();

        model.addAttribute("totalVentas", totalVentas);
        model.addAttribute("totalPedidos", totalPedidos);
        model.addAttribute("totalClientes", totalClientes);
        model.addAttribute("totalProductos", productoRepository.count());

        // ---------- 2. Gráfica de barras: ventas último mes por día ----------
        LocalDateTime desde = LocalDate.now().minusMonths(1).atStartOfDay();

        Map<LocalDate, BigDecimal> ventasPorDiaMap = new LinkedHashMap<>();
        // Inicializamos todos los días del rango en 0 para que la gráfica no tenga huecos
        for (LocalDate dia = desde.toLocalDate(); !dia.isAfter(LocalDate.now()); dia = dia.plusDays(1)) {
            ventasPorDiaMap.put(dia, BigDecimal.ZERO);
        }

        todasLasVentas.stream()
                .filter(v -> v.getCreatedAt() != null && !v.getCreatedAt().isBefore(desde))
                .forEach(v -> {
                    LocalDate dia = v.getCreatedAt().toLocalDate();
                    ventasPorDiaMap.merge(dia, v.getTotal() != null ? v.getTotal() : BigDecimal.ZERO, BigDecimal::add);
                });

        List<String> labelsDias = new ArrayList<>();
        List<BigDecimal> ventasPorDia = new ArrayList<>();
        ventasPorDiaMap.forEach((dia, monto) -> {
            labelsDias.add(dia.format(LABEL_FORMAT));
            ventasPorDia.add(monto);
        });

        model.addAttribute("labelsDias", labelsDias);
        model.addAttribute("ventasPorDia", ventasPorDia);

        // ---------- 3. Gráfica de dona: ventas por método de pago ----------
        Map<String, BigDecimal> porMetodo = todasLasVentas.stream()
                .filter(v -> v.getMetodoPago() != null && v.getTotal() != null)
                .collect(Collectors.groupingBy(
                        Venta::getMetodoPago,
                        LinkedHashMap::new,
                        Collectors.reducing(BigDecimal.ZERO, Venta::getTotal, BigDecimal::add)
                ));

        List<Object[]> ventasPorMetodo = porMetodo.entrySet().stream()
                .map(e -> new Object[]{ e.getKey(), e.getValue() })
                .collect(Collectors.toList());

        model.addAttribute("ventasPorMetodo", ventasPorMetodo);

        return "admin/dashboard";
    }
}
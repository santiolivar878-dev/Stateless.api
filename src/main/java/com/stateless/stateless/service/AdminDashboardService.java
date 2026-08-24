package com.stateless.stateless.service;

import com.stateless.stateless.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Service
public class AdminDashboardService {

    @Autowired private VentaRepository ventaRepository;
    @Autowired private ProductoRepository productoRepository;
    @Autowired private UserRepository userRepository;
    @Autowired private EnvioRepository envioRepository;

    public Map<String, Object> obtenerEstadisticas() {
        Map<String, Object> stats = new HashMap<>();

        // KPIs principales
        stats.put("totalVentas", ventaRepository.sumTotalVentas());
        stats.put("totalPedidos", ventaRepository.count());
        stats.put("totalClientes", userRepository.countByRoleName("cliente"));
        stats.put("totalProductos", productoRepository.count());
        
        // Alertas
        stats.put("stockBajo", productoRepository.countStockBajo());
        stats.put("enviosPendientes", envioRepository.countByEstado("pendiente"));

        // Ventas recientes y más vendidos
        stats.put("ventasRecientes", ventaRepository.findTop5ByOrderByCreated_atDesc());
        stats.put("productosMasVendidos", productoRepository.findProductosMasVendidos(PageRequest.of(0, 5)));

        // Métodos de pago (para la gráfica Doughnut)
        stats.put("ventasPorMetodo", ventaRepository.countVentasByMetodoPago());

        // Gráfica de los últimos 30 días (Lógica Carbon replicada)
        List<String> labelsDias = new ArrayList<>();
        List<BigDecimal> ventasPorDia = new ArrayList<>();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM");

        for (int i = 29; i >= 0; i--) {
            LocalDate dia = LocalDate.now().minusDays(i);
            labelsDias.add(dia.format(formatter));
            
            LocalDateTime inicio = dia.atStartOfDay();
            LocalDateTime fin = dia.atTime(23, 59, 59);
            BigDecimal total = ventaRepository.sumTotalByFecha(inicio, fin);
            ventasPorDia.add(total != null ? total : BigDecimal.ZERO);
        }
        
        stats.put("labelsDias", labelsDias);
        stats.put("ventasPorDia", ventasPorDia);

        return stats;
    }
}
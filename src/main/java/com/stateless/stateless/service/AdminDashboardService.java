package com.stateless.stateless.service;

import com.stateless.stateless.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.math.BigDecimal;
import java.time.LocalDate;
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
        
        stats.put("totalVentas", ventaRepository.sumTotalVentas() != null ? ventaRepository.sumTotalVentas() : BigDecimal.ZERO);
        stats.put("totalPedidos", ventaRepository.count());
        stats.put("totalClientes", userRepository.countByRoleName("cliente"));
        stats.put("totalProductos", productoRepository.count());
        stats.put("enviosPendientes", envioRepository.countByEstado("pendiente"));
        stats.put("ventasRecientes", ventaRepository.findTop5ByOrderByCreatedAtDesc());
        stats.put("productosMasVendidos", ventaRepository.findTopSellingProducts());
        stats.put("ventasPorMetodo", ventaRepository.countVentasByMetodoPago());

        List<String> labelsDias = new ArrayList<>();
        List<BigDecimal> ventasPorDia = new ArrayList<>();
        for (int i = 29; i >= 0; i--) {
            LocalDate dia = LocalDate.now().minusDays(i);
            labelsDias.add(dia.format(DateTimeFormatter.ofPattern("dd/MM")));
            BigDecimal suma = ventaRepository.sumTotalByFecha(dia.atStartOfDay(), dia.atTime(23, 59, 59));
            ventasPorDia.add(suma != null ? suma : BigDecimal.ZERO);
        }
        stats.put("labelsDias", labelsDias);
        stats.put("ventasPorDia", ventasPorDia);
        return stats;
    }
}
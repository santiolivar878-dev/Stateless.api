package com.stateless.stateless.service;

import com.stateless.stateless.model.Venta;
import com.stateless.stateless.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
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

        // KPIs principales con manejo de nulos (Evita error 500 si no hay ventas)
        BigDecimal total = ventaRepository.sumTotalVentas();
        stats.put("totalVentas", total != null ? total : BigDecimal.ZERO);
        
        stats.put("totalPedidos", ventaRepository.count());
        stats.put("totalClientes", userRepository.countByRoleName("cliente"));
        stats.put("totalProductos", productoRepository.count());
        
        // Alertas de stock e inventario
        stats.put("stockBajo", productoRepository.findStockBajo().size());
        stats.put("enviosPendientes", envioRepository.countByEstado("pendiente"));

        // Tablas de actividad reciente
        stats.put("ventasRecientes", ventaRepository.findTop5ByOrderByCreatedAtDesc());
        stats.put("ventasPorMetodo", ventaRepository.countVentasByMetodoPago());

        // Lógica de Gráfica de últimos 30 días (Replicando Carbon de Laravel)
        List<String> labelsDias = new ArrayList<>();
        List<BigDecimal> ventasPorDia = new ArrayList<>();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM");

        for (int i = 29; i >= 0; i--) {
            LocalDate dia = LocalDate.now().minusDays(i);
            labelsDias.add(dia.format(formatter));
            
            BigDecimal sumaDia = ventaRepository.sumTotalByFecha(dia.atStartOfDay(), dia.atTime(23, 59, 59));
            ventasPorDia.add(sumaDia != null ? sumaDia : BigDecimal.ZERO);
        }
        
        stats.put("labelsDias", labelsDias);
        stats.put("ventasPorDia", ventasPorDia);

        return stats;
    }
}
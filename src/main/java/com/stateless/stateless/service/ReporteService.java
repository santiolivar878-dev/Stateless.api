package com.stateless.stateless.service;

import com.stateless.stateless.model.Venta;
import com.stateless.stateless.repository.VentaRepository;
import com.stateless.stateless.repository.ProductoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;

@Service
public class ReporteService {

    @Autowired private VentaRepository ventaRepository;
    @Autowired private ProductoRepository productoRepository;

    public Map<String, Object> generarDataVentas(LocalDateTime desde, LocalDateTime hasta) {
        List<Venta> ventas = ventaRepository.findAllByCreatedAtBetween(desde, hasta);
        Map<String, Object> data = new HashMap<>();
        data.put("ventas", ventas);
        data.put("totalVentas", ventas.stream().map(Venta::getTotal).filter(Objects::nonNull).reduce(BigDecimal.ZERO, BigDecimal::add));
        data.put("totalPedidos", ventas.size());
        return data;
    }

    public Map<String, Object> obtenerResumenGeneral(LocalDateTime inicio, LocalDateTime fin) {
        Map<String, Object> stats = new HashMap<>();
        stats.put("ingresosPeriodo", ventaRepository.sumTotalByFecha(inicio, fin));
        stats.put("cantidadVentas", ventaRepository.findAllByCreatedAtBetween(inicio, fin).size());
        stats.put("topProductos", ventaRepository.findTopSellingProducts());
        stats.put("productosAlerta", productoRepository.findStockBajo());
        return stats;
    }
    
    public byte[] exportarVentasExcel(LocalDateTime desde, LocalDateTime hasta) {
        // Implementación básica para evitar errores de compilación
        return new byte[0];
    }
}
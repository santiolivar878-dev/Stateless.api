package com.stateless.stateless.service;

import com.stateless.stateless.model.Venta;
import com.stateless.stateless.repository.VentaRepository;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class ReporteService {

    @Autowired private VentaRepository ventaRepository;

    public Map<String, Object> generarDataVentas(LocalDateTime desde, LocalDateTime hasta) {
        List<Venta> ventas = ventaRepository.findAllByCreatedAtBetween(desde, hasta);
        
        Map<String, Object> data = new HashMap<>();
        data.put("ventas", ventas);
        
        BigDecimal total = ventas.stream().map(Venta::getTotal).filter(Objects::nonNull).reduce(BigDecimal.ZERO, BigDecimal::add);
        data.put("totalVentas", total);
        data.put("totalPedidos", ventas.size());
        
        BigDecimal fisicas = ventas.stream().filter(v -> "fisica".equals(v.getTipoVenta())).map(Venta::getTotal).filter(Objects::nonNull).reduce(BigDecimal.ZERO, BigDecimal::add);
        data.put("ventasFisicas", fisicas);
        
        BigDecimal online = ventas.stream().filter(v -> "online".equals(v.getTipoVenta())).map(Venta::getTotal).filter(Objects::nonNull).reduce(BigDecimal.ZERO, BigDecimal::add);
        data.put("ventasOnline", online);

        // Agrupación por método de pago
        Map<String, Map<String, Object>> porMetodo = ventas.stream()
            .collect(Collectors.groupingBy(Venta::getMetodoPago, Collectors.collectingAndThen(Collectors.toList(), list -> {
                Map<String, Object> map = new HashMap<>();
                map.put("cantidad", list.size());
                map.put("total", list.stream().map(Venta::getTotal).filter(Objects::nonNull).reduce(BigDecimal.ZERO, BigDecimal::add));
                return map;
            })));
        data.put("porMetodo", porMetodo);
        
        return data;
    }

    public byte[] exportarVentasExcel(LocalDateTime desde, LocalDateTime hasta) throws IOException {
        List<Venta> ventas = ventaRepository.findAllByCreatedAtBetween(desde, hasta);
        try (Workbook workbook = new XSSFWorkbook(); ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            Sheet sheet = workbook.createSheet("Reporte");
            Row header = sheet.createRow(0);
            header.createCell(0).setCellValue("ID");
            header.createCell(1).setCellValue("Cliente");
            header.createCell(2).setCellValue("Total");

            int rowIdx = 1;
            for (Venta v : ventas) {
                Row row = sheet.createRow(rowIdx++);
                row.createCell(0).setCellValue(v.getId());
                row.createCell(1).setCellValue(v.getUsuario().getName());
                row.createCell(2).setCellValue(v.getTotal().doubleValue());
            }
            workbook.write(out);
            return out.toByteArray();
        }
    }
}
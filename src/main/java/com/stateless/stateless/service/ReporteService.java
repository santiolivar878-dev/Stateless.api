package com.stateless.stateless.service;

import com.stateless.stateless.model.*;
import com.stateless.stateless.repository.*;
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
    @Autowired private ProductoRepository productoRepository;

    // Lógica para reporte de Ventas (Equivalente a ReporteController@ventas)
    public Map<String, Object> generarDataVentas(LocalDateTime desde, LocalDateTime hasta) {
        List<Venta> ventas = ventaRepository.findAllByCreated_atBetween(desde, hasta);
        
        Map<String, Object> data = new HashMap<>();
        data.put("ventas", ventas);
        data.put("totalVentas", ventas.stream().map(Venta::getTotal).reduce(BigDecimal.ZERO, BigDecimal::add));
        data.put("totalPedidos", ventas.size());
        data.put("ventasFisicas", ventas.stream().filter(v -> "fisica".equals(v.getTipoVenta())).map(Venta::getTotal).reduce(BigDecimal.ZERO, BigDecimal::add));
        data.put("ventasOnline", ventas.stream().filter(v -> "online".equals(v.getTipoVenta())).map(Venta::getTotal).reduce(BigDecimal.ZERO, BigDecimal::add));

        // Agrupación por método de pago
        Map<String, Map<String, Object>> porMetodo = ventas.stream()
            .collect(Collectors.groupingBy(Venta::getMetodoPago, Collectors.collectingAndThen(Collectors.toList(), list -> {
                Map<String, Object> map = new HashMap<>();
                map.put("cantidad", list.size());
                map.put("total", list.stream().map(Venta::getTotal).reduce(BigDecimal.ZERO, BigDecimal::add));
                return map;
            })));
        data.put("porMetodo", porMetodo);
        
        return data;
    }

    // Exportación a Excel usando Apache POI
    public byte[] exportarVentasExcel(LocalDateTime desde, LocalDateTime hasta) throws IOException {
        List<Venta> ventas = ventaRepository.findAllByCreated_atBetween(desde, hasta);
        
        try (Workbook workbook = new XSSFWorkbook(); ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            Sheet sheet = workbook.createSheet("Reporte de Ventas");

            // Encabezados
            Row headerRow = sheet.createRow(0);
            String[] columns = {"ID Venta", "Cliente", "Tipo", "Método", "Total", "Fecha"};
            for (int i = 0; i < columns.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(columns[i]);
            }

            // Datos
            int rowIdx = 1;
            for (Venta v : ventas) {
                Row row = sheet.createRow(rowIdx++);
                row.createCell(0).setCellValue(v.getId());
                row.createCell(1).setCellValue(v.getUsuario().getName());
                row.createCell(2).setCellValue(v.getTipoVenta());
                row.createCell(3).setCellValue(v.getMetodoPago());
                row.createCell(4).setCellValue(v.getTotal().doubleValue());
                row.createCell(5).setCellValue(v.getCreated_at().toString());
            }

            workbook.write(out);
            return out.toByteArray();
        }
    }
}.stateless
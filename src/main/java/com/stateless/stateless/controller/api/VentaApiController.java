package com.stateless.stateless.controller.api;

import com.stateless.stateless.model.User;
import com.stateless.stateless.repository.VentaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api")
public class VentaApiController {

    @Autowired private VentaRepository ventaRepository;

    @GetMapping("/mis-pedidos")
    public ResponseEntity<?> index(@AuthenticationPrincipal User user) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("d/m/Y H:i");

        List<Map<String, Object>> pedidos = ventaRepository.findByUsuarioIdOrderByCreatedAtDesc(user.getId())
            .stream()
            .map(v -> Map.<String, Object>of(
                "id", v.getId(),
                "tipo_venta", v.getTipoVenta(),
                "metodo_pago", v.getMetodoPago(),
                "total", v.getTotal(),
                "created_at", v.getCreatedAt().format(formatter)
            ))
            .collect(Collectors.toList());

        return ResponseEntity.ok(Map.of("data", pedidos));
    }
}
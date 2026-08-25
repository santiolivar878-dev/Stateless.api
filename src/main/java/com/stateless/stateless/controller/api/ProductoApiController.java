package com.stateless.stateless.controller.api;

import com.stateless.stateless.dto.*;
import com.stateless.stateless.model.Producto;
import com.stateless.stateless.repository.ProductoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/productos")
public class ProductoApiController {

    @Autowired private ProductoRepository productoRepository;

    @GetMapping
    public ResponseEntity<?> index() {
        List<ProductoApiDTO> data = productoRepository.findByEstado("activo").stream()
            .map(this::mapToDTO)
            .collect(Collectors.toList());

        Map<String, Object> response = new HashMap<>();
        response.put("data", data);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> show(@PathVariable Long id) {
        Producto p = productoRepository.findById(id).orElseThrow();
        Map<String, Object> response = new HashMap<>();
        response.put("data", mapToDTO(p));
        return ResponseEntity.ok(response);
    }

    private ProductoApiDTO mapToDTO(Producto p) {
        return new ProductoApiDTO(
            p.getId(),
            p.getNombre(),
            p.getDescripcion(),
            p.getPrecio(),
            p.getImagen(),
            p.getStockActual(),
            p.getEstado(),
            new CategoriaSimpleDTO(p.getCategoria().getNombre()),
            p.getImagenes().stream().map(img -> img.getImagen()).collect(Collectors.toList())
        );
    }
}
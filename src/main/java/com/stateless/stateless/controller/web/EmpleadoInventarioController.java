package com.stateless.stateless.controller.web;

import com.stateless.stateless.model.Producto;
import com.stateless.stateless.repository.ProductoRepository;
import com.stateless.stateless.repository.CategoriaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/empleado/inventario")
@PreAuthorize("hasAnyRole('EMPLEADO', 'ADMIN')") // Restricción de acceso
public class EmpleadoInventarioController {

    @Autowired
    private ProductoRepository productoRepository;

    @Autowired
    private CategoriaRepository categoriaRepository;

    @GetMapping
    public String index(@RequestParam(required = false) Long categoriaId, Model model) {
        List<Producto> productos;
        if (categoriaId != null) {
            productos = productoRepository.findAll().stream()
                    .filter(p -> p.getCategoria().getId().equals(categoriaId))
                    .toList();
        } else {
            productos = productoRepository.findAll();
        }

        model.addAttribute("productos", productos);
        model.addAttribute("categorias", categoriaRepository.findAll());
        model.addAttribute("categoriaSeleccionada", categoriaId);
        return "empleado/inventario/index";
    }

    // Método para actualización rápida de stock (Ajuste de inventario)
    @PostMapping("/update-stock/{id}")
    public String updateStock(@PathVariable Long id, 
                              @RequestParam Integer cantidad, 
                              RedirectAttributes ra) {
        Producto producto = productoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Producto no encontrado"));

        producto.setStockActual(cantidad);
        productoRepository.save(producto);

        ra.addFlashAttribute("success", "Stock de " + producto.getNombre() + " actualizado a " + cantidad);
        return "redirect:/empleado/inventario";
    }
}
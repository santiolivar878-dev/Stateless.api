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
@PreAuthorize("hasAnyRole('EMPLEADO', 'ADMIN')")
public class EmpleadoInventarioController {

    @Autowired private ProductoRepository productoRepository;
    @Autowired private CategoriaRepository categoriaRepository;

    @GetMapping
    public String index(Model model) {
        model.addAttribute("productos", productoRepository.findAll());
        model.addAttribute("categorias", categoriaRepository.findAll());
        return "empleado/inventario/index";
    }

    @PostMapping("/update-stock/{id}")
    public String updateStock(@PathVariable Long id, @RequestParam Integer cantidad, RedirectAttributes ra) {
        Producto p = productoRepository.findById(id).get();
        p.setStockActual(cantidad);
        productoRepository.save(p);
        ra.addFlashAttribute("success", "Stock actualizado.");
        return "redirect:/empleado/inventario";
    }
}
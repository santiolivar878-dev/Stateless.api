package com.stateless.stateless.controller.web;

import com.stateless.stateless.model.Producto;
import com.stateless.stateless.repository.ProductoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import java.util.ArrayList;

@Controller
public class ProductoWebController {

    @Autowired private ProductoRepository productoRepository;

    @GetMapping("/")
    public String home(Model model) {
        try {
            model.addAttribute("essentials", productoRepository.findByEstadoAndCategoriaNombre("activo", "Essentials"));
        } catch (Exception e) {
            model.addAttribute("essentials", new ArrayList<>());
        }
        return "welcome";
    }

    @GetMapping("/producto/{id}")
    public String show(@PathVariable("id") Long id, Model model) {
        // Buscamos el producto, si no existe damos error 404
        Producto producto = productoRepository.findById(id).orElseThrow();
        
        model.addAttribute("producto", producto);
        
        // Lógica ultra-segura para relacionados
        try {
            if (producto.getCategoria() != null) {
                model.addAttribute("relacionados", productoRepository.findTop3ByCategoriaIdAndIdNot(producto.getCategoria().getId(), id));
            } else {
                model.addAttribute("relacionados", new ArrayList<>());
            }
        } catch (Exception e) {
            model.addAttribute("relacionados", new ArrayList<>());
        }
        
        return "producto/show";
    }
}
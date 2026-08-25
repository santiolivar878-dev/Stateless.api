package com.stateless.stateless.controller.web;

import com.stateless.stateless.model.Producto;
import com.stateless.stateless.repository.ProductoRepository;
import com.stateless.stateless.repository.CategoriaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Controller
public class ProductoWebController {

    @Autowired
    private ProductoRepository productoRepository;

    @Autowired
    private CategoriaRepository categoriaRepository;

    @GetMapping("/")
    public String home(Model model) {
        List<Producto> essentials = productoRepository.findByEstadoAndCategoriaNombre("activo", "Essentials");
        model.addAttribute("essentials", essentials != null ? essentials : new ArrayList<>());
        return "welcome";
    }

    @GetMapping("/essentials")
    public String essentials(Model model) {
        List<Producto> essentials = productoRepository.findByEstadoAndCategoriaNombre("activo", "Essentials");
        model.addAttribute("essentials", essentials != null ? essentials : new ArrayList<>());
        return "ecommerce/essentials";
    }

    @GetMapping("/waves")
    public String waves(Model model) {
        List<Producto> waves = productoRepository.findByEstadoAndCategoriaNombre("activo", "Waves");
        model.addAttribute("waves", waves != null ? waves : new ArrayList<>());
        return "ecommerce/waves";
    }

    @GetMapping("/octane")
    public String octane(Model model) {
        List<Producto> octane = productoRepository.findByEstadoAndCategoriaNombre("activo", "Octane");
        model.addAttribute("octane", octane != null ? octane : new ArrayList<>());
        return "ecommerce/octane";
    }

    @GetMapping("/catalogo")
    public String buscar(
            @RequestParam(required = false) String q,
            @RequestParam(required = false) Long cat,
            @RequestParam(required = false) BigDecimal min,
            @RequestParam(required = false) BigDecimal max,
            @RequestParam(defaultValue = "false") boolean disponible,
            Model model) {

        List<Producto> productos = productoRepository.buscarAvanzado(q, cat, min, max, disponible);
        model.addAttribute("productos", productos != null ? productos : new ArrayList<>());
        model.addAttribute("categorias", categoriaRepository.findAll());
        model.addAttribute("query", q);
        return "ecommerce/buscar";
    }

    @GetMapping("/producto/{id}")
    public String show(@PathVariable Long id, Model model) {
        Producto producto = productoRepository.findById(id)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Producto no encontrado"));
        
        model.addAttribute("producto", producto);
        
        // Relacionados: misma categoria, diferente id
        model.addAttribute("relacionados", 
            productoRepository.findTop3ByCategoriaIdAndIdNot(producto.getCategoria().getId(), id));
            
        return "producto/show";
    }
}
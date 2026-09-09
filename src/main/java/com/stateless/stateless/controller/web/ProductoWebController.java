package com.stateless.stateless.controller.web;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.server.ResponseStatusException;

import com.stateless.stateless.model.Producto;
import com.stateless.stateless.repository.CategoriaRepository;
import com.stateless.stateless.repository.ProductoRepository;

@Controller
public class ProductoWebController {

    @Autowired 
    private ProductoRepository productoRepository;

    @Autowired 
    private CategoriaRepository categoriaRepository;

    // RUTA RAIZ: http://localhost:8081/
    @GetMapping("/")
    public String home(Model model) {
        List<Producto> essentials = productoRepository.findByEstadoAndCategoriaNombre("activo", "Essentials");
        model.addAttribute("essentials", essentials != null ? essentials : new ArrayList<>());
        return "welcome";
    }

    @GetMapping("/essentials")
    public String essentials(Model model) {
        List<Producto> lista = productoRepository.findByEstadoAndCategoriaNombre("activo", "Essentials");
        model.addAttribute("productos", lista != null ? lista : new ArrayList<>());
        return "ecommerce/essentials";
    }

    @GetMapping("/waves")
    public String waves(Model model) {
        List<Producto> lista = productoRepository.findByEstadoAndCategoriaNombre("activo", "Waves");
        model.addAttribute("productos", lista != null ? lista : new ArrayList<>());
        return "ecommerce/waves";
    }

    @GetMapping("/octane")
    public String octane(Model model) {
        List<Producto> lista = productoRepository.findByEstadoAndCategoriaNombre("activo", "Octane");
        model.addAttribute("productos", lista != null ? lista : new ArrayList<>());
        return "ecommerce/octane";
    }

    // DETALLE DE PRODUCTO CON TRANSACCIÓN ACTIVA PARA VARIANTES
    @Transactional(readOnly = true)
    @GetMapping("/producto/{id}")
    public String show(@PathVariable("id") Long id, Model model) {
        Producto producto = productoRepository.findById(id)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Producto no encontrado"));
        
        // Forzamos la inicialización de las variantes de color mientras la sesión de BD está abierta
        if (producto.getVariantes() != null) {
            producto.getVariantes().size();
        }

        model.addAttribute("producto", producto);
        
        if (producto.getCategoria() != null) {
            model.addAttribute("relacionados", productoRepository.findTop3ByCategoriaIdAndIdNot(producto.getCategoria().getId(), id));
        } else {
            model.addAttribute("relacionados", new ArrayList<>());
        }
        return "producto/show";
    }
    
    @GetMapping("/catalogo")
    public String catalogo(Model model) {
        model.addAttribute("productos", productoRepository.findAll());
        model.addAttribute("categorias", categoriaRepository.findAll());
        return "ecommerce/buscar";
    }
}
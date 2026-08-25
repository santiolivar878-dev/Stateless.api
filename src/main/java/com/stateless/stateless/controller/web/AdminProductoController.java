package com.stateless.stateless.controller.web;

import com.stateless.stateless.model.Producto;
import com.stateless.stateless.repository.ProductoRepository;
import com.stateless.stateless.repository.CategoriaRepository;
import com.stateless.stateless.repository.ProveedorRepository;
import com.stateless.stateless.service.CloudinaryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.util.Map;

@Controller
@RequestMapping("/admin/productos")
public class AdminProductoController {

    @Autowired private ProductoRepository productoRepository;
    @Autowired private CategoriaRepository categoriaRepository;
    @Autowired private ProveedorRepository proveedorRepository;
    @Autowired private CloudinaryService cloudinaryService;

    @GetMapping
    public String index(Model model) {
        model.addAttribute("productos", productoRepository.findAll());
        return "admin/productos/index";
    }

    @PostMapping("/store")
    public String store(@ModelAttribute Producto producto, 
                        @RequestParam("imagen_nueva") MultipartFile file,
                        RedirectAttributes ra) throws IOException {
        
        if (!file.isEmpty()) {
            // Subir a Cloudinary y obtener la URL segura
            Map result = cloudinaryService.upload(file);
            producto.setImagen((String) result.get("secure_url"));
        }

        productoRepository.save(producto);
        ra.addFlashAttribute("success", "Producto creado con éxito en la nube.");
        return "redirect:/admin/productos";
    }

    @PostMapping("/update/{id}")
    public String update(@PathVariable Long id, @ModelAttribute Producto productoData,
                         @RequestParam("imagen_nueva") MultipartFile file,
                         RedirectAttributes ra) throws IOException {
        
        Producto producto = productoRepository.findById(id).orElseThrow();
        
        if (!file.isEmpty()) {
            // 1. Opcional: Eliminar imagen anterior de Cloudinary si existía
            // (Requiere guardar el publicId en la DB, por ahora solo sobreescribimos la URL)
            
            // 2. Subir nueva imagen
            Map result = cloudinaryService.upload(file);
            producto.setImagen((String) result.get("secure_url"));
        }

        producto.setNombre(productoData.getNombre());
        producto.setPrecio(productoData.getPrecio());
        producto.setStockActual(productoData.getStockActual());
        producto.setCategoria(productoData.getCategoria());
        producto.setEstado(productoData.getEstado());

        productoRepository.save(producto);
        ra.addFlashAttribute("success", "Producto actualizado.");
        return "redirect:/admin/productos";
    }
}
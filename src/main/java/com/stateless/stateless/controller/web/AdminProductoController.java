package com.stateless.stateless.controller.web;

import com.stateless.stateless.model.Producto;
import com.stateless.stateless.repository.CategoriaRepository;
import com.stateless.stateless.repository.ProductoRepository;
import com.stateless.stateless.repository.ProveedorRepository;
import com.stateless.stateless.service.CloudinaryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.util.Map;

@Controller
@RequestMapping("/admin/productos")
@PreAuthorize("hasAnyRole('ADMIN', 'EMPLEADO')")
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

    @GetMapping("/create")
    public String create(Model model) {
        model.addAttribute("producto", new Producto());
        model.addAttribute("categorias", categoriaRepository.findAll());
        model.addAttribute("proveedores", proveedorRepository.findAll());
        return "admin/productos/create";
    }

    @PostMapping("/store")
    public String store(@ModelAttribute Producto producto, 
                        @RequestParam("imagen_file") MultipartFile file,
                        RedirectAttributes ra) throws IOException {
        
        if (!file.isEmpty()) {
            Map result = cloudinaryService.upload(file);
            producto.setImagen((String) result.get("secure_url"));
        }
        
        productoRepository.save(producto);
        ra.addFlashAttribute("success", "Producto creado correctamente.");
        return "redirect:/admin/productos";
    }

    @GetMapping("/edit/{id}")
    public String edit(@PathVariable Long id, Model model) {
        Producto producto = productoRepository.findById(id).orElseThrow();
        model.addAttribute("producto", producto);
        model.addAttribute("categorias", categoriaRepository.findAll());
        model.addAttribute("proveedores", proveedorRepository.findAll());
        return "admin/productos/edit";
    }

    @PostMapping("/update/{id}")
    public String update(@PathVariable Long id, 
                         @ModelAttribute Producto productoData,
                         @RequestParam(value = "imagen_file", required = false) MultipartFile file,
                         RedirectAttributes ra) throws IOException {
        
        Producto producto = productoRepository.findById(id).orElseThrow();
        
        producto.setNombre(productoData.getNombre());
        producto.setDescripcion(productoData.getDescripcion());
        producto.setPrecio(productoData.getPrecio());
        producto.setStockActual(productoData.getStockActual());
        producto.setCategoria(productoData.getCategoria());
        producto.setProveedor(productoData.getProveedor());
        producto.setEstado(productoData.getEstado());

        if (file != null && !file.isEmpty()) {
            Map result = cloudinaryService.upload(file);
            producto.setImagen((String) result.get("secure_url"));
        }

        productoRepository.save(producto);
        ra.addFlashAttribute("success", "Producto actualizado.");
        return "redirect:/admin/productos";
    }

    @PostMapping("/delete/{id}")
    public String delete(@PathVariable Long id, RedirectAttributes ra) {
        productoRepository.deleteById(id);
        ra.addFlashAttribute("success", "Producto eliminado.");
        return "redirect:/admin/productos";
    }
}
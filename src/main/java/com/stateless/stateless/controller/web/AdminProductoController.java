package com.stateless.stateless.controller.web;

import com.stateless.stateless.model.Producto;
import com.stateless.stateless.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.nio.file.*;
import java.util.List;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/admin/productos")
public class AdminProductoController {

    @Autowired private ProductoRepository productoRepository;
    @Autowired private CategoriaRepository categoriaRepository;
    @Autowired private ProveedorRepository proveedorRepository;

    // Ruta donde se guardan las imágenes (Equivalente a public/images)
    private final String UPLOAD_DIR = "src/main/resources/static/images/";

    @GetMapping
    public String index(@RequestParam(required = false) String filtro, Model model) {
        List<Producto> productos;
        if ("stock_bajo".equals(filtro)) productos = productoRepository.findStockBajo();
        else if ("sin_stock".equals(filtro)) productos = productoRepository.findSinStock();
        else if ("activo".equals(filtro) || "inactivo".equals(filtro)) productos = productoRepository.findByEstado(filtro);
        else productos = productoRepository.findAll();

        model.addAttribute("productos", productos);
        return "admin/productos/index";
    }

    @GetMapping("/create")
    public String create(Model model) throws IOException {
        model.addAttribute("producto", new Producto());
        model.addAttribute("categorias", categoriaRepository.findAll());
        model.addAttribute("proveedores", proveedorRepository.findAll());
        
        // Listar archivos existentes en /images/ (Lógica glob de Laravel)
        List<String> imagenesExistentes = Files.list(Paths.get(UPLOAD_DIR))
                .map(path -> path.getFileName().toString())
                .collect(Collectors.toList());
        model.addAttribute("imagenes", imagenesExistentes);
        
        return "admin/productos/create";
    }

    @PostMapping("/store")
    public String store(@ModelAttribute Producto producto, 
                        @RequestParam("imagen_nueva") MultipartFile file,
                        RedirectAttributes ra) throws IOException {
        
        if (!file.isEmpty()) {
            String fileName = System.currentTimeMillis() + "_" + file.getOriginalFilename();
            Path path = Paths.get(UPLOAD_DIR + fileName);
            Files.write(path, file.getBytes());
            producto.setImagen(fileName);
        }

        productoRepository.save(producto);
        ra.addFlashAttribute("success", "Producto creado correctamente.");
        return "redirect:/admin/productos";
    }

    @GetMapping("/edit/{id}")
    public String edit(@PathVariable Long id, Model model) throws IOException {
        Producto producto = productoRepository.findById(id).orElseThrow();
        model.addAttribute("producto", producto);
        model.addAttribute("categorias", categoriaRepository.findAll());
        model.addAttribute("proveedores", proveedorRepository.findAll());
        
        List<String> imagenesExistentes = Files.list(Paths.get(UPLOAD_DIR))
                .map(path -> path.getFileName().toString())
                .collect(Collectors.toList());
        model.addAttribute("imagenes", imagenesExistentes);
        
        return "admin/productos/edit";
    }

    @PostMapping("/update/{id}")
    public String update(@PathVariable Long id, @ModelAttribute Producto producto,
                         @RequestParam("imagen_nueva") MultipartFile file,
                         RedirectAttributes ra) throws IOException {
        
        Producto existing = productoRepository.findById(id).orElseThrow();
        
        if (!file.isEmpty()) {
            String fileName = System.currentTimeMillis() + "_" + file.getOriginalFilename();
            Files.write(Paths.get(UPLOAD_DIR + fileName), file.getBytes());
            producto.setImagen(fileName);
        } else {
            producto.setImagen(existing.getImagen());
        }

        producto.setId(id);
        productoRepository.save(producto);
        ra.addFlashAttribute("success", "Producto actualizado correctamente.");
        return "redirect:/admin/productos";
    }

    @PostMapping("/delete/{id}")
    public String destroy(@PathVariable Long id, RedirectAttributes ra) {
        productoRepository.deleteById(id);
        ra.addFlashAttribute("success", "Producto eliminado.");
        return "redirect:/admin/productos";
    }
}
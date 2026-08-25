package com.stateless.stateless.controller.web;

import com.stateless.stateless.model.Producto;
import com.stateless.stateless.model.ProductoVariante;
import com.stateless.stateless.repository.ProductoRepository;
import com.stateless.stateless.repository.ProductoVarianteRepository;
import com.stateless.stateless.service.CloudinaryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.util.Map;

@Controller
@RequestMapping("/admin/variantes")
@PreAuthorize("hasRole('ADMIN')")
public class AdminVarianteController {

    @Autowired private ProductoVarianteRepository varianteRepository;
    @Autowired private ProductoRepository productoRepository;
    @Autowired private CloudinaryService cloudinaryService;

    @PostMapping("/store/{productoId}")
    public String store(@PathVariable Long productoId,
                        @ModelAttribute ProductoVariante variante,
                        @RequestParam("imagen_file") MultipartFile file,
                        RedirectAttributes ra) throws IOException {
        
        Producto producto = productoRepository.findById(productoId).orElseThrow();
        variante.setProducto(producto);

        if (!file.isEmpty()) {
            Map result = cloudinaryService.upload(file);
            variante.setImagen((String) result.get("secure_url"));
        }

        varianteRepository.save(variante);
        ra.addFlashAttribute("success", "Variante agregada correctamente.");
        return "redirect:/admin/productos/edit/" + productoId;
    }

    @PostMapping("/delete/{id}")
    public String destroy(@PathVariable Long id, RedirectAttributes ra) {
        ProductoVariante variante = varianteRepository.findById(id).orElseThrow();
        Long productoId = variante.getProducto().getId();
        varianteRepository.delete(variante);
        ra.addFlashAttribute("success", "Variante eliminada.");
        return "redirect:/admin/productos/edit/" + productoId;
    }
}
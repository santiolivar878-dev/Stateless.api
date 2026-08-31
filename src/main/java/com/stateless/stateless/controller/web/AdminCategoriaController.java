package com.stateless.stateless.controller.web;

import com.stateless.stateless.model.Categoria;
import com.stateless.stateless.repository.CategoriaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/admin/categorias")
@PreAuthorize("hasAnyRole('ADMIN', 'EMPLEADO')")
public class AdminCategoriaController {

    @Autowired private CategoriaRepository categoriaRepository;

    @GetMapping
    public String index(Model model) {
        model.addAttribute("categorias", categoriaRepository.findAll());
        return "admin/categorias/index";
    }

    @GetMapping("/create")
    public String create(Model model) {
        model.addAttribute("categoria", new Categoria());
        return "admin/categorias/form";
    }

    @PostMapping("/save")
    public String save(@ModelAttribute Categoria categoria, RedirectAttributes ra) {
        categoriaRepository.save(categoria);
        ra.addFlashAttribute("success", "Categoría gestionada correctamente.");
        return "redirect:/admin/categorias";
    }

    @GetMapping("/edit/{id}")
    public String edit(@PathVariable Long id, Model model) {
        model.addAttribute("categoria", categoriaRepository.findById(id).orElseThrow());
        return "admin/categorias/form";
    }

    @PostMapping("/delete/{id}")
    public String delete(@PathVariable Long id, RedirectAttributes ra) {
        try {
            categoriaRepository.deleteById(id);
            ra.addFlashAttribute("success", "Categoría eliminada.");
        } catch (Exception e) {
            ra.addFlashAttribute("error", "No se puede eliminar: tiene productos vinculados.");
        }
        return "redirect:/admin/categorias";
    }
}
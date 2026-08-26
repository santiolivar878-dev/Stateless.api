package com.stateless.stateless.controller.web;

import com.stateless.stateless.model.Proveedor;
import com.stateless.stateless.repository.ProveedorRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/admin/proveedores")
@PreAuthorize("hasRole('ADMIN')")
public class AdminProveedorController {

    @Autowired private ProveedorRepository proveedorRepository;

    @GetMapping
    public String index(Model model) {
        model.addAttribute("proveedores", proveedorRepository.findAll());
        return "admin/proveedores/index";
    }

    @GetMapping("/create")
    public String create(Model model) {
        model.addAttribute("proveedor", new Proveedor());
        return "admin/proveedores/form";
    }

    @PostMapping("/save")
    public String save(@ModelAttribute Proveedor proveedor, RedirectAttributes ra) {
        proveedorRepository.save(proveedor);
        ra.addFlashAttribute("success", "Proveedor guardado correctamente.");
        return "redirect:/admin/proveedores";
    }

    @GetMapping("/edit/{id}")
    public String edit(@PathVariable Long id, Model model) {
        model.addAttribute("proveedor", proveedorRepository.findById(id).orElseThrow());
        return "admin/proveedores/form";
    }

    @PostMapping("/delete/{id}")
    public String delete(@PathVariable Long id, RedirectAttributes ra) {
        try {
            proveedorRepository.deleteById(id);
            ra.addFlashAttribute("success", "Proveedor eliminado.");
        } catch (Exception e) {
            ra.addFlashAttribute("error", "No se puede eliminar: tiene productos asociados.");
        }
        return "redirect:/admin/proveedores";
    }
}
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
@PreAuthorize("hasAnyRole('ADMIN', 'EMPLEADO')")
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
        return "admin/proveedores/create";
    }

    @PostMapping("/store")
    public String store(@ModelAttribute Proveedor proveedor, RedirectAttributes ra) {
        proveedorRepository.save(proveedor);
        ra.addFlashAttribute("success", "Proveedor creado correctamente.");
        return "redirect:/admin/proveedores";
    }

    @GetMapping("/edit/{id}")
    public String edit(@PathVariable Long id, Model model) {
        model.addAttribute("proveedor", proveedorRepository.findById(id).orElseThrow());
        return "admin/proveedores/edit";
    }

    @PostMapping("/update/{id}")
    public String update(@PathVariable Long id, @ModelAttribute Proveedor proveedorData, RedirectAttributes ra) {
        Proveedor proveedor = proveedorRepository.findById(id).orElseThrow();
        proveedor.setNombre(proveedorData.getNombre());
        proveedor.setTelefono(proveedorData.getTelefono());
        proveedor.setCorreo(proveedorData.getCorreo());
        proveedor.setEstado(proveedorData.isEstado());
        
        proveedorRepository.save(proveedor);
        ra.addFlashAttribute("success", "Proveedor actualizado correctamente.");
        return "redirect:/admin/proveedores";
    }

    @PostMapping("/delete/{id}")
    public String destroy(@PathVariable Long id, RedirectAttributes ra) {
        proveedorRepository.deleteById(id);
        ra.addFlashAttribute("success", "Proveedor eliminado correctamente.");
        return "redirect:/admin/proveedores";
    }
}
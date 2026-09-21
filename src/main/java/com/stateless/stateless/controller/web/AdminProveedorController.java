package com.stateless.stateless.controller.web;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.stateless.stateless.model.Proveedor;
import com.stateless.stateless.repository.ProveedorRepository;

@Controller
@RequestMapping("/admin/proveedores")
@PreAuthorize("hasAnyRole('ADMIN', 'EMPLEADO')")
public class AdminProveedorController {

    @Autowired private ProveedorRepository proveedorRepository;

    // Listado robusto con filtros multicriterio
    @GetMapping
    public String index(
            @RequestParam(required = false) String buscar,
            @RequestParam(required = false) String tipoInsumo,
            @RequestParam(required = false) Boolean estado,
            Model model) {

        List<Proveedor> proveedores = proveedorRepository.findAll();

        if (buscar != null && !buscar.trim().isEmpty()) {
            String q = buscar.toLowerCase().trim();
            proveedores = proveedores.stream()
                .filter(p -> (p.getNombre() != null && p.getNombre().toLowerCase().contains(q)) ||
                             (p.getNit() != null && p.getNit().toLowerCase().contains(q)) ||
                             (p.getContactoNombre() != null && p.getContactoNombre().toLowerCase().contains(q)) ||
                             (p.getCiudad() != null && p.getCiudad().toLowerCase().contains(q)))
                .toList();
        }
        if (tipoInsumo != null && !tipoInsumo.trim().isEmpty()) {
            proveedores = proveedores.stream()
                .filter(p -> tipoInsumo.equalsIgnoreCase(p.getTipoInsumo()))
                .toList();
        }
        if (estado != null) {
            proveedores = proveedores.stream()
                .filter(p -> p.isEstado() == estado)
                .toList();
        }

        model.addAttribute("proveedores", proveedores);
        model.addAttribute("buscar", buscar);
        model.addAttribute("selectedTipoInsumo", tipoInsumo);
        model.addAttribute("selectedEstado", estado);
        return "admin/proveedores/index";
    }

    @GetMapping("/create")
    public String create(Model model) {
        model.addAttribute("proveedor", new Proveedor());
        return "admin/proveedores/form";
    }

    @GetMapping("/edit/{id}")
    public String edit(@PathVariable Long id, Model model) {
        Proveedor proveedor = proveedorRepository.findById(id).orElseThrow();
        model.addAttribute("proveedor", proveedor);
        return "admin/proveedores/form";
    }

    @PostMapping("/save")
    public String save(@ModelAttribute Proveedor proveedor, RedirectAttributes ra) {
        System.out.println("GUARDANDO PROVEEDOR -> Nombre: " + proveedor.getNombre() + " | Insumo: " + proveedor.getTipoInsumo() + " | NIT: " + proveedor.getNit());
        
        proveedorRepository.save(proveedor);
        ra.addFlashAttribute("success", "Proveedor '" + proveedor.getNombre() + "' guardado correctamente.");
        return "redirect:/admin/proveedores";
    }

    @PostMapping("/delete/{id}")
    public String delete(@PathVariable Long id, RedirectAttributes ra) {
        try {
            proveedorRepository.deleteById(id);
            ra.addFlashAttribute("success", "Proveedor eliminado correctamente.");
        } catch (Exception e) {
            ra.addFlashAttribute("error", "No se puede eliminar: tiene prendas o telas asociadas en inventario.");
        }
        return "redirect:/admin/proveedores";
    }
}
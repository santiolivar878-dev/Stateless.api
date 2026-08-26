package com.stateless.stateless.controller.web;

import com.stateless.stateless.model.Venta;
import com.stateless.stateless.repository.VentaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/admin/ventas")
@PreAuthorize("hasAnyRole('ADMIN', 'EMPLEADO')")
public class AdminVentaController {

    @Autowired 
    private VentaRepository ventaRepository;

    // Listado de todos los pedidos recibidos
    @GetMapping
    public String index(Model model) {
        List<Venta> ventas = ventaRepository.findAllByOrderByCreatedAtDesc();
        model.addAttribute("ventas", ventas);
        return "admin/ventas/index";
    }

    // Ver el detalle de una venta específica (con sus productos y variantes)
    @GetMapping("/{id}")
    public String show(@PathVariable Long id, Model model) {
        Venta venta = ventaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Pedido no encontrado"));
        model.addAttribute("venta", venta);
        return "admin/ventas/show";
    }

    // Actualizar el estado del pedido (Pendiente, Pagado, Enviado, etc.)
    @PostMapping("/update-status/{id}")
    public String updateStatus(@PathVariable Long id, 
                               @RequestParam String nuevoEstado, 
                               RedirectAttributes ra) {
        Venta venta = ventaRepository.findById(id).orElseThrow();
        venta.setEstado(nuevoEstado);
        ventaRepository.save(venta);
        
        ra.addFlashAttribute("success", "El estado del pedido #" + id + " ha sido actualizado a: " + nuevoEstado);
        return "redirect:/admin/ventas/" + id;
    }
}
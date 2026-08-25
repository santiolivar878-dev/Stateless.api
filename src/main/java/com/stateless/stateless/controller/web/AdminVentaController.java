package com.stateless.stateless.controller.web;

import com.stateless.stateless.model.Venta;
import com.stateless.stateless.repository.VentaRepository;
import com.stateless.stateless.service.EnvioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import java.math.BigDecimal; // IMPORTANTE: Este faltaba

@Controller
@RequestMapping("/admin/ventas")
@PreAuthorize("hasAnyRole('ADMIN', 'EMPLEADO')")
public class AdminVentaController {

    @Autowired private VentaRepository ventaRepository;
    @Autowired private EnvioService envioService;

    @GetMapping
    public String index(@RequestParam(required = false) String estado, Model model) {
        if (estado != null && !estado.isEmpty()) {
            model.addAttribute("ventas", ventaRepository.findByEstadoOrderByCreatedAtDesc(estado));
        } else {
            model.addAttribute("ventas", ventaRepository.findAllByOrderByCreatedAtDesc());
        }
        
        BigDecimal total = ventaRepository.sumTotalVentas();
        model.addAttribute("totalGeneral", total != null ? total : BigDecimal.ZERO);
        return "admin/ventas/index";
    }

    @GetMapping("/{id}")
    public String show(@PathVariable Long id, Model model) {
        Venta venta = ventaRepository.findById(id).orElseThrow();
        model.addAttribute("venta", venta);
        return "admin/ventas/show";
    }

    @PostMapping("/update-status/{id}")
    public String updateStatus(@PathVariable Long id, @RequestParam String estado, RedirectAttributes ra) {
        Venta venta = ventaRepository.findById(id).orElseThrow();
        venta.setEstado(estado);
        ventaRepository.save(venta);
        ra.addFlashAttribute("success", "Estado actualizado.");
        return "redirect:/admin/ventas";
    }
}
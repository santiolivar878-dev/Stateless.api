package com.stateless.stateless.controller.web;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.stateless.stateless.model.Venta;
import com.stateless.stateless.repository.VentaRepository;
import com.stateless.stateless.service.EmailService;

@Controller
@RequestMapping("/admin/ventas")
@PreAuthorize("hasAnyRole('ADMIN', 'EMPLEADO')")
public class AdminVentaController {

    @Autowired 
    private VentaRepository ventaRepository;

    @Autowired
    private EmailService emailService;

    // Listado de todos los pedidos recibidos
    @GetMapping
    public String index(Model model) {
        List<Venta> ventas = ventaRepository.findAllByOrderByCreatedAtDesc();
        model.addAttribute("ventas", ventas);
        return "admin/ventas/index";
    }

    // Ver el detalle de una venta específica
    @GetMapping("/{id}")
    public String show(@PathVariable Long id, Model model) {
        Venta venta = ventaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Pedido no encontrado"));
        model.addAttribute("venta", venta);
        return "admin/ventas/show";
    }

    // Actualizar el estado del pedido
    @PostMapping("/update-status/{id}")
    public String updateStatus(@PathVariable Long id, 
                               @RequestParam String nuevoEstado, 
                               RedirectAttributes ra) {
        Venta venta = ventaRepository.findById(id).orElseThrow();
        venta.setEstado(nuevoEstado);
        ventaRepository.save(venta);
        
        // 📧 Notificar al cliente si el estado es logístico
        if (List.of("en_preparacion", "enviado", "entregado").contains(nuevoEstado)) {
            try {
                emailService.enviarActualizacionLogistica(venta, venta.getEnvio(), nuevoEstado);
            } catch (Exception e) {
                System.err.println("Aviso: No se pudo enviar notificación de estado: " + e.getMessage());
            }
        }

        ra.addFlashAttribute("success", "El estado del pedido #" + id + " ha sido actualizado a: " + nuevoEstado);
        return "redirect:/admin/ventas/" + id;
    }
}
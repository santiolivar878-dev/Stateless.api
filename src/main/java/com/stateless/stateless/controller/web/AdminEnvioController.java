package com.stateless.stateless.controller.web;

import com.stateless.stateless.model.Envio;
import com.stateless.stateless.model.Venta;
import com.stateless.stateless.repository.EnvioRepository;
import com.stateless.stateless.service.EmailService;
import com.stateless.stateless.service.EnvioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/admin/envios")
@PreAuthorize("hasAnyRole('ADMIN', 'EMPLEADO')")
public class AdminEnvioController {

    @Autowired private EnvioRepository envioRepository;
    @Autowired private EnvioService envioService;
    @Autowired private EmailService emailService;

    @GetMapping
    public String index(Model model) {
        model.addAttribute("envios", envioRepository.findAll());
        return "admin/envios/index";
    }

    @GetMapping("/edit/{id}")
    public String edit(@PathVariable Long id, Model model) {
        Envio envio = envioRepository.findById(id).orElseThrow();
        model.addAttribute("envio", envio);
        return "admin/envios/edit";
    }

    @PostMapping("/update/{id}")
    public String update(@PathVariable Long id, 
                         @RequestParam String transportadora,
                         @RequestParam String numeroGuia,
                         @RequestParam String estado,
                         RedirectAttributes ra) {
        try {
            Envio envio = envioRepository.findById(id).orElseThrow();
            envio.setTransportadora(transportadora);
            envio.setNumeroGuia(numeroGuia);
            envio.setEstado(estado);
            envioRepository.save(envio);

            envioService.actualizarEstado(id, estado);

            // Disparo seguro de email de logística
            Venta venta = envio.getVenta();
            if (venta != null && venta.getUsuario() != null && venta.getUsuario().getEmail() != null) {
                String clienteEmail = venta.getUsuario().getEmail();
                emailService.enviarActualizacionLogistica(clienteEmail, venta.getId(), transportadora, numeroGuia, estado);
            }

            ra.addFlashAttribute("success", "Guía actualizada y correo enviado al cliente.");
        } catch (Exception e) {
            System.err.println("Error al actualizar envío: " + e.getMessage());
            ra.addFlashAttribute("error", "Error al actualizar envío: " + e.getMessage());
        }
        return "redirect:/admin/envios";
    }
}
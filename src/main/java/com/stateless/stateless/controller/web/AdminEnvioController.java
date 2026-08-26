package com.stateless.stateless.controller.web;

import com.stateless.stateless.model.Envio;
import com.stateless.stateless.repository.EnvioRepository;
import com.stateless.stateless.service.EnvioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDateTime;

@Controller
@RequestMapping("/admin/envios")
@PreAuthorize("hasAnyRole('ADMIN', 'EMPLEADO')")
public class AdminEnvioController {

    @Autowired private EnvioRepository envioRepository;
    @Autowired private EnvioService envioService;

    // Listado de envíos
    @GetMapping
    public String index(Model model) {
        model.addAttribute("envios", envioRepository.findAll());
        return "admin/envios/index";
    }

    // Formulario para asignar guía y actualizar
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
        
        Envio envio = envioRepository.findById(id).orElseThrow();
        envio.setTransportadora(transportadora);
        envio.setNumeroGuia(numeroGuia);
        
        // Usamos el servicio para asegurar que se registren las fechas de hitos
        envioService.actualizarEstado(id, estado);
        
        ra.addFlashAttribute("success", "Guía de envío actualizada correctamente.");
        return "redirect:/admin/envios";
    }
}
package com.stateless.stateless.controller.web;

import com.stateless.stateless.repository.EnvioRepository;
import com.stateless.stateless.service.EnvioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/admin/envios")
@PreAuthorize("hasAnyRole('ADMIN', 'EMPLEADO')")
public class AdminEnvioController {

    @Autowired private EnvioRepository envioRepository;
    @Autowired private EnvioService envioService;

    @GetMapping
    public String index(Model model) {
        model.addAttribute("envios", envioRepository.findAll());
        return "admin/envios/index";
    }

    @PostMapping("/update-status/{id}")
    public String updateStatus(@PathVariable Long id, @RequestParam String estado) {
        envioService.actualizarEstado(id, estado);
        return "redirect:/admin/envios";
    }
}
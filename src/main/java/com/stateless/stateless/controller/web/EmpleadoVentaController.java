package com.stateless.stateless.controller.web;

import com.stateless.stateless.repository.VentaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/empleado/ventas")
@PreAuthorize("hasAnyRole('EMPLEADO', 'ADMIN')")
public class EmpleadoVentaController {

    @Autowired 
    private VentaRepository ventaRepository;

    @GetMapping
    public String index(Model model) {
        model.addAttribute("ventas", ventaRepository.findAllByOrderByCreatedAtDesc());
        return "admin/ventas/index"; // Reutilizamos la vista de ventas del admin
    }
}
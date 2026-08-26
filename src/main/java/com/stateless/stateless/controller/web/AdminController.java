package com.stateless.stateless.controller.web;

import com.stateless.stateless.repository.ProductoRepository;
import com.stateless.stateless.repository.UserRepository;
import com.stateless.stateless.repository.VentaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/admin")
@PreAuthorize("hasAnyRole('ADMIN', 'EMPLEADO')") // Ambos roles entran al panel
public class AdminController {

    @Autowired private VentaRepository ventaRepository;
    @Autowired private ProductoRepository productoRepository;
    @Autowired private UserRepository userRepository;

    @GetMapping("/dashboard")
    public String dashboard(Model model) {
        // Estadísticas rápidas para los "widgets" del dashboard
        model.addAttribute("totalVentas", ventaRepository.count());
        model.addAttribute("totalProductos", productoRepository.count());
        model.addAttribute("totalUsuarios", userRepository.count());
        model.addAttribute("ventasRecientes", ventaRepository.findAll()); // Ajustar con Pageable luego
        
        return "admin/dashboard";
    }
}
package com.stateless.stateless.controller.web;

import com.stateless.stateless.service.AdminDashboardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/admin")
@PreAuthorize("hasRole('ADMIN')") // Reemplaza el RoleMiddleware de Laravel
public class AdminController {

    @Autowired
    private AdminDashboardService dashboardService;

    @GetMapping("/dashboard")
    public String dashboard(Model model) {
        model.addAllAttributes(dashboardService.obtenerEstadisticas());
        return "admin/dashboard";
    }
}
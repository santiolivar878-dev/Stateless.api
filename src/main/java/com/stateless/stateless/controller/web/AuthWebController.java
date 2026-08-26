package com.stateless.stateless.controller.web;

import com.stateless.stateless.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class AuthWebController {

    @Autowired private AuthService authService;

    @GetMapping("/login")
    public String showLogin() { return "auth/login"; }

    @GetMapping("/register")
    public String showRegister() { return "auth/register"; }

    @PostMapping("/register")
    public String register(@RequestParam String name, 
                           @RequestParam String email, 
                           @RequestParam String password,
                           RedirectAttributes ra) {
        try {
            authService.registrarCliente(name, email, password);
            ra.addFlashAttribute("success", "Registro exitoso. Ya puedes iniciar sesión.");
            return "redirect:/login";
        } catch (Exception e) {
            ra.addFlashAttribute("error", "Error en el registro: " + e.getMessage());
            return "redirect:/register";
        }
    }
}
package com.stateless.stateless.controller.web;

import com.stateless.stateless.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class AuthWebController {

    @Autowired
    private AuthService authService;

    @GetMapping("/login")
    public String showLogin(@RequestParam(value = "error", required = false) String error, Model model) {
        if (error != null) {
            model.addAttribute("loginError", "Credenciales incorrectas.");
        }
        return "auth/login";
    }

    @GetMapping("/register")
    public String showRegister() {
        return "auth/register";
    }

    @PostMapping("/register")
    public String register(@RequestParam String name, 
                           @RequestParam String email, 
                           @RequestParam String password,
                           @RequestParam String password_confirmation,
                           RedirectAttributes redirectAttributes) {
        
        if (!password.equals(password_confirmation)) {
            redirectAttributes.addFlashAttribute("error", "Las contraseñas no coinciden.");
            return "redirect:/register";
        }

        authService.registrarCliente(name, email, password);
        // Aquí se llamaría al servicio de Mail (MailService) similar a VerificarEmailMail de Laravel
        
        redirectAttributes.addFlashAttribute("status", "Te enviamos un correo de verificación.");
        return "redirect:/verify-email";
    }

    @GetMapping("/verify-email")
    public String showVerifyNotice() {
        return "auth/verify-email";
    }

    @GetMapping("/auth/verificar/{token}")
    public String verifyEmail(@PathVariable String token, RedirectAttributes redirectAttributes) {
        if (authService.verificarEmail(token)) {
            redirectAttributes.addFlashAttribute("success", "¡Cuenta verificada! Ya puedes iniciar sesión.");
            return "redirect:/login";
        }
        redirectAttributes.addFlashAttribute("error", "El enlace es inválido o expiró.");
        return "redirect:/login";
    }
}
package com.stateless.stateless.controller.web;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.stateless.stateless.model.User;
import com.stateless.stateless.service.AuthService;
import com.stateless.stateless.service.EmailService;

@Controller
public class AuthWebController {

    @Autowired private AuthService authService;
    @Autowired private EmailService emailService;

    @Value("${app.base-url:http://localhost:8081}")
    private String baseUrl;

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
            User user = authService.registrarCliente(name, email, password);

            // 👉 Enviamos el correo de verificación
            try {
                String linkVerificacion = baseUrl + "/auth/verificar/" + user.getEmailToken();
                emailService.sendVerificationEmail(user.getEmail(), linkVerificacion, user.getName());
                ra.addFlashAttribute("success", "Registro exitoso. Te enviamos un correo para verificar tu cuenta.");
            } catch (Exception mailEx) {
                System.err.println("Aviso: No se pudo enviar el correo de verificación: " + mailEx.getMessage());
                ra.addFlashAttribute("success", "Registro exitoso. Ya puedes iniciar sesión.");
            }

            return "redirect:/login";

        } catch (IllegalArgumentException e) {
            ra.addFlashAttribute("error", e.getMessage());
            return "redirect:/register";
        } catch (Exception e) {
            ra.addFlashAttribute("error", "No se pudo completar el registro: " + e.getMessage());
            return "redirect:/register";
        }
    }

    // 👉 Ruta que se ejecuta cuando el usuario da clic en el botón del correo
    @GetMapping("/auth/verificar/{token}")
    public String verificarCuenta(@PathVariable String token, RedirectAttributes ra) {
        if (authService.verificarEmail(token)) {
            ra.addFlashAttribute("success", "¡Cuenta verificada exitosamente! Ya puedes iniciar sesión.");
        } else {
            ra.addFlashAttribute("error", "El enlace de verificación es inválido o ha expirado.");
        }
        return "redirect:/login";
    }
}
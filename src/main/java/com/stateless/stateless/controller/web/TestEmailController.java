package com.stateless.stateless.controller.web;

import com.stateless.stateless.service.EmailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TestEmailController {

    @Autowired
    private EmailService emailService;

    @GetMapping("/test-email")
    public String testEmail(@RequestParam String correo) {
        try {
            // Simulamos un token y una URL de verificación
            String urlPrueba = "http://localhost:8081/auth/verificar/token-de-prueba-123";
            
            emailService.sendVerificationEmail(correo, urlPrueba, "Usuario de Prueba");
            
            return "✅ Correo enviado con éxito a: " + correo + ". Revisa tu bandeja de entrada.";
        } catch (Exception e) {
            return "❌ Error al enviar correo: " + e.getMessage();
        }
    }
}
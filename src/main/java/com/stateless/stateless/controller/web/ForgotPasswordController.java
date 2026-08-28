package com.stateless.stateless.controller.web;

import com.stateless.stateless.model.User;
import com.stateless.stateless.service.EmailService;
import com.stateless.stateless.service.PasswordResetService;
import com.stateless.stateless.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class ForgotPasswordController {

    @Autowired private PasswordResetService resetService;
    @Autowired private EmailService emailService;
    @Autowired private UserRepository userRepository;
    
    @Value("${app.base-url}") 
    private String baseUrl;

    // 1. Muestra el formulario para pedir el correo
    @GetMapping("/forgot-password")
    public String showForgotForm() {
        return "auth/forgot-password";
    }

    // 2. Procesa el envío del email
    @PostMapping("/forgot-password")
    public String processForgot(@RequestParam String email, RedirectAttributes ra) {
        try {
            String token = resetService.createToken(email);
            if (token != null) {
                User user = userRepository.findByEmail(email).orElse(null);
                if (user != null) {
                    String url = baseUrl + "/reset-password/" + token;
                    emailService.sendResetPasswordEmail(email, url, user.getName());
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        ra.addFlashAttribute("status", "Si ese correo existe, recibirás un enlace en breve.");
        return "redirect:/forgot-password";
    }

    // 3. Muestra el formulario para poner la NUEVA contraseña (ESTA FALTABA - ERROR 404)
    @GetMapping("/reset-password/{token}")
    public String showResetForm(@PathVariable String token, Model model) {
        model.addAttribute("token", token);
        return "auth/reset-password";
    }

    // 4. Procesa el cambio final en la base de datos
    @PostMapping("/reset-password")
    public String processReset(@RequestParam String token, 
                               @RequestParam String password, 
                               @RequestParam String password_confirmation, 
                               RedirectAttributes ra) {
        
        if (!password.equals(password_confirmation)) {
            ra.addFlashAttribute("error", "Las contraseñas no coinciden.");
            return "redirect:/reset-password/" + token;
        }

        if (resetService.validateAndReset(token, password)) {
            ra.addFlashAttribute("success", "¡Contraseña actualizada! Ya puedes iniciar sesión.");
            return "redirect:/login";
        }

        ra.addFlashAttribute("error", "El enlace expiró o es inválido. Solicita uno nuevo.");
        return "redirect:/forgot-password";
    }
}
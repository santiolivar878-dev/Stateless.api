package com.stateless.stateless.controller.web;

import com.stateless.stateless.model.User;
import com.stateless.stateless.repository.UserRepository;
import com.stateless.stateless.repository.RoleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/admin/usuarios")
@PreAuthorize("hasRole('ADMIN')") // Solo el administrador puede entrar
public class AdminUsuarioController {

    @Autowired private UserRepository userRepository;
    @Autowired private RoleRepository roleRepository;
    @Autowired private PasswordEncoder passwordEncoder;

    @GetMapping
    public String index(Model model) {
        model.addAttribute("users", userRepository.findAll());
        return "admin/usuarios/index";
    }

    @GetMapping("/edit/{id}")
    public String edit(@PathVariable Long id, Model model) {
        model.addAttribute("user", userRepository.findById(id).orElseThrow());
        model.addAttribute("roles", roleRepository.findAll());
        return "admin/usuarios/edit";
    }

    @PostMapping("/update/{id}")
    public String update(@PathVariable Long id, 
                         @ModelAttribute User userData,
                         @RequestParam(required = false) String newPassword,
                         RedirectAttributes ra) {
        try {
            User user = userRepository.findById(id).orElseThrow();
            user.setName(userData.getName());
            user.setEmail(userData.getEmail());
            user.setRole(userData.getRole());
            user.setEstado(userData.getEstado());

            // Solo actualizamos password si se envía uno nuevo
            if (newPassword != null && !newPassword.isEmpty()) {
                user.setPassword(passwordEncoder.encode(newPassword));
            }

            userRepository.save(user);
            ra.addFlashAttribute("success", "Usuario actualizado correctamente.");
        } catch (Exception e) {
            ra.addFlashAttribute("error", "Error al actualizar: " + e.getMessage());
        }
        return "redirect:/admin/usuarios";
    }

    @PostMapping("/toggle/{id}")
    public String toggleStatus(@PathVariable Long id, RedirectAttributes ra) {
        User user = userRepository.findById(id).orElseThrow();
        
        // Evitar que el administrador se inhabilite a sí mismo
        if (user.getEmail().equals("admin@example.com")) {
            ra.addFlashAttribute("error", "No puedes inhabilitar la cuenta principal de administración.");
            return "redirect:/admin/usuarios";
        }

        user.setEstado(user.getEstado().equals("activo") ? "inactivo" : "activo");
        userRepository.save(user);
        ra.addFlashAttribute("success", "Estado del usuario cambiado.");
        return "redirect:/admin/usuarios";
    }
}
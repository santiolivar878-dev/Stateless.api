package com.stateless.stateless.controller.web;

import com.stateless.stateless.model.User;
import com.stateless.stateless.repository.RoleRepository;
import com.stateless.stateless.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/admin/usuarios")
@PreAuthorize("hasRole('ADMIN')")
public class AdminUsuarioController {

    @Autowired private UserRepository userRepository;
    @Autowired private RoleRepository roleRepository;
    @Autowired private PasswordEncoder passwordEncoder;

    @GetMapping
    public String index(@RequestParam(required = false) String search,
                        @RequestParam(required = false) Long rol, Model model) {
        model.addAttribute("users", userRepository.searchUsers(search, rol));
        model.addAttribute("roles", roleRepository.findAll());
        return "admin/usuarios/index";
    }

    @GetMapping("/create")
    public String create(Model model) {
        model.addAttribute("user", new User());
        model.addAttribute("roles", roleRepository.findAll());
        return "admin/usuarios/create";
    }

    @PostMapping("/store")
    public String store(@ModelAttribute User user, @RequestParam String password_confirmation, RedirectAttributes ra) {
        if (!user.getPassword().equals(password_confirmation)) {
            ra.addFlashAttribute("error", "Las contraseñas no coinciden.");
            return "redirect:/admin/usuarios/create";
        }
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setEstado("activo");
        userRepository.save(user);
        ra.addFlashAttribute("success", "Usuario creado correctamente.");
        return "redirect:/admin/usuarios";
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
        User user = userRepository.findById(id).orElseThrow();
        
        user.setName(userData.getName());
        user.setEmail(userData.getEmail());
        user.setRole(userData.getRole());
        user.setEstado(userData.getEstado());

        // Solo encriptar y cambiar contraseña si el campo no está vacío
        if (newPassword != null && !newPassword.isEmpty()) {
            user.setPassword(passwordEncoder.encode(newPassword));
        }

        userRepository.save(user);
        ra.addFlashAttribute("success", "Usuario actualizado correctamente.");
        return "redirect:/admin/usuarios";
    }

    // Ruta rápida para el cambio de estado (Inhabilitar/Habilitar)
    @PostMapping("/toggle-status/{id}")
    public String toggleStatus(@PathVariable Long id) {
        User user = userRepository.findById(id).orElseThrow();
        user.setEstado(user.getEstado().equals("activo") ? "inactivo" : "activo");
        userRepository.save(user);
        return "redirect:/admin/usuarios";
    }
}
package com.stateless.stateless.controller.web;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.stateless.stateless.model.Role;
import com.stateless.stateless.model.User;
import com.stateless.stateless.repository.RoleRepository;
import com.stateless.stateless.repository.UserRepository;

@Controller
@RequestMapping("/admin/usuarios")
@PreAuthorize("hasRole('ADMIN')")
public class AdminUsuarioController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @GetMapping
    public String index(
            @RequestParam(required = false) String buscar,
            @RequestParam(required = false) Long roleId,
            @RequestParam(required = false) String estado,
            Model model) {

        List<User> usuarios = userRepository.findAll();

        // Filtros multicriterio
        if (buscar != null && !buscar.trim().isEmpty()) {
            String q = buscar.toLowerCase().trim();
            usuarios = usuarios.stream()
                .filter(u -> (u.getName() != null && u.getName().toLowerCase().contains(q)) ||
                             (u.getEmail() != null && u.getEmail().toLowerCase().contains(q)))
                .toList();
        }
        if (roleId != null && roleId > 0) {
            usuarios = usuarios.stream()
                .filter(u -> u.getRole() != null && u.getRole().getId().equals(roleId))
                .toList();
        }
        if (estado != null && !estado.trim().isEmpty()) {
            usuarios = usuarios.stream()
                .filter(u -> estado.equalsIgnoreCase(u.getEstado()))
                .toList();
        }

        List<Role> roles = roleRepository.findAll();

        model.addAttribute("usuarios", usuarios);
        model.addAttribute("roles", roles);
        model.addAttribute("buscar", buscar);
        model.addAttribute("selectedRoleId", roleId);
        model.addAttribute("selectedEstado", estado);

        return "admin/usuarios";
    }

    // Cambio dinámico de rol
    @PostMapping("/cambiar-rol")
    public String cambiarRol(
            @RequestParam Long userId,
            @RequestParam Long roleId,
            RedirectAttributes flash) {

        User user = userRepository.findById(userId).orElse(null);
        Role newRole = roleRepository.findById(roleId).orElse(null);

        if (user == null || newRole == null) {
            flash.addFlashAttribute("error", "Usuario o Rol no encontrado.");
            return "redirect:/admin/usuarios";
        }

        if ("admin@example.com".equalsIgnoreCase(user.getEmail()) && !"admin".equalsIgnoreCase(newRole.getName())) {
            flash.addFlashAttribute("error", "No se puede revocar el rol de Administrador a la cuenta principal.");
            return "redirect:/admin/usuarios";
        }

        user.setRole(newRole);
        userRepository.save(user);

        flash.addFlashAttribute("success", "Rol de " + user.getName() + " actualizado a " + newRole.getName().toUpperCase() + " exitosamente.");
        return "redirect:/admin/usuarios";
    }

    // Activar / Suspender usuario
    @PostMapping("/cambiar-estado")
    public String cambiarEstado(
            @RequestParam Long userId,
            @RequestParam String nuevoEstado,
            RedirectAttributes flash) {

        User user = userRepository.findById(userId).orElse(null);
        if (user == null) {
            flash.addFlashAttribute("error", "Usuario no encontrado.");
            return "redirect:/admin/usuarios";
        }

        if ("admin@example.com".equalsIgnoreCase(user.getEmail())) {
            flash.addFlashAttribute("error", "No se puede suspender al administrador principal.");
            return "redirect:/admin/usuarios";
        }

        user.setEstado(nuevoEstado);
        userRepository.save(user);

        flash.addFlashAttribute("success", "Estado del usuario actualizado a: " + nuevoEstado.toUpperCase());
        return "redirect:/admin/usuarios";
    }

    // Crear nuevo rol dinámico
    @PostMapping("/roles/crear")
    public String crearRol(
            @RequestParam String nombreRol,
            RedirectAttributes flash) {

        String limpio = nombreRol.trim().toLowerCase();
        if (limpio.isEmpty()) {
            flash.addFlashAttribute("error", "El nombre del rol no puede estar vacío.");
            return "redirect:/admin/usuarios";
        }

        boolean existe = roleRepository.findAll().stream()
                .anyMatch(r -> r.getName() != null && r.getName().equalsIgnoreCase(limpio));

        if (existe) {
            flash.addFlashAttribute("error", "El rol '" + limpio + "' ya existe en el sistema.");
            return "redirect:/admin/usuarios";
        }

        Role r = new Role();
        r.setName(limpio);
        roleRepository.save(r);

        flash.addFlashAttribute("success", "Nuevo rol '" + limpio.toUpperCase() + "' registrado dinámicamente.");
        return "redirect:/admin/usuarios";
    }
}
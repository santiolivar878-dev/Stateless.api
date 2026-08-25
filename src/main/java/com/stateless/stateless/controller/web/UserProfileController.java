package com.stateless.stateless.controller.web;

import com.stateless.stateless.model.User;
import com.stateless.stateless.service.UserProfileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/account/profile")
public class UserProfileController {

    @Autowired private UserProfileService profileService;

    @GetMapping
    public String edit(@AuthenticationPrincipal User user, Model model) {
        model.addAttribute("user", user);
        return "account/profile";
    }

    @PostMapping("/update-info")
    public String updateInfo(@AuthenticationPrincipal User user,
                             @RequestParam String name,
                             @RequestParam String email,
                             RedirectAttributes ra) {
        profileService.updateBasicInfo(user.getId(), name, email);
        ra.addFlashAttribute("successInfo", "Información de perfil actualizada.");
        return "redirect:/account/profile";
    }

    @PostMapping("/update-password")
    public String updatePassword(@AuthenticationPrincipal User user,
                                 @RequestParam String current_password,
                                 @RequestParam String password,
                                 @RequestParam String password_confirmation,
                                 RedirectAttributes ra) {
        
        if (!password.equals(password_confirmation)) {
            ra.addFlashAttribute("errorPass", "La nueva contraseña y su confirmación no coinciden.");
            return "redirect:/account/profile";
        }

        if (profileService.updatePassword(user.getId(), current_password, password)) {
            ra.addFlashAttribute("successPass", "Contraseña actualizada correctamente.");
        } else {
            ra.addFlashAttribute("errorPass", "La contraseña actual es incorrecta.");
        }

        return "redirect:/account/profile";
    }
}
package com.stateless.stateless.player.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class PlayerViewController {

// ✅ AHORA (Ruta única para el login del reproductor):
@GetMapping("/player/login")
public String home() {
    return "player/index";
}

    @GetMapping("/player")
    public String player(@RequestParam(name = "coleccion", defaultValue = "essentials") String coleccion, Model model) {
        String skin = switch (coleccion.toLowerCase()) {
            case "waves" -> "waves";
            case "octane" -> "octane";
            case "life" -> "life";
            default -> "essentials";
        };
        model.addAttribute("skin", skin);
        return "player/player";
    }
}
package com.stateless.stateless.controller.web;

import com.stateless.stateless.model.User;
import com.stateless.stateless.model.Carrito;
import com.stateless.stateless.service.CarritoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("/carrito")
public class CarritoWebController {

    @Autowired private CarritoService carritoService;

    @GetMapping
    public String index(Model model, @AuthenticationPrincipal User user, HttpSession session) {
        // Obtenemos el carrito (ya sea de DB o de Sesión)
        Carrito carrito = carritoService.obtenerCarritoDeCualquierFuente(user, session);
        model.addAttribute("carrito", carrito);
        return "cart/index";
    }

    @PostMapping("/agregar/{id}")
    public String agregar(@PathVariable("id") Long id, 
                          @RequestParam(required = false) Long varianteId, 
                          @AuthenticationPrincipal User user,
                          HttpSession session) {
        
        // Agregamos sin pedir login
        carritoService.agregarProducto(id, varianteId, user, session);
        return "redirect:/carrito";
    }
}
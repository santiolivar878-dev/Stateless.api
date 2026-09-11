package com.stateless.stateless.controller.web;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.stateless.stateless.model.Carrito;
import com.stateless.stateless.model.User;
import com.stateless.stateless.service.CarritoService;

import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("/carrito")
public class CarritoWebController {

    @Autowired private CarritoService carritoService;

    @GetMapping
    public String index(Model model, @AuthenticationPrincipal User user, HttpSession session) {
        Carrito carrito = carritoService.obtenerCarritoDeCualquierFuente(user, session);
        model.addAttribute("carrito", carrito);
        return "cart/index";
    }

    @PostMapping("/agregar/{id}")
    public String agregar(@PathVariable("id") Long id, 
                          @RequestParam(required = false) Long varianteId, 
                          @RequestParam(defaultValue = "1") Integer cantidad,
                          @AuthenticationPrincipal User user,
                          HttpSession session) {
        carritoService.agregarProducto(id, varianteId, cantidad, user, session);
        return "redirect:/carrito";
    }

    @PostMapping("/actualizar")
    public String actualizar(@RequestParam Long productoId,
                             @RequestParam(required = false) Long varianteId,
                             @RequestParam Integer cantidad,
                             @AuthenticationPrincipal User user,
                             HttpSession session) {
        carritoService.actualizarCantidad(productoId, varianteId, cantidad, user, session);
        return "redirect:/carrito";
    }

    @PostMapping("/eliminar")
    public String eliminar(@RequestParam Long productoId,
                           @RequestParam(required = false) Long varianteId,
                           @AuthenticationPrincipal User user,
                           HttpSession session) {
        carritoService.eliminarProducto(productoId, varianteId, user, session);
        return "redirect:/carrito";
    }
}
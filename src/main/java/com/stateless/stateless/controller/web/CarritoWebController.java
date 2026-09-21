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
        Carrito carrito = carritoService.obtenerCarritoDeCualquierFuente(user, session);
        model.addAttribute("carrito", carrito);
        return "cart/index";
    }

    @PostMapping("/agregar/{id}")
    public String agregar(@PathVariable("id") Long id, 
                          @RequestParam(required = false) Long varianteId, 
<<<<<<< HEAD
                          @AuthenticationPrincipal User user,
                          HttpSession session) {
        carritoService.agregarProducto(id, varianteId, user, session);
        return "redirect:/carrito";
    }

    @PostMapping("/actualizar")
    public String actualizar(@RequestParam Long productoId,
                             @RequestParam(required = false) Long varianteId,
                             @RequestParam Integer cantidad,
                             @AuthenticationPrincipal User user,
                             HttpSession session) {
        carritoService.actualizarCantidad(productoId, varianteId, cantidad, user, session);
=======
                          @RequestParam(required = false) String talla, // 👉 Recibe la talla del formulario
                          @RequestParam(defaultValue = "1") Integer cantidad,
                          @AuthenticationPrincipal User user,
                          HttpSession session) {
        carritoService.agregarProducto(id, varianteId, talla, cantidad, user, session);
>>>>>>> 2e47a3aa3c6bbc34415d59ee05877a9c01093587
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
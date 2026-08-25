package com.stateless.stateless.controller.web;

import com.stateless.stateless.model.*;
import com.stateless.stateless.repository.VentaRepository;
import com.stateless.stateless.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import java.util.Map;

@Controller
@RequestMapping("/checkout")
public class CheckoutWebController {

    @Autowired private CarritoService carritoService;
    @Autowired private CheckoutService checkoutService;
    @Autowired private VentaRepository ventaRepository;

    @GetMapping
    public String index(Model model, @AuthenticationPrincipal User user) {
        Carrito carrito = carritoService.obtenerOcrearCarrito(user);
        if (carrito.getItems().isEmpty()) return "redirect:/carrito";
        model.addAttribute("carrito", carrito);
        return "checkout/index";
    }

    @PostMapping("/procesar")
    @ResponseBody
    public Map<String, String> procesar(@RequestBody Map<String, String> payload, 
                                       @AuthenticationPrincipal User user) {
        Venta venta = checkoutService.procesarPedido(
            user, 
            payload.get("direccion"), 
            payload.get("ciudad"), 
            payload.get("metodo_pago")
        );
        return Map.of("redirect", "/checkout/factura/" + venta.getId());
    }

    @GetMapping("/factura/{id}")
    public String factura(@PathVariable Long id, Model model) {
        Venta venta = ventaRepository.findById(id).get();
        model.addAttribute("venta", venta);
        return "checkout/factura";
    }
}
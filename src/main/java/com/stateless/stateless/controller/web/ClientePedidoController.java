package com.stateless.stateless.controller.web;

import com.stateless.stateless.model.User;
import com.stateless.stateless.model.Venta;
import com.stateless.stateless.model.Envio;
import com.stateless.stateless.repository.VentaRepository;
import com.stateless.stateless.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.security.Principal;
import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping("/account")
public class ClientePedidoController {

    @Autowired
    private VentaRepository ventaRepository;

    @Autowired
    private UserRepository userRepository;

    @GetMapping("/pedidos")
    @Transactional(readOnly = true)
    public String index(Model model, Principal principal) {
        if (principal == null) {
            return "redirect:/login";
        }

        User user = userRepository.findByEmail(principal.getName()).orElse(null);
        List<Venta> ventas = new ArrayList<>();

        if (user != null) {
            ventas = ventaRepository.findByUsuarioOrderByCreatedAtDesc(user);
        }

        model.addAttribute("ventas", ventas != null ? ventas : new ArrayList<>());
        return "cliente/pedidos/index";
    }

    @GetMapping("/tracking/{id}")
    @Transactional(readOnly = true)
    public String tracking(@PathVariable("id") Long id, Principal principal, Model model) {
        if (principal == null) {
            return "redirect:/login";
        }

        try {
            Venta venta = ventaRepository.findById(id).orElse(null);
            if (venta == null) {
                return "redirect:/account/pedidos";
            }

            // Forzar inicialización de relaciones
            if (venta.getItems() != null) {
                venta.getItems().size();
            }

            Envio envio = venta.getEnvio();

            model.addAttribute("venta", venta);
            model.addAttribute("envio", envio);
            
            return "tracking";
        } catch (Exception e) {
            e.printStackTrace();
            model.addAttribute("errorMsg", e.getMessage());
            return "cliente/pedidos/index";
        }
    }

    // Endpoint de prueba rápida directa para verificar si la data llega bien
    @GetMapping("/tracking/{id}/test")
    @ResponseBody
    public String trackingTest(@PathVariable("id") Long id) {
        Venta venta = ventaRepository.findById(id).orElse(null);
        if (venta == null) return "Venta no encontrada en BD";
        return "OK - Venta #" + venta.getId() + ", Total: " + venta.getTotal() + 
               ", Envio: " + (venta.getEnvio() != null ? venta.getEnvio().getEstado() : "Sin envío");
    }
}
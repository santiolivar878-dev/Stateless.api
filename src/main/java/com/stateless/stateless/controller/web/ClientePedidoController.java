package com.stateless.stateless.controller.web;

import java.security.Principal;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import com.stateless.stateless.model.Envio;
import com.stateless.stateless.model.User;
import com.stateless.stateless.model.Venta;
import com.stateless.stateless.repository.UserRepository;
import com.stateless.stateless.repository.VentaRepository;

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
        List<Venta> ventas = (user != null) 
            ? ventaRepository.findByUsuarioOrderByCreatedAtDesc(user) 
            : new ArrayList<>();

        model.addAttribute("ventas", ventas != null ? ventas : new ArrayList<>());
        return "cliente/pedidos/index";
    }

    @GetMapping("/tracking/{id}")
    @Transactional(readOnly = true)
    public String tracking(@PathVariable("id") Long id, Principal principal, Model model) {
        if (principal == null) {
            return "redirect:/login";
        }

        Venta venta = ventaRepository.findById(id).orElse(null);
        if (venta == null) {
            return "redirect:/account/pedidos";
        }

        // Si no tiene envío asociado, creamos uno en memoria para que la plantilla no reviente
        Envio envio = venta.getEnvio();
        if (envio == null) {
            envio = new Envio();
            envio.setVenta(venta);
            envio.setEstado(venta.getEstado() != null ? venta.getEstado() : "pendiente");
            envio.setDireccion("Dirección registrada en compra");
            envio.setCiudad("Colombia");
        }

        model.addAttribute("venta", venta);
        model.addAttribute("envio", envio);
        
        return "tracking";
    }
}
package com.stateless.stateless.controller.web;

import com.stateless.stateless.model.User;
import com.stateless.stateless.model.Venta;
import com.stateless.stateless.repository.VentaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

@Controller
public class EnvioWebController {

    @Autowired private VentaRepository ventaRepository;

    @GetMapping("/account/tracking/{ventaId}")
    public String trackingDetalle(@PathVariable Long ventaId, 
                                  @AuthenticationPrincipal User user, Model model) {
        Venta venta = ventaRepository.findById(ventaId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        
        if (!venta.getUsuario().getId().equals(user.getId())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN);
        }

        model.addAttribute("venta", venta);
        model.addAttribute("envio", venta.getEnvio());
        return "envios/tracking-detalle";
    }
}
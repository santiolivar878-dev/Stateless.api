package com.stateless.stateless.controller.web;

import com.stateless.stateless.model.Carrito;
import com.stateless.stateless.model.User;
import com.stateless.stateless.model.Venta;
import com.stateless.stateless.repository.VentaRepository;
import com.stateless.stateless.service.CarritoService;
import com.stateless.stateless.service.CheckoutService;
import com.stateless.stateless.service.FacturaPdfService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.Map;

@Controller
@RequestMapping("/checkout")
public class CheckoutWebController {

    @Autowired 
    private CarritoService carritoService;

    @Autowired 
    private CheckoutService checkoutService;

    @Autowired
    private FacturaPdfService facturaPdfService;

    @Autowired
    private VentaRepository ventaRepository;

    @Value("${stripe.api.public.key:pk_test_dummy}")
    private String stripePublicKey;

    @GetMapping
    public String index(Model model, @AuthenticationPrincipal User user) {
        Carrito carrito = carritoService.obtenerOcrearCarrito(user);
        if (carrito.getItems().isEmpty()) {
            return "redirect:/carrito";
        }

        model.addAttribute("carrito", carrito);
        model.addAttribute("stripeKey", stripePublicKey);
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

        String redirectUrl = "/checkout/factura/" + venta.getId();
        if ("pse".equals(payload.get("metodo_pago"))) {
            redirectUrl = "/checkout/pse/" + venta.getId();
        }

        return Map.of("redirect", redirectUrl);
    }

    @GetMapping("/factura/{ventaId}/descargar")
    public ResponseEntity<byte[]> descargarFactura(@PathVariable Long ventaId) {
        Venta venta = ventaRepository.findById(ventaId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

        byte[] pdfBytes = facturaPdfService.generarFacturaPdf(venta);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PDF);
        headers.setContentDispositionFormData("attachment", "factura-" + String.format("%06d", venta.getId()) + ".pdf");

        return new ResponseEntity<>(pdfBytes, headers, HttpStatus.OK);
    }
}
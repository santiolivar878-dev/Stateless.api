package com.stateless.stateless.controller.web;

import com.stateless.stateless.model.Carrito;
import com.stateless.stateless.model.User;
import com.stateless.stateless.model.Venta;
import com.stateless.stateless.repository.VentaRepository;
import com.stateless.stateless.service.CarritoService;
import com.stateless.stateless.service.CheckoutService;
import com.stateless.stateless.service.FacturaPdfService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
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
    private VentaRepository ventaRepository;

    @Autowired
    private FacturaPdfService facturaPdfService;

    // 1. Mostrar la página de Checkout
    @GetMapping
    public String index(Model model, @AuthenticationPrincipal User user, HttpSession session) {
        Carrito carrito = carritoService.obtenerCarritoDeCualquierFuente(user, session);
        
        if (carrito.getItems().isEmpty()) {
            return "redirect:/carrito";
        }

        model.addAttribute("carrito", carrito);
        return "checkout/index";
    }

    // 2. Procesar el pago (Petición AJAX desde el navegador)
    @PostMapping("/procesar")
    @ResponseBody
    public ResponseEntity<?> procesar(@RequestBody Map<String, String> payload, 
                                       @AuthenticationPrincipal User user) {
        try {
            // Verificación de sesión
            if (user == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(Map.of("error", "Debes iniciar sesión para finalizar la compra."));
            }

            // Llamada al servicio de lógica de negocio (Resta stock y crea Venta)
            Venta venta = checkoutService.procesarPedido(
                user, 
                payload.get("direccion"), 
                payload.get("ciudad"), 
                payload.get("metodo_pago")
            );

            // Devolvemos la URL a la que el JavaScript debe redirigir
            return ResponseEntity.ok(Map.of("redirect", "/checkout/factura/" + venta.getId()));

        } catch (Exception e) {
            e.printStackTrace(); // Imprime el error en la terminal de Docker para debug
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Error en el servidor: " + e.getMessage()));
        }
    }

    // 3. Mostrar la factura en pantalla
    @GetMapping("/factura/{id}")
    public String factura(@PathVariable Long id, Model model) {
        Venta venta = ventaRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Factura no encontrada"));
        
        model.addAttribute("venta", venta);
        return "checkout/factura";
    }

    // 4. Descargar la factura en PDF
    @GetMapping("/factura/{ventaId}/descargar")
    public ResponseEntity<byte[]> descargarFactura(@PathVariable Long ventaId) {
        Venta venta = ventaRepository.findById(ventaId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

        byte[] pdfBytes = facturaPdfService.generarFacturaPdf(venta);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PDF);
        headers.setContentDispositionFormData("attachment", "factura-stateless-" + venta.getId() + ".pdf");

        return new ResponseEntity<>(pdfBytes, headers, HttpStatus.OK);
    }
}
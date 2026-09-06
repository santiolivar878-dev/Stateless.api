package com.stateless.stateless.controller.web;

import com.stateless.stateless.model.Carrito;
import com.stateless.stateless.model.Producto;
import com.stateless.stateless.model.User;
import com.stateless.stateless.model.Venta;
import com.stateless.stateless.repository.ProductoRepository;
import com.stateless.stateless.repository.UserRepository;
import com.stateless.stateless.repository.VentaRepository;
import com.stateless.stateless.service.CarritoService;
import com.stateless.stateless.service.CheckoutService;
import com.stateless.stateless.service.EmailService;
import com.stateless.stateless.service.FacturaPdfService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
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

    @Autowired
    private EmailService emailService;

    @Autowired
    private ProductoRepository productoRepository;

    @Autowired
    private UserRepository userRepository;

    // 1. Mostrar la página de Checkout con sugerencias de ropa real
    @GetMapping
    public String index(Model model, @AuthenticationPrincipal User user, HttpSession session, Authentication auth) {
        if (user == null && auth != null && auth.isAuthenticated()) {
            user = userRepository.findByEmail(auth.getName()).orElse(null);
        }

        Carrito carrito = carritoService.obtenerCarritoDeCualquierFuente(user, session);
        
        if (carrito == null || carrito.getItems() == null || carrito.getItems().isEmpty()) {
            return "redirect:/carrito";
        }

        // 3 prendas reales de tu base de datos para "Frequently bought together"
        List<Producto> sugerencias = productoRepository.findAll().stream()
                .filter(p -> p.getPrecio() != null)
                .limit(3)
                .toList();

        model.addAttribute("carrito", carrito);
        model.addAttribute("user", user);
        model.addAttribute("sugerencias", sugerencias);
        return "checkout/index";
    }

    // 2. Procesar el pago con soporte de Map<String, Object> (para Shop Pay, PayPal, G Pay y tarjetas)
    @PostMapping("/procesar")
    @ResponseBody
    public ResponseEntity<?> procesar(@RequestBody Map<String, Object> payload, 
                                       @AuthenticationPrincipal User user,
                                       Authentication auth) {
        try {
            if (user == null && auth != null && auth.isAuthenticated()) {
                user = userRepository.findByEmail(auth.getName()).orElse(null);
            }

            if (user == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(Map.of("error", "Debes iniciar sesión para finalizar la compra."));
            }

            String direccion = (payload.get("direccion") != null) ? payload.get("direccion").toString() : "Dirección estándar";
            String ciudad = (payload.get("ciudad") != null) ? payload.get("ciudad").toString() : "Colombia";
            String metodoPago = (payload.get("metodo_pago") != null) ? payload.get("metodo_pago").toString() : "tarjeta_credito";

            // Procesar la orden con el servicio existente
            Venta venta = checkoutService.procesarPedido(
                user, 
                direccion, 
                ciudad, 
                metodoPago
            );

            // Disparo del correo de compra
            try {
                emailService.enviarConfirmacionCompra(venta);
            } catch (Exception mailEx) {
                System.err.println("Aviso: Correo no enviado: " + mailEx.getMessage());
            }

            // Redirección con mapa Leaflet
            return ResponseEntity.ok(Map.of("redirect", "/checkout/confirmacion/" + venta.getId()));

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Error al procesar el pago: " + e.getMessage()));
        }
    }

    // 3. Confirmación de compra con mapa y resumen
    @GetMapping("/confirmacion/{id}")
    public String confirmacion(@PathVariable Long id, Model model) {
        Venta venta = ventaRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Orden no encontrada"));
        
        model.addAttribute("venta", venta);
        return "checkout/confirmacion";
    }

    // 4. Factura en pantalla
    @GetMapping("/factura/{id}")
    public String factura(@PathVariable Long id, Model model) {
        Venta venta = ventaRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Factura no encontrada"));
        
        model.addAttribute("venta", venta);
        return "checkout/factura";
    }

    // 5. Descargar PDF
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
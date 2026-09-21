package com.stateless.stateless.controller.web;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.stateless.stateless.model.GarantiaReclamo;
import com.stateless.stateless.model.Producto;
import com.stateless.stateless.model.User;
import com.stateless.stateless.model.Venta;
import com.stateless.stateless.model.VentaItem;
import com.stateless.stateless.repository.GarantiaReclamoRepository;
import com.stateless.stateless.repository.ProductoRepository;
import com.stateless.stateless.repository.VentaRepository;

@Controller
@RequestMapping("/account")
public class AccountWebController {

    @Autowired
    private VentaRepository ventaRepository;

    @Autowired
    private GarantiaReclamoRepository garantiaReclamoRepository;

    @Autowired
    private ProductoRepository productoRepository;

    // Menú principal de cuenta
    @GetMapping
    public String index(@AuthenticationPrincipal User user, Model model) {
        if (user == null) return "redirect:/login";
        model.addAttribute("user", user);
        return "account/index";
    }

    // 1. SOLUCIÓN AL 404: Historial de Pedidos del Cliente
    @GetMapping("/orders")
    public String orders(@AuthenticationPrincipal User user, Model model) {
        if (user == null) return "redirect:/login";

        // Trae las compras del cliente logueado
        List<Venta> pedidos = ventaRepository.findAll().stream()
                .filter(v -> v.getUsuario() != null && v.getUsuario().getId().equals(user.getId()))
                .sorted((a, b) -> (b.getCreatedAt() != null && a.getCreatedAt() != null) 
                        ? b.getCreatedAt().compareTo(a.getCreatedAt()) : 0)
                .toList();

        // Trae las garantías radicadas por este cliente
        List<GarantiaReclamo> misGarantias = garantiaReclamoRepository.findByUsuarioIdOrderByCreatedAtDesc(user.getId());

        model.addAttribute("user", user);
        model.addAttribute("pedidos", pedidos);
        model.addAttribute("garantias", misGarantias);
        return "account/orders";
    }

    // 2. RADICAR QUEJA / GARANTÍA POR PRENDA DAÑADA (CLIENTE)
    @PostMapping("/garantias/radicar")
    public String radicarGarantia(
            @AuthenticationPrincipal User user,
            @RequestParam Long ventaId,
            @RequestParam Long productoId,
            @RequestParam String tipoIncidencia,
            @RequestParam String descripcion,
            RedirectAttributes ra) {

        if (user == null) return "redirect:/login";

        Venta venta = ventaRepository.findById(ventaId).orElse(null);
        Producto producto = productoRepository.findById(productoId).orElse(null);

        if (venta == null || producto == null) {
            ra.addFlashAttribute("error", "Datos de pedido o prenda inválidos.");
            return "redirect:/account/orders";
        }

        GarantiaReclamo gr = new GarantiaReclamo();
        gr.setUsuario(user);
        gr.setVenta(venta);
        gr.setProducto(producto);
        if (producto.getProveedor() != null) {
            gr.setProveedor(producto.getProveedor());
        }
        gr.setTipoIncidencia(tipoIncidencia);
        gr.setDescripcionCliente(descripcion.trim());
        gr.setEstado("RADICADO");
        gr.setCreatedAt(LocalDateTime.now());
        garantiaReclamoRepository.save(gr);

        ra.addFlashAttribute("success", "Tu solicitud de garantía #" + gr.getId() + " para la prenda '" + producto.getNombre() + "' fue radicada. Nuestro equipo de control de calidad y el proveedor textil asignado la están evaluando.");
        return "redirect:/account/orders";
    }
}
package com.stateless.stateless.controller.web;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
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
import com.stateless.stateless.model.Venta;
import com.stateless.stateless.model.VentaItem;
import com.stateless.stateless.repository.GarantiaReclamoRepository;
import com.stateless.stateless.repository.VentaRepository;
import com.stateless.stateless.service.EmailService;

@Controller
@RequestMapping("/admin/ventas")
@PreAuthorize("hasAnyRole('ADMIN', 'EMPLEADO')")
public class AdminVentaController {

    @Autowired 
    private VentaRepository ventaRepository;

    @Autowired
    private GarantiaReclamoRepository garantiaReclamoRepository;

    @Autowired
    private EmailService emailService;

    // Listado de todos los pedidos recibidos con filtros de búsqueda
    @GetMapping
    public String index(
            @RequestParam(required = false) String buscar,
            @RequestParam(required = false) String estado,
            @RequestParam(required = false) String metodoPago,
            Model model) {

        List<Venta> ventas = ventaRepository.findAllByOrderByCreatedAtDesc();

        // Filtro dinámico multicriterio
        if (buscar != null && !buscar.trim().isEmpty()) {
            String q = buscar.toLowerCase().trim();
            ventas = ventas.stream()
                .filter(v -> String.valueOf(v.getId()).contains(q) ||
                             (v.getUsuario() != null && v.getUsuario().getName() != null && v.getUsuario().getName().toLowerCase().contains(q)) ||
                             (v.getUsuario() != null && v.getUsuario().getEmail() != null && v.getUsuario().getEmail().toLowerCase().contains(q)))
                .toList();
        }
        if (estado != null && !estado.trim().isEmpty()) {
            ventas = ventas.stream()
                .filter(v -> estado.equalsIgnoreCase(v.getEstado()))
                .toList();
        }
        if (metodoPago != null && !metodoPago.trim().isEmpty()) {
            ventas = ventas.stream()
                .filter(v -> metodoPago.equalsIgnoreCase(v.getMetodoPago()))
                .toList();
        }

        model.addAttribute("ventas", ventas);
        model.addAttribute("buscar", buscar);
        model.addAttribute("selectedEstado", estado);
        model.addAttribute("selectedMetodoPago", metodoPago);
        return "admin/ventas/index";
    }

    // Ver el detalle de una venta específica
    @GetMapping("/{id}")
    public String show(@PathVariable Long id, Model model) {
        Venta venta = ventaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Pedido no encontrado"));
        model.addAttribute("venta", venta);

        // Si la venta tiene reclamos/garantías registradas, se envían al modelo
        List<GarantiaReclamo> garantias = garantiaReclamoRepository.findByVentaId(id);
        model.addAttribute("garantias", garantias);

        return "admin/ventas/show";
    }

    // Actualizar el estado operativo del pedido (PROHIBIDO CANCELAR POR AQUÍ)
    @PostMapping("/update-status/{id}")
    public String updateStatus(@PathVariable Long id, 
                               @RequestParam String nuevoEstado, 
                               RedirectAttributes ra) {
        
        // REGLA DE NEGOCIO: La cancelación directa está estrictamente bloqueada
        if ("cancelado".equalsIgnoreCase(nuevoEstado)) {
            ra.addFlashAttribute("error", "No está permitida la cancelación directa en este flujo. Debe utilizar el procedimiento de 'Cancelación Excepcional' justificando la solicitud del cliente.");
            return "redirect:/admin/ventas/" + id;
        }

        Venta venta = ventaRepository.findById(id).orElseThrow();
        venta.setEstado(nuevoEstado);
        ventaRepository.save(venta);
        
        // Notificar al cliente si el estado es logístico
        if (List.of("en_preparacion", "enviado", "entregado").contains(nuevoEstado)) {
            try {
                emailService.enviarActualizacionLogistica(venta, venta.getEnvio(), nuevoEstado);
            } catch (Exception e) {
                System.err.println("Aviso: No se pudo enviar notificación de estado: " + e.getMessage());
            }
        }

        ra.addFlashAttribute("success", "El estado del pedido #" + id + " ha sido actualizado a: " + nuevoEstado.toUpperCase());
        return "redirect:/admin/ventas/" + id;
    }

    // =========================================================================
    // CANCELACIÓN EXCEPCIONAL CONTROLADA (SOLO CASOS ESPECIALES / CLIENTE)
    // =========================================================================
    @PostMapping("/cancelar-excepcional/{id}")
    public String cancelarExcepcional(
            @PathVariable Long id,
            @RequestParam String motivo,
            @RequestParam String justificacion,
            @RequestParam(required = false) Long productoAfectadoId,
            RedirectAttributes ra) {

        Venta venta = ventaRepository.findById(id).orElseThrow();

        if ("cancelado".equalsIgnoreCase(venta.getEstado())) {
            ra.addFlashAttribute("error", "Este pedido ya se encuentra cancelado.");
            return "redirect:/admin/ventas/" + id;
        }

        if (justificacion == null || justificacion.trim().length() < 10) {
            ra.addFlashAttribute("error", "Para cancelar una orden se requiere una justificación técnica o de solicitud del cliente de al menos 10 caracteres.");
            return "redirect:/admin/ventas/" + id;
        }

        // Registrar la cancelación formal
        venta.setEstado("cancelado");
        venta.setMotivoCancelacion(motivo);
        venta.setJustificacionCancelacion(justificacion.trim());
        venta.setCancelledAt(LocalDateTime.now());
        ventaRepository.save(venta);

        // Si la cancelación es por PRENDA DAÑADA o DEFECTO DE FÁBRICA, se traslada al PROVEEDOR
        if ("DEFECTO_FABRICA_GARANTIA".equalsIgnoreCase(motivo)) {
            // Buscamos el producto defectuoso
            Producto prod = null;
            if (productoAfectadoId != null) {
                for (VentaItem item : venta.getItems()) {
                    if (item.getProducto() != null && item.getProducto().getId().equals(productoAfectadoId)) {
                        prod = item.getProducto();
                        break;
                    }
                }
            } else if (!venta.getItems().isEmpty() && venta.getItems().get(0).getProducto() != null) {
                prod = venta.getItems().get(0).getProducto();
            }

            GarantiaReclamo gr = new GarantiaReclamo();
            gr.setVenta(venta);
            gr.setUsuario(venta.getUsuario());
            gr.setProducto(prod);
            if (prod != null && prod.getProveedor() != null) {
                gr.setProveedor(prod.getProveedor());
            }
            gr.setTipoIncidencia("PRENDA_DANADA");
            gr.setDescripcionCliente("Cancelación por garantía reportada por cliente: " + justificacion);
            gr.setEstado("TRASLADADO_A_PROVEEDOR");
            gr.setRespuestaAdmin("Cancelación aprobada y caso trasladado al proveedor para auditoría de calidad.");
            gr.setNotificadoProveedor(true);
            garantiaReclamoRepository.save(gr);

            ra.addFlashAttribute("success", "Pedido #" + id + " CANCELADO. Se generó automáticamente el reclamo de garantía #" + gr.getId() + " para el proveedor " + (prod != null && prod.getProveedor() != null ? prod.getProveedor().getNombre() : "asignado") + ".");
        } else {
            ra.addFlashAttribute("success", "Pedido #" + id + " CANCELADO por caso especial (" + motivo + "). Justificación guardada en auditoría.");
        }

        
        

        return "redirect:/admin/ventas/" + id;
    }
}
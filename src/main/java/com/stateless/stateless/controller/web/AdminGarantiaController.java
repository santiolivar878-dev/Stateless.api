package com.stateless.stateless.controller.web;

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
import com.stateless.stateless.repository.GarantiaReclamoRepository;

@Controller
@RequestMapping("/admin/garantias")
@PreAuthorize("hasAnyRole('ADMIN', 'EMPLEADO')")
public class AdminGarantiaController {

    @Autowired
    private GarantiaReclamoRepository garantiaReclamoRepository;

    @GetMapping
    public String index(
            @RequestParam(required = false) String estado,
            @RequestParam(required = false) String tipo,
            Model model) {

        List<GarantiaReclamo> reclamos = garantiaReclamoRepository.findAll();

        if (estado != null && !estado.trim().isEmpty()) {
            reclamos = reclamos.stream().filter(r -> estado.equalsIgnoreCase(r.getEstado())).toList();
        }
        if (tipo != null && !tipo.trim().isEmpty()) {
            reclamos = reclamos.stream().filter(r -> tipo.equalsIgnoreCase(r.getTipoIncidencia())).toList();
        }

        model.addAttribute("reclamos", reclamos);
        model.addAttribute("selectedEstado", estado);
        model.addAttribute("selectedTipo", tipo);

        return "admin/garantias/index";
    }

    // Trasladar reclamo formal al Proveedor o cambiar estado
    @PostMapping("/actualizar-estado/{id}")
    public String actualizarEstado(
            @PathVariable Long id,
            @RequestParam String nuevoEstado,
            @RequestParam String respuestaAdmin,
            RedirectAttributes ra) {

        GarantiaReclamo gr = garantiaReclamoRepository.findById(id).orElseThrow();
        gr.setEstado(nuevoEstado);
        gr.setRespuestaAdmin(respuestaAdmin);

        if ("TRASLADADO_A_PROVEEDOR".equalsIgnoreCase(nuevoEstado)) {
            gr.setNotificadoProveedor(true);
        }

        garantiaReclamoRepository.save(gr);
        ra.addFlashAttribute("success", "Incidencia #" + id + " actualizada a estado: " + nuevoEstado);
        return "redirect:/admin/garantias";
    }
}
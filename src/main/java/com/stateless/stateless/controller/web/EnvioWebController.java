package com.stateless.stateless.controller.web;

@Controller
public class EnvioWebController {

    @Autowired private VentaRepository ventaRepository;

    @GetMapping("/account/tracking/{ventaId}")
    public String trackingDetalle(@PathVariable Long ventaId, 
                                  @AuthenticationPrincipal User user, Model model) {
        Venta venta = ventaRepository.findById(ventaId).orElseThrow();
        
        // Seguridad: Solo el dueño de la venta puede ver el tracking
        if (!venta.getUsuario().getId().equals(user.getId())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN);
        }

        model.addAttribute("venta", venta);
        model.addAttribute("envio", venta.getEnvio());
        return "envios/tracking-detalle";
    }
}
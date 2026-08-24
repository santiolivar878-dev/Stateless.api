package com.stateless.stateless.controller.web;

@Controller
@RequestMapping("/admin/envios")
@PreAuthorize("hasAnyRole('ADMIN', 'EMPLEADO')")
public class AdminEnvioController {

    @Autowired private EnvioRepository envioRepository;
    @Autowired private EnvioService envioService;

    @GetMapping
    public String index(Model model) {
        model.addAttribute("envios", envioRepository.findAll());
        return "admin/envios/index";
    }

    @PostMapping("/update-status/{id}")
    public String updateStatus(@PathVariable Long id, @RequestParam String estado) {
        envioService.actualizarEstado(id, estado);
        return "redirect:/admin/envios";
    }
}
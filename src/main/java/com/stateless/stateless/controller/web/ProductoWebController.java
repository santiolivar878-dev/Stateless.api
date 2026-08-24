package com.stateless.stateless.controller.web;

@Controller
public class ProductoWebController {

    @Autowired
    private ProductoRepository productoRepository;

    @GetMapping("/")
    public String home(Model model) {
        // En Laravel: $essentials = Producto::where('estado','activo').whereHas('categoria', ...)->get();
        model.addAttribute("essentials", 
            productoRepository.findByEstadoAndCategoriaNombre("activo", "Essentials"));
        return "welcome";
    }

    @GetMapping("/essentials")
    public String essentials(Model model) {
        model.addAttribute("essentials", 
            productoRepository.findByEstadoAndCategoriaNombre("activo", "Essentials"));
        return "ecommerce/essentials";
    }

    @GetMapping("/producto/{id}")
    public String show(@PathVariable Long id, Model model) {
        Producto producto = productoRepository.findById(id)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        
        model.addAttribute("producto", producto);
        model.addAttribute("relacionados", 
            productoRepository.findTop3ByCategoriaIdAndIdNot(producto.getCategoria().getId(), id));
            
        return "producto/show";
    }
}
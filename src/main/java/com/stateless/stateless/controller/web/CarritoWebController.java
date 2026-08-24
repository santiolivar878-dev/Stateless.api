package com.stateless.stateless.controller.web;

@Controller
@RequestMapping("/carrito")
public class CarritoWebController {

    @Autowired private CarritoService carritoService;
    @Autowired private CarritoItemRepository itemRepository;

    @GetMapping
    public String index(Model model, @AuthenticationPrincipal User user) {
        model.addAttribute("carrito", carritoService.obtenerOcrearCarrito(user));
        return "carrito/index";
    }

    @PostMapping("/agregar/{id}")
    public String agregar(@PathVariable Long id, @AuthenticationPrincipal User user, RedirectAttributes ra) {
        String resultado = carritoService.agregarProducto(id, user);
        if (!resultado.equals("OK")) {
            ra.addFlashAttribute("error", resultado);
        } else {
            ra.addFlashAttribute("success", "Producto añadido al carrito.");
        }
        return "redirect:/carrito";
    }

    @PostMapping("/actualizar/{itemId}")
    public String actualizar(@PathVariable Long itemId, @RequestParam Integer cantidad, RedirectAttributes ra) {
        CarritoItem item = itemRepository.findById(itemId).orElseThrow();
        
        if (cantidad <= 0) {
            itemRepository.delete(item);
            ra.addFlashAttribute("success", "Producto eliminado.");
        } else if (cantidad > item.getProducto().getStockActual()) {
            ra.addFlashAttribute("error", "Stock insuficiente. Máximo: " + item.getProducto().getStockActual());
        } else {
            item.setCantidad(cantidad);
            itemRepository.save(item);
            ra.addFlashAttribute("success", "Carrito actualizado.");
        }
        return "redirect:/carrito";
    }

    @PostMapping("/eliminar/{itemId}")
    public String eliminar(@PathVariable Long itemId) {
        itemRepository.deleteById(itemId);
        return "redirect:/carrito";
    }
}
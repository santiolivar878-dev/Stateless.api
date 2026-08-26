package com.stateless.stateless.controller.web;

import com.stateless.stateless.model.User;
import com.stateless.stateless.model.Carrito;
import com.stateless.stateless.service.CarritoService;
import com.stateless.stateless.repository.CarritoItemRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/carrito")
public class CarritoWebController {

    @Autowired private CarritoService carritoService;
    @Autowired private CarritoItemRepository itemRepository;

    @GetMapping
public String index(Model model, @AuthenticationPrincipal User user) {
    // Si el usuario es anónimo (null), le pasamos un carrito vacío manual
    if (user == null) {
        model.addAttribute("carrito", new Carrito()); 
    } else {
        model.addAttribute("carrito", carritoService.obtenerOcrearCarrito(user));
    }
    return "cart/index";
}

    @PostMapping("/agregar/{id}")
    public String agregar(@PathVariable Long id, 
                          @RequestParam(required = false) Long varianteId, 
                          @AuthenticationPrincipal User user, 
                          RedirectAttributes ra) {
        
        // El agregado de productos sí requiere cuenta para guardarse en la DB
        if (user == null) {
            ra.addFlashAttribute("error", "Debes iniciar sesión para añadir productos al carrito.");
            return "redirect:/login";
        }
        
        String resultado = carritoService.agregarProducto(id, varianteId, user);
        return "redirect:/carrito";
    }

    @PostMapping("/actualizar/{itemId}")
    public String actualizar(@PathVariable Long itemId, @RequestParam Integer cantidad) {
        itemRepository.findById(itemId).ifPresent(item -> {
            if (cantidad <= 0) {
                itemRepository.delete(item);
            } else {
                item.setCantidad(cantidad);
                itemRepository.save(item);
            }
        });
        return "redirect:/carrito";
    }

    @PostMapping("/eliminar/{itemId}")
    public String eliminar(@PathVariable Long itemId) {
        itemRepository.deleteById(itemId);
        return "redirect:/carrito";
    }
}
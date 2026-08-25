package com.stateless.stateless.controller.web;

import com.stateless.stateless.model.User;
import com.stateless.stateless.model.CarritoItem;
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
        model.addAttribute("carrito", carritoService.obtenerOcrearCarrito(user));
        return "cart/index";
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
        } else {
            item.setCantidad(cantidad);
            itemRepository.save(item);
        }
        return "redirect:/carrito";
    }

    @PostMapping("/eliminar/{itemId}")
    public String eliminar(@PathVariable Long itemId) {
        itemRepository.deleteById(itemId);
        return "redirect:/carrito";
    }
}
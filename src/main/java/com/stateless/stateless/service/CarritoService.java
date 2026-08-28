package com.stateless.stateless.service;

import com.stateless.stateless.model.*;
import com.stateless.stateless.repository.*;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CarritoService {
    @Autowired private CarritoRepository carritoRepository;
    @Autowired private ProductoRepository productoRepository;
    @Autowired private ProductoVarianteRepository varianteRepository;

    // Método principal para obtener el carrito
    public Carrito obtenerCarritoDeCualquierFuente(User user, HttpSession session) {
        if (user != null) {
            return carritoRepository.findByUserId(user.getId()).orElseGet(() -> {
                Carrito nuevo = new Carrito();
                nuevo.setUser(user);
                return carritoRepository.save(nuevo);
            });
        }
        
        Carrito carritoSesion = (Carrito) session.getAttribute("guest_cart");
        if (carritoSesion == null) {
            carritoSesion = new Carrito();
            session.setAttribute("guest_cart", carritoSesion);
        }
        return carritoSesion;
    }

    @Transactional
    public void agregarProducto(Long productoId, Long varianteId, User user, HttpSession session) {
        Producto producto = productoRepository.findById(productoId).orElseThrow();
        ProductoVariante variante = (varianteId != null) ? varianteRepository.findById(varianteId).orElse(null) : null;
        
        Carrito carrito = obtenerCarritoDeCualquierFuente(user, session);
        
        CarritoItem item = carrito.getItems().stream()
                .filter(i -> i.getProducto().getId().equals(productoId) && 
                            ((variante == null && i.getVariante() == null) || 
                             (i.getVariante() != null && i.getVariante().getId().equals(varianteId))))
                .findFirst().orElse(null);

        if (item != null) {
            item.setCantidad(item.getCantidad() + 1);
        } else {
            item = new CarritoItem();
            item.setCarrito(carrito);
            item.setProducto(producto);
            item.setVariante(variante);
            item.setCantidad(1);
            item.setPrecioUnitario(producto.getPrecio());
            carrito.getItems().add(item);
        }

        if (user != null) {
            carritoRepository.save(carrito);
        } else {
            session.setAttribute("guest_cart", carrito);
        }
        
        int count = carrito.getItems().stream().mapToInt(CarritoItem::getCantidad).sum();
        session.setAttribute("cartCount", count);
    }
}
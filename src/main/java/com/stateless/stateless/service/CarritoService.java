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
    @Autowired private HttpSession session; // Para el contador del navbar

    public Carrito obtenerOcrearCarrito(User user) {
        Carrito carrito = carritoRepository.findByUserId(user.getId())
                .orElseGet(() -> {
                    Carrito nuevo = new Carrito();
                    nuevo.setUser(user);
                    return carritoRepository.save(nuevo);
                });
        
        // Actualizamos el contador de la sesión
        int totalItems = carrito.getItems().stream().mapToInt(CarritoItem::getCantidad).sum();
        session.setAttribute("cartCount", totalItems);
        
        return carrito;
    }

    @Transactional
    public String agregarProducto(Long productoId, User user) {
        Producto producto = productoRepository.findById(productoId).orElseThrow();
        Carrito carrito = obtenerOcrearCarrito(user);
        
        CarritoItem item = carrito.getItems().stream()
                .filter(i -> i.getProducto().getId().equals(productoId))
                .findFirst().orElse(null);

        if (item != null) {
            item.setCantidad(item.getCantidad() + 1);
        } else {
            item = new CarritoItem();
            item.setCarrito(carrito);
            item.setProducto(producto);
            item.setCantidad(1);
            item.setPrecioUnitario(producto.getPrecio());
            carrito.getItems().add(item);
        }
        carritoRepository.save(carrito);
        obtenerOcrearCarrito(user); // Actualiza sesión
        return "OK";
    }
}
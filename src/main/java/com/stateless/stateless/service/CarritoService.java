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
    @Autowired private HttpSession session;

    public Carrito obtenerOcrearCarrito(User user) {
        Carrito carrito = carritoRepository.findByUserId(user.getId()).orElseGet(() -> {
            Carrito nuevo = new Carrito();
            nuevo.setUser(user);
            return carritoRepository.save(nuevo);
        });
        session.setAttribute("cartCount", carrito.getItems().stream().mapToInt(CarritoItem::getCantidad).sum());
        return carrito;
    }

    @Transactional
    public String agregarProducto(Long productoId, Long varianteId, User user) {
        Producto producto = productoRepository.findById(productoId).orElseThrow();
        ProductoVariante variante = (varianteId != null) ? varianteRepository.findById(varianteId).orElse(null) : null;
        Carrito carrito = obtenerOcrearCarrito(user);
        
        // Buscar si ya existe el mismo producto con la misma variante
        CarritoItem item = carrito.getItems().stream()
                .filter(i -> i.getProducto().getId().equals(productoId) && 
                            (variante == null || (i.getVariante() != null && i.getVariante().getId().equals(varianteId))))
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
        carritoRepository.save(carrito);
        return "OK";
    }
}
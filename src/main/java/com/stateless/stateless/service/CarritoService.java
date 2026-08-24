package com.stateless.stateless.service;

import com.stateless.stateless.model.*;
import com.stateless.stateless.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CarritoService {

    @Autowired private CarritoRepository carritoRepository;
    @Autowired private CarritoItemRepository itemRepository;
    @Autowired private ProductoRepository productoRepository;

    public Carrito obtenerOcrearCarrito(User user) {
        return carritoRepository.findByUserId(user.getId())
                .orElseGet(() -> {
                    Carrito nuevo = new Carrito();
                    nuevo.setUser(user);
                    return carritoRepository.save(nuevo);
                });
    }

    @Transactional
    public String agregarProducto(Long productoId, User user) {
        Producto producto = productoRepository.findById(productoId)
                .orElseThrow(() -> new RuntimeException("Producto no encontrado"));

        Carrito carrito = obtenerOcrearCarrito(user);
        
        // Buscar si el producto ya está en el carrito
        CarritoItem item = carrito.getItems().stream()
                .filter(i -> i.getProducto().getId().equals(productoId))
                .findFirst().orElse(null);

        int cantidadDeseada = (item != null) ? item.getCantidad() + 1 : 1;

        // Validación de Stock (Misma lógica que Laravel)
        if (cantidadDeseada > producto.getStockActual()) {
            return "No hay suficiente stock disponible.";
        }

        if (item != null) {
            item.setCantidad(cantidadDeseada);
        } else {
            item = new CarritoItem();
            item.setCarrito(carrito);
            item.setProducto(producto);
            item.setCantidad(1);
            item.setPrecioUnitario(producto.getPrecio());
            carrito.getItems().add(item);
        }
        
        carritoRepository.save(carrito);
        return "OK";
    }
}
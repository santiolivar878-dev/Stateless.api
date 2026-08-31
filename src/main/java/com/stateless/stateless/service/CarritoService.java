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

    // 1. Obtener carrito (DB si está autenticado, o Sesión si es invitado)
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

    // 2. Migrar carrito de invitado a base de datos al iniciar sesión
    @Transactional
    public void migrarCarritoSesionAUsuario(HttpSession session, User user) {
        if (session == null || user == null) return;
        
        // Leemos con la clave correcta "guest_cart"
        Carrito carritoSesion = (Carrito) session.getAttribute("guest_cart");
        
        if (carritoSesion != null && carritoSesion.getItems() != null && !carritoSesion.getItems().isEmpty()) {
            for (CarritoItem item : carritoSesion.getItems()) {
                Long varianteId = (item.getVariante() != null) ? item.getVariante().getId() : null;
                
                // Pasamos los ítems a la base de datos del usuario
                for (int i = 0; i < item.getCantidad(); i++) {
                    this.agregarProducto(item.getProducto().getId(), varianteId, user, null);
                }
            }
            // Limpiamos el carrito temporal de la sesión
            session.removeAttribute("guest_cart");
        }
    }

    // 3. Agregar producto
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
        } else if (session != null) {
            session.setAttribute("guest_cart", carrito);
        }
        
        if (session != null) {
            int count = carrito.getItems().stream().mapToInt(CarritoItem::getCantidad).sum();
            session.setAttribute("cartCount", count);
        }
    }
}
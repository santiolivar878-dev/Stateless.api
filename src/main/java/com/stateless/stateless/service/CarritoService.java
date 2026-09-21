package com.stateless.stateless.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.stateless.stateless.model.Carrito;
import com.stateless.stateless.model.CarritoItem;
import com.stateless.stateless.model.Producto;
import com.stateless.stateless.model.ProductoVariante;
import com.stateless.stateless.model.User;
import com.stateless.stateless.repository.CarritoRepository;
import com.stateless.stateless.repository.ProductoRepository;
import com.stateless.stateless.repository.ProductoVarianteRepository;

import jakarta.servlet.http.HttpSession;

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
        
        Carrito carritoSesion = (Carrito) session.getAttribute("guest_cart");
        
        if (carritoSesion != null && carritoSesion.getItems() != null && !carritoSesion.getItems().isEmpty()) {
            for (CarritoItem item : carritoSesion.getItems()) {
                Long varianteId = (item.getVariante() != null) ? item.getVariante().getId() : null;
<<<<<<< HEAD
                for (int i = 0; i < item.getCantidad(); i++) {
                    this.agregarProducto(item.getProducto().getId(), varianteId, user, null);
                }
=======
                this.agregarProducto(item.getProducto().getId(), varianteId, item.getTalla(), item.getCantidad(), user, null);
>>>>>>> 2e47a3aa3c6bbc34415d59ee05877a9c01093587
            }
            session.removeAttribute("guest_cart");
        }
    }

    // 3. Agregar producto
    @Transactional
<<<<<<< HEAD
    public void agregarProducto(Long productoId, Long varianteId, User user, HttpSession session) {
=======
    public void agregarProducto(Long productoId, Long varianteId, String talla, Integer cantidad, User user, HttpSession session) {
        if (cantidad == null || cantidad < 1) cantidad = 1;

>>>>>>> 2e47a3aa3c6bbc34415d59ee05877a9c01093587
        Producto producto = productoRepository.findById(productoId).orElseThrow();
        ProductoVariante variante = (varianteId != null) ? varianteRepository.findById(varianteId).orElse(null) : null;
        
        Carrito carrito = obtenerCarritoDeCualquierFuente(user, session);
        
        // Comparamos producto, variante y también TALLA
        CarritoItem item = carrito.getItems().stream()
                .filter(i -> i.getProducto().getId().equals(productoId) && 
                            ((variante == null && i.getVariante() == null) || 
                             (i.getVariante() != null && i.getVariante().getId().equals(varianteId))) &&
                            ((talla == null && i.getTalla() == null) ||
                             (talla != null && talla.equalsIgnoreCase(i.getTalla()))))
                .findFirst().orElse(null);

        if (item != null) {
            item.setCantidad(item.getCantidad() + 1);
        } else {
            item = new CarritoItem();
            item.setCarrito(carrito);
            item.setProducto(producto);
            item.setVariante(variante);
<<<<<<< HEAD
            item.setCantidad(1);
=======
            item.setTalla(talla); // 👉 Se guarda la talla escogida
            item.setCantidad(cantidad);
>>>>>>> 2e47a3aa3c6bbc34415d59ee05877a9c01093587
            item.setPrecioUnitario(producto.getPrecio());
            carrito.getItems().add(item);
        }

        guardarCarrito(carrito, user, session);
    }

    // 4. Actualizar cantidad directamente (+, -, o número escrito)
    @Transactional
    public void actualizarCantidad(Long productoId, Long varianteId, int nuevaCantidad, User user, HttpSession session) {
        Carrito carrito = obtenerCarritoDeCualquierFuente(user, session);

        if (nuevaCantidad <= 0) {
            eliminarProducto(productoId, varianteId, user, session);
            return;
        }

        carrito.getItems().stream()
                .filter(i -> i.getProducto().getId().equals(productoId) &&
                            ((varianteId == null && i.getVariante() == null) ||
                             (i.getVariante() != null && i.getVariante().getId().equals(varianteId))))
                .findFirst()
                .ifPresent(item -> item.setCantidad(nuevaCantidad));

        guardarCarrito(carrito, user, session);
    }

    // 5. Eliminar producto de la bolsa
    @Transactional
    public void eliminarProducto(Long productoId, Long varianteId, User user, HttpSession session) {
        Carrito carrito = obtenerCarritoDeCualquierFuente(user, session);

        carrito.getItems().removeIf(i -> i.getProducto().getId().equals(productoId) &&
                ((varianteId == null && i.getVariante() == null) ||
                 (i.getVariante() != null && i.getVariante().getId().equals(varianteId))));

        guardarCarrito(carrito, user, session);
    }

    private void guardarCarrito(Carrito carrito, User user, HttpSession session) {
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
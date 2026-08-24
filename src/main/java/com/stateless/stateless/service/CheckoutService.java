package com.stateless.stateless.service;

import com.stateless.stateless.model.*;
import com.stateless.stateless.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.UUID;

@Service
public class CheckoutService {

    @Autowired private VentaRepository ventaRepository;
    @Autowired private ProductoRepository productoRepository;
    @Autowired private CarritoRepository carritoRepository;

    @Transactional
    public Venta procesarPedido(User user, String direccion, String ciudad, String metodoPago) {
        Carrito carrito = carritoRepository.findByUserId(user.getId())
                .orElseThrow(() -> new RuntimeException("Carrito vacío"));

        // 1. Validar Stock
        for (CarritoItem item : carrito.getItems()) {
            if (item.getProducto().getStockActual() < item.getCantidad()) {
                throw new RuntimeException("Stock insuficiente para: " + item.getProducto().getNombre());
            }
        }

        // 2. Generar Código Efecty si aplica (Lógica Laravel)
        String codigoPago = null;
        if ("efecty".equals(metodoPago)) {
            codigoPago = "EFY-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        }

        // 3. Crear Venta
        Venta venta = new Venta();
        venta.setUsuario(user);
        venta.setTotal(carrito.getTotal());
        venta.setMetodoPago(metodoPago);
        venta.setCodigoPago(codigoPago);
        venta.setEstado("tarjeta".equals(metodoPago) ? "pagado" : "pendiente_pago");
        venta = ventaRepository.save(venta);

        // 4. Crear Envío
        Envio envio = new Envio();
        envio.setVenta(venta);
        envio.setDireccion(direccion);
        envio.setCiudad(ciudad);
        venta.setEnvio(envio);

        // 5. Descontar Stock y Vaciar Carrito
        for (CarritoItem item : carrito.getItems()) {
            Producto p = item.getProducto();
            p.setStockActual(p.getStockActual() - item.getCantidad());
            productoRepository.save(p);
        }

        carrito.getItems().clear();
        carritoRepository.save(carrito);

        return venta;
    }
}
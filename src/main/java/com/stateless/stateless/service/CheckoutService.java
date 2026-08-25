package com.stateless.stateless.service;

import com.stateless.stateless.model.*;
import com.stateless.stateless.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CheckoutService {
    @Autowired private VentaRepository ventaRepository;
    @Autowired private CarritoRepository carritoRepository;

    @Transactional
    public Venta procesarPedido(User user, String direccion, String ciudad, String metodoPago) {
        Carrito carrito = carritoRepository.findByUserId(user.getId()).orElseThrow();
        
        Venta venta = new Venta();
        venta.setUsuario(user);
        venta.setTotal(carrito.getTotal());
        venta.setMetodoPago(metodoPago);
        venta.setEstado("pendiente");
        venta = ventaRepository.save(venta);

        Envio envio = new Envio();
        envio.setVenta(venta);
        envio.setDireccion(direccion);
        envio.setCiudad(ciudad);
        venta.setEnvio(envio);

        carrito.getItems().clear();
        carritoRepository.save(carrito);
        return ventaRepository.save(venta);
    }
}
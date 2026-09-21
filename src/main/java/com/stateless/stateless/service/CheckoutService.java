package com.stateless.stateless.service;

import java.time.LocalDateTime;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.stateless.stateless.model.Carrito;
import com.stateless.stateless.model.CarritoItem;
import com.stateless.stateless.model.Envio;
import com.stateless.stateless.model.Producto;
import com.stateless.stateless.model.User;
import com.stateless.stateless.model.Venta;
import com.stateless.stateless.model.VentaItem;
import com.stateless.stateless.repository.CarritoRepository;
import com.stateless.stateless.repository.ProductoRepository;
import com.stateless.stateless.repository.ProductoVarianteRepository;
import com.stateless.stateless.repository.VentaItemRepository;
import com.stateless.stateless.repository.VentaRepository;

@Service
public class CheckoutService {
    @Autowired private VentaRepository ventaRepository;
    @Autowired private CarritoRepository carritoRepository;
    @Autowired private VentaItemRepository ventaItemRepository;
    @Autowired private ProductoRepository productoRepository;
    @Autowired private ProductoVarianteRepository varianteRepository;

    @Transactional
    public Venta procesarPedido(User user, String direccion, String ciudad, String metodoPago) {
        Carrito carrito = carritoRepository.findByUserId(user.getId()).orElseThrow();
        
        Venta venta = new Venta();
        venta.setUsuario(user);
        venta.setTotal(carrito.getTotal());
        venta.setMetodoPago(metodoPago);
        venta.setEstado("pagado"); // Seteo corregido
        venta.setCreatedAt(LocalDateTime.now());
        
        if ("efecty".equals(metodoPago)) {
            venta.setCodigoPago("EFY-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase());
            venta.setEstado("pendiente_pago");
        }

        Venta ventaGuardada = ventaRepository.save(venta);

        for (CarritoItem cartItem : carrito.getItems()) {
            VentaItem vItem = new VentaItem();
            vItem.setVenta(ventaGuardada);
            vItem.setProducto(cartItem.getProducto());
            vItem.setVariante(cartItem.getVariante());
            vItem.setTalla(cartItem.getTalla()); // 👉 Se traslada la talla a la venta final
            vItem.setCantidad(cartItem.getCantidad());
            vItem.setPrecioUnitario(cartItem.getPrecioUnitario());
            ventaItemRepository.save(vItem);

            Producto p = cartItem.getProducto();
            p.setStockActual(p.getStockActual() - cartItem.getCantidad());
            productoRepository.save(p);
        }

        Envio envio = new Envio();
        envio.setVenta(ventaGuardada);
        envio.setDireccion(direccion);
        envio.setCiudad(ciudad);
        envio.setEstado("pendiente");
        ventaGuardada.setEnvio(envio);

        carrito.getItems().clear();
        carritoRepository.save(carrito);

        return ventaRepository.save(ventaGuardada);
    }
}
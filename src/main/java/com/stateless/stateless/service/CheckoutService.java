package com.stateless.stateless.service;

import com.stateless.stateless.model.*;
import com.stateless.stateless.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List; // ESTA ES LA LÍNEA QUE FALTABA

@Service
public class CheckoutService {

    @Autowired private VentaRepository ventaRepository;
    @Autowired private CarritoRepository carritoRepository;
    @Autowired private ProductoRepository productoRepository;
    @Autowired private ProductoVarianteRepository varianteRepository;
    @Autowired private VentaItemRepository ventaItemRepository;

    @Transactional
    public Venta procesarPedido(User user, String direccion, String ciudad, String metodoPago) {
        Carrito carrito = carritoRepository.findByUserId(user.getId())
                .orElseThrow(() -> new RuntimeException("Carrito no encontrado"));

        // 1. Crear la Venta principal
        Venta venta = new Venta();
        venta.setUsuario(user);
        venta.setTotal(carrito.getTotal());
        venta.setMetodoPago(metodoPago);
        venta.setEstado("pendiente");
        Venta ventaGuardada = ventaRepository.save(venta);

        // 2. Crear el Envío
        Envio envio = new Envio();
        envio.setVenta(ventaGuardada);
        envio.setDireccion(direccion);
        envio.setCiudad(ciudad);
        // Sincronización manual ya que no usamos Lombok
        ventaGuardada.setEnvio(envio);

        // 3. Procesar Items y descontar Stock
        List<VentaItem> listaVentaItems = new ArrayList<>();
        
        for (CarritoItem cartItem : carrito.getItems()) {
            VentaItem vItem = new VentaItem();
            vItem.setVenta(ventaGuardada);
            vItem.setProducto(cartItem.getProducto());
            vItem.setVariante(cartItem.getVariante());
            vItem.setCantidad(cartItem.getCantidad());
            vItem.setPrecioUnitario(cartItem.getPrecioUnitario());
            listaVentaItems.add(vItem);

            // A. Descontar del total general del Producto (usando el setter manual)
            Producto p = cartItem.getProducto();
            p.setStockActual(p.getStockActual() - cartItem.getCantidad());
            productoRepository.save(p);

            // B. Descontar de la Variante (si existe)
            if (cartItem.getVariante() != null) {
                ProductoVariante pv = cartItem.getVariante();
                pv.setStockActual(pv.getStockActual() - cartItem.getCantidad());
                varianteRepository.save(pv);
            }
        }

        // Guardar todos los detalles de la venta
        ventaItemRepository.saveAll(listaVentaItems);

        // 4. Limpiar carrito (vaciar la lista y persistir)
        carrito.getItems().clear();
        carritoRepository.save(carrito);

        return ventaRepository.save(ventaGuardada);
    }
}
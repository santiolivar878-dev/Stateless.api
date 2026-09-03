package com.stateless.stateless.service;

import com.stateless.stateless.model.Envio;
import com.stateless.stateless.model.Venta;
import com.stateless.stateless.repository.EnvioRepository;
import com.stateless.stateless.repository.VentaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class EnvioService {

    @Autowired 
    private EnvioRepository envioRepository;

    @Autowired 
    private VentaRepository ventaRepository;

    @Transactional
    public void actualizarEstado(Long envioId, String nuevoEstado) {
        Envio envio = envioRepository.findById(envioId).orElseThrow();
        envio.setEstado(nuevoEstado);

        Venta venta = envio.getVenta();
        if (venta != null) {
            String estadoVenta = switch (nuevoEstado.toLowerCase().trim()) {
                case "preparando" -> "en_preparacion";
                case "en_curso" -> "enviado";
                case "entregado" -> "entregado";
                default -> venta.getEstado();
            };
            venta.setEstado(estadoVenta);
            ventaRepository.save(venta);
        }
        envioRepository.save(envio);
    }
}
package com.stateless.stateless.service;

import com.stateless.stateless.model.*;
import com.stateless.stateless.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;

@Service
public class EnvioService {

    @Autowired private EnvioRepository envioRepository;
    @Autowired private VentaRepository ventaRepository;

    @Transactional
    public void actualizarEstado(Long envioId, String nuevoEstado) {
        Envio envio = envioRepository.findById(envioId).orElseThrow();
        envio.setEstado(nuevoEstado);

        // Registrar fecha del hito dinámicamente
        switch (nuevoEstado) {
            case "confirmado" -> envio.setFecha_confirmado(LocalDateTime.now());
            case "preparando" -> envio.setFecha_preparando(LocalDateTime.now());
            case "en_curso"   -> envio.setFecha_en_curso(LocalDateTime.now());
            case "entregado"  -> envio.setFecha_entregado(LocalDateTime.now());
        }

        // Sincronizar estado de la Venta (Misma lógica match de Laravel)
        Venta venta = envio.getVenta();
        switch (nuevoEstado) {
            case "preparando" -> venta.setEstado("en_preparacion");
            case "en_curso"   -> venta.setEstado("enviado");
            case "entregado"  -> venta.setEstado("entregado");
        }

        envioRepository.save(envio);
        ventaRepository.save(venta);
    }
}
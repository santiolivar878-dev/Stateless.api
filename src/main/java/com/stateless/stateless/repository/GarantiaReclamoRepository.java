package com.stateless.stateless.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.stateless.stateless.model.GarantiaReclamo;

@Repository
public interface GarantiaReclamoRepository extends JpaRepository<GarantiaReclamo, Long> {
    List<GarantiaReclamo> findByUsuarioIdOrderByCreatedAtDesc(Long usuarioId);
    List<GarantiaReclamo> findByVentaId(Long ventaId);
    List<GarantiaReclamo> findByProveedorId(Long proveedorId);
}
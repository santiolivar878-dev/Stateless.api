package com.stateless.stateless.repository;

import com.stateless.stateless.model.Venta;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public interface VentaRepository extends JpaRepository<Venta, Long> {

    @Query("SELECT SUM(v.total) FROM Venta v")
    BigDecimal sumTotalVentas();

    @Query("SELECT v.metodoPago, COUNT(v) FROM Venta v GROUP BY v.metodoPago")
    List<Object[]> countVentasByMetodoPago();

    List<Venta> findTop5ByOrderByCreatedAtDesc();

    // Método corregido para que coincida con el campo 'usuario' del modelo Venta
    List<Venta> findByUsuarioIdOrderByCreatedAtDesc(Long usuarioId);

    List<Venta> findByEstadoOrderByCreatedAtDesc(String estado);

    List<Venta> findAllByOrderByCreatedAtDesc();

    @Query("SELECT SUM(v.total) FROM Venta v WHERE v.tipoVenta = :tipo")
    BigDecimal sumTotalByTipo(@Param("tipo") String tipo);

    @Query("SELECT SUM(v.total) FROM Venta v WHERE v.createdAt >= :inicio AND v.createdAt <= :fin")
    BigDecimal sumTotalByFecha(@Param("inicio") LocalDateTime inicio, @Param("fin") LocalDateTime fin);

    List<Venta> findAllByCreatedAtBetween(LocalDateTime desde, LocalDateTime hasta);
}
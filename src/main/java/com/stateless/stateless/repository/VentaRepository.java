package com.stateless.stateless.repository;

import com.stateless.stateless.model.Venta;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public interface VentaRepository extends JpaRepository<Venta, Long> {

    @Query("SELECT SUM(v.total) FROM Venta v")
    BigDecimal sumTotalVentas();

    @Query("SELECT v.metodoPago, COUNT(v) FROM Venta v GROUP BY v.metodoPago")
    List<Object[]> countVentasByMetodoPago();

    List<Venta> findTop5ByOrderByCreated_atDesc();

    @Query("SELECT SUM(v.total) FROM Venta v WHERE v.created_at >= ?1 AND v.created_at <= ?2")
    BigDecimal sumTotalByFecha(LocalDateTime inicio, LocalDateTime fin);
}
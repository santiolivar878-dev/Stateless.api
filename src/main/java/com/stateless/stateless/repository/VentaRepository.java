package com.stateless.stateless.repository;

import com.stateless.stateless.model.User;
import com.stateless.stateless.model.Venta;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface VentaRepository extends JpaRepository<Venta, Long> {

    @Query("SELECT SUM(v.total) FROM Venta v")
    BigDecimal sumTotalVentas();

    // Métodos para Listados (Admin y Empleado)
    List<Venta> findAllByOrderByCreatedAtDesc();
    List<Venta> findByEstadoOrderByCreatedAtDesc(String estado);
    List<Venta> findTop5ByOrderByCreatedAtDesc();

    // Métodos para el Cliente (usando el objeto User y el ID)
    List<Venta> findByUsuarioOrderByCreatedAtDesc(User usuario);
    List<Venta> findByUsuarioIdOrderByCreatedAtDesc(Long usuarioId);

    // Métodos para Reportes
    List<Venta> findAllByCreatedAtBetween(LocalDateTime desde, LocalDateTime hasta);

    @Query("SELECT SUM(v.total) FROM Venta v WHERE v.createdAt >= :inicio AND v.createdAt <= :fin")
    BigDecimal sumTotalByFecha(@Param("inicio") LocalDateTime inicio, @Param("fin") LocalDateTime fin);

    // Métodos para Dashboard
    @Query("SELECT v.metodoPago, COUNT(v) FROM Venta v GROUP BY v.metodoPago")
    List<Object[]> countVentasByMetodoPago();

    @Query("SELECT p.nombre, SUM(vi.cantidad), p.stockActual FROM VentaItem vi JOIN vi.producto p GROUP BY p.id, p.nombre, p.stockActual ORDER BY SUM(vi.cantidad) DESC")
    List<Object[]> findTopSellingProducts();
}
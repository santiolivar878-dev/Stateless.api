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

    // Métodos de listado estándar
    List<Venta> findAllByOrderByCreatedAtDesc();
    List<Venta> findByUsuarioIdOrderByCreatedAtDesc(Long usuarioId);
    List<Venta> findByEstadoOrderByCreatedAtDesc(String estado);
    List<Venta> findTop5ByOrderByCreatedAtDesc();

    // Consultas para Reportes y Dashboard (Corregidas para usar variables Java)
    @Query("SELECT SUM(v.total) FROM Venta v WHERE v.createdAt >= :inicio AND v.createdAt <= :fin")
    BigDecimal sumTotalByFecha(@Param("inicio") LocalDateTime inicio, @Param("fin") LocalDateTime fin);

    List<Venta> findAllByCreatedAtBetween(LocalDateTime desde, LocalDateTime hasta);

    @Query("SELECT v.metodoPago, COUNT(v) FROM Venta v GROUP BY v.metodoPago")
    List<Object[]> countVentasByMetodoPago();

    @Query("SELECT vi.producto.nombre, SUM(vi.cantidad) as total FROM VentaItem vi GROUP BY vi.producto.nombre ORDER BY total DESC")
    List<Object[]> findTopSellingProducts();

    // CORREGIDO: Se cambió v.tipo_venta por v.tipoVenta
    @Query("SELECT SUM(v.total) FROM Venta v WHERE v.tipoVenta = :tipo")
    BigDecimal sumTotalByTipo(@Param("tipo") String tipo);
}
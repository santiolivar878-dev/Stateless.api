package com.stateless.stateless.repository;

import com.stateless.stateless.model.Producto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.math.BigDecimal;
import java.util.List;

public interface ProductoRepository extends JpaRepository<Producto, Long> {

    @Query("SELECT p FROM Producto p WHERE p.estado = 'activo' " +
           "AND (:q IS NULL OR LOWER(p.nombre) LIKE LOWER(CONCAT('%', :q, '%')) OR LOWER(p.descripcion) LIKE LOWER(CONCAT('%', :q, '%'))) " +
           "AND (:catId IS NULL OR p.categoria.id = :catId) " +
           "AND (:minP IS NULL OR p.precio >= :minP) " +
           "AND (:maxP IS NULL OR p.precio <= :maxP) " +
           "AND (:soloDisponible = false OR p.stockActual > 0)")
    List<Producto> buscarAvanzado(
        @Param("q") String q, 
        @Param("catId") Long catId, 
        @Param("minP") BigDecimal minP, 
        @Param("maxP") BigDecimal maxP,
        @Param("soloDisponible") boolean soloDisponible
    );

    List<Producto> findByEstado(String estado);

    @Query("SELECT p FROM Producto p JOIN p.categoria c WHERE p.estado = :estado AND c.nombre = :catNombre")
    List<Producto> findByEstadoAndCategoriaNombre(@Param("estado") String estado, @Param("catNombre") String catNombre);
    
    List<Producto> findTop3ByCategoriaIdAndIdNot(Long categoriaId, Long id);

    @Query("SELECT p FROM Producto p WHERE p.stockActual <= p.stockMinimo")
    List<Producto> findStockBajo();

    @Query("SELECT p FROM Producto p WHERE p.stockActual = 0")
    List<Producto> findSinStock();
}
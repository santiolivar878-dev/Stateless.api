package com.stateless.stateless.repository;

import com.stateless.stateless.model.Producto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import java.util.List;

public interface ProductoRepository extends JpaRepository<Producto, Long> {
    
    // Filtros de Laravel: stock_bajo, sin_stock, activo, inactivo
    @Query("SELECT p FROM Producto p WHERE p.stockActual <= p.stockMinimo")
    List<Producto> findStockBajo();

    @Query("SELECT p FROM Producto p WHERE p.stockActual = 0")
    List<Producto> findSinStock();

    List<Producto> findByEstado(String estado);
}
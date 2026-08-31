package com.stateless.stateless.repository;

import com.stateless.stateless.model.Carrito;
import com.stateless.stateless.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CarritoRepository extends JpaRepository<Carrito, Long> {
    // Spring Data JPA generará automáticamente la consulta buscando por el id del usuario
    Optional<Carrito> findByUserId(Long userId);
    
    // También puedes dejar este si lo necesitas en otros lados:
    Optional<Carrito> findByUser(User user);
}
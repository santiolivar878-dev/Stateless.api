package com.stateless.stateless.repository;

import com.stateless.stateless.model.Envio;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EnvioRepository extends JpaRepository<Envio, Long> {
    long countByEstado(String estado);
}
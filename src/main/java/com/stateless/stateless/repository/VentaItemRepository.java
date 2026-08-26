package com.stateless.stateless.repository;

import com.stateless.stateless.model.VentaItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface VentaItemRepository extends JpaRepository<VentaItem, Long> {
}
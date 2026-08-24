package com.stateless.stateless.model;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;

@Entity
@Table(name = "carrito_items")
@Getter @Setter @NoArgsConstructor
public class CarritoItem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Integer cantidad;

    @Column(name = "precio_unitario")
    private BigDecimal precioUnitario;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "carrito_id")
    private Carrito carrito;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "producto_id")
    private Producto producto;

    // Equivalente al método subtotal() en CarritoItem.php
    public BigDecimal getSubtotal() {
        return precioUnitario.multiply(new BigDecimal(cantidad));
    }
}

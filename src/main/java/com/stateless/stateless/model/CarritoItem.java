package com.stateless.stateless.model;

import jakarta.persistence.*;
import java.math.BigDecimal;

@Entity
@Table(name = "carrito_items")
public class CarritoItem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Integer cantidad;
    @Column(name = "precio_unitario")
    private BigDecimal precioUnitario;

    @ManyToOne
    @JoinColumn(name = "carrito_id")
    private Carrito carrito;

    @ManyToOne
    @JoinColumn(name = "producto_id")
    private Producto producto;

    public CarritoItem() {}
    public Long getId() { return id; }
    public Integer getCantidad() { return cantidad; }
    public void setCantidad(Integer cantidad) { this.cantidad = cantidad; }
    public BigDecimal getPrecioUnitario() { return precioUnitario; }
    public void setPrecioUnitario(BigDecimal p) { this.precioUnitario = p; }
    public void setCarrito(Carrito c) { this.carrito = c; }
    public Producto getProducto() { return producto; }
    public void setProducto(Producto p) { this.producto = p; }
    public BigDecimal getSubtotal() { 
        return precioUnitario != null ? precioUnitario.multiply(new BigDecimal(cantidad)) : BigDecimal.ZERO; 
    }
}
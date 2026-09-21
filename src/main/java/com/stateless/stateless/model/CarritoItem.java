package com.stateless.stateless.model;

import java.math.BigDecimal;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "carrito_items")
public class CarritoItem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "carrito_id")
    private Carrito carrito;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "producto_id")
    private Producto producto;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "variante_id")
    private ProductoVariante variante;

    // 👉 NUEVO CAMPO TALLA:
    private String talla;

    private Integer cantidad;

    @Column(name = "precio_unitario")
    private BigDecimal precioUnitario;

    // Getters y Setters de talla:
    public String getTalla() { return talla; }
    public void setTalla(String talla) { this.talla = talla; }

    public CarritoItem() {}
    public Long getId() { return id; }
    public Integer getCantidad() { return cantidad; }
    public void setCantidad(Integer c) { this.cantidad = c; }
    public BigDecimal getPrecioUnitario() { return precioUnitario; }
    public void setPrecioUnitario(BigDecimal p) { this.precioUnitario = p; }
    public void setCarrito(Carrito c) { this.carrito = c; }
    public Producto getProducto() { return producto; }
    public void setProducto(Producto p) { this.producto = p; }
    public ProductoVariante getVariante() { return variante; }
    public void setVariante(ProductoVariante v) { this.variante = v; }
    public BigDecimal getSubtotal() { 
        return precioUnitario != null ? precioUnitario.multiply(new BigDecimal(cantidad)) : BigDecimal.ZERO; 
    }
}
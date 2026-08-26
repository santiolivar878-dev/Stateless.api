package com.stateless.stateless.model;

import jakarta.persistence.*;
import java.math.BigDecimal;

@Entity
@Table(name = "venta_items")
public class VentaItem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne @JoinColumn(name = "venta_id")
    private Venta venta;

    @ManyToOne @JoinColumn(name = "producto_id")
    private Producto producto;

    @ManyToOne @JoinColumn(name = "variante_id") // Para saber qué color se compró
    private ProductoVariante variante;

    private Integer cantidad;
    private BigDecimal precioUnitario;

    public VentaItem() {}
    public Long getId() { return id; }
    public void setVenta(Venta v) { this.venta = v; }
    public Producto getProducto() { return producto; }
    public void setProducto(Producto p) { this.producto = p; }
    public ProductoVariante getVariante() { return variante; }
    public void setVariante(ProductoVariante v) { this.variante = v; }
    public Integer getCantidad() { return cantidad; }
    public void setCantidad(Integer c) { this.cantidad = c; }
    public void setPrecioUnitario(BigDecimal p) { this.precioUnitario = p; }
}
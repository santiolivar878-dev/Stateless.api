package com.stateless.stateless.model;

import jakarta.persistence.*;

@Entity
@Table(name = "producto_variantes")
public class ProductoVariante {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String color;
    private String hex;
    private String imagen;
    @Column(name = "stock_actual")
    private Integer stockActual = 0;

    @ManyToOne
    @JoinColumn(name = "producto_id")
    private Producto producto;

    public ProductoVariante() {}
    public Long getId() { return id; }
    public String getColor() { return color; }
    public void setColor(String c) { this.color = c; }
    public String getHex() { return hex; }
    public void setHex(String h) { this.hex = h; }
    public String getImagen() { return imagen; }
    public void setImagen(String i) { this.imagen = i; }
    public Integer getStockActual() { return stockActual; }
    public void setStockActual(Integer s) { this.stockActual = s; }
    public Producto getProducto() { return producto; }
    public void setProducto(Producto p) { this.producto = p; }
}
package com.stateless.stateless.model;

import jakarta.persistence.*;
import java.util.List;

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

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "producto_id")
    private Producto producto;

    @OneToMany(mappedBy = "variante")
    private List<VentaItem> ventaItems;

    public ProductoVariante() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getColor() { return color; }
    public void setColor(String color) { this.color = color; }
    public String getHex() { return hex; }
    public void setHex(String hex) { this.hex = hex; }
    public String getImagen() { return imagen; }
    public void setImagen(String imagen) { this.imagen = imagen; }
    public Integer getStockActual() { return stockActual; }
    public void setStockActual(Integer stockActual) { this.stockActual = stockActual; }
    public Producto getProducto() { return producto; }
    public void setProducto(Producto producto) { this.producto = producto; }
}
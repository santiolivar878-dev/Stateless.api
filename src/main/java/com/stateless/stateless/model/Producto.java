package com.stateless.stateless.model;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "productos")
public class Producto {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String nombre;
    private String descripcion;
    private BigDecimal precio;
    private String estado = "activo";
    private String imagen;

    @Column(name = "stock_actual")
    private Integer stockActual;

    @Column(name = "stock_minimo")
    private Integer stockMinimo = 5;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "categoria_id")
    private Categoria categoria;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "proveedor_id")
    private Proveedor proveedor;

    @OneToMany(mappedBy = "producto", cascade = CascadeType.ALL)
    private List<ProductoImagen> imagenes = new ArrayList<>();

    @OneToMany(mappedBy = "producto", cascade = CascadeType.ALL)
    private List<ProductoVariante> variantes = new ArrayList<>();

    public Producto() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getNombre() { return nombre; }
    public void setNombre(String n) { this.nombre = n; }
    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String d) { this.descripcion = d; }
    public BigDecimal getPrecio() { return precio; }
    public void setPrecio(BigDecimal p) { this.precio = p; }
    public String getEstado() { return estado; }
    public void setEstado(String e) { this.estado = e; }
    public String getImagen() { return imagen; }
    public void setImagen(String i) { this.imagen = i; }
    public Integer getStockActual() { return stockActual; }
    public void setStockActual(Integer s) { this.stockActual = s; }
    public Integer getStockMinimo() { return stockMinimo; }
    public void setStockMinimo(Integer s) { this.stockMinimo = s; }
    public Categoria getCategoria() { return categoria; }
    public void setCategoria(Categoria c) { this.categoria = c; }
    public Proveedor getProveedor() { return proveedor; }
    public void setProveedor(Proveedor p) { this.proveedor = p; }
    public List<ProductoImagen> getImagenes() { return imagenes; }
    public List<ProductoVariante> getVariantes() { return variantes; }
}
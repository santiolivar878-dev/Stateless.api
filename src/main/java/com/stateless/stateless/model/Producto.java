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
    private Integer stock_actual;
    private Integer stock_minimo = 5;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "categoria_id")
    private Categoria categoria;

    @OneToMany(mappedBy = "producto", cascade = CascadeType.ALL)
    private List<ProductoImagen> imagenes = new ArrayList<>();

    @OneToMany(mappedBy = "producto", cascade = CascadeType.ALL)
    private List<ProductoVariante> variantes = new ArrayList<>();

    public Producto() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }
    public BigDecimal getPrecio() { return precio; }
    public void setPrecio(BigDecimal precio) { this.precio = precio; }
    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }
    public String getImagen() { return imagen; }
    public void setImagen(String imagen) { this.imagen = imagen; }
    public Integer getStockActual() { return stock_actual; }
    public void setStockActual(Integer stock_actual) { this.stock_actual = stock_actual; }
    public Integer getStockMinimo() { return stock_minimo; }
    public void setStockMinimo(Integer stock_minimo) { this.stock_minimo = stock_minimo; }
    public Categoria getCategoria() { return categoria; }
    public void setCategoria(Categoria categoria) { this.categoria = categoria; }
    public List<ProductoImagen> getImagenes() { return imagenes; }
    public void setImagenes(List<ProductoImagen> imagenes) { this.imagenes = imagenes; }
    public List<ProductoVariante> getVariantes() { return variantes; }
    public void setVariantes(List<ProductoVariante> variantes) { this.variantes = variantes; }
}
package com.stateless.stateless.model;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.util.List;

@Entity
@Table(name = "productos")
@Getter @Setter @NoArgsConstructor
public class Producto {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nombre;
    @Column(columnDefinition = "TEXT")
    private String descripcion;
    private BigDecimal precio;
    private String estado = "activo";
    private String imagen; // Nombre del archivo en /images/

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
    @OrderBy("orden ASC")
    private List<ProductoImagen> imagenes;

    @OneToMany(mappedBy = "producto", cascade = CascadeType.ALL)
    private List<ProductoVariante> variantes;
}

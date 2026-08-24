package com.stateless.stateless.model;
public package com.stateless.stateless.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "producto_imagenes")
@Getter @Setter @NoArgsConstructor
public class ProductoImagen {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String imagen;
    private Integer orden = 0;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "producto_id")
    private Producto producto;
}
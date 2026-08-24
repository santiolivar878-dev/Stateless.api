package com.stateless.stateless.model;

import jakarta.persistence.*;
import lombok.*;
import java.util.List;

@Entity
@Table(name = "proveedores")
@Getter @Setter @NoArgsConstructor
public class Proveedor {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nombre;
    private String telefono;
    private String correo;
    private boolean estado = true;

    @OneToMany(mappedBy = "proveedor")
    private List<Producto> productos;
}
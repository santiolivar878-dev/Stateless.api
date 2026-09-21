package com.stateless.stateless.model;

import jakarta.persistence.*;
import java.util.List;

@Entity
@Table(name = "proveedores")
public class Proveedor {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Razón Social o Nombre Comercial
    private String nombre;

    // Identificación Tributaria
    @Column(name = "nit", length = 30)
    private String nit;

    // Asesor o Persona de Contacto
    @Column(name = "contacto_nombre", length = 120)
    private String contactoNombre;

    private String telefono;
    private String correo;

    // Ubicación de Planta / Taller
    private String direccion;
    private String ciudad;

    // Tipo de Insumo / Servicio suministrado
    // (Ej: "Telas & Tejidos", "Confección Textil", "Estampado & Lavandería", "Insumos & Etiquetas")
    @Column(name = "tipo_insumo", length = 80)
    private String tipoInsumo = "Telas & Tejidos";

    // Calificación de Calidad (1 al 5)
    @Column(name = "calificacion_calidad")
    private Integer calificacionCalidad = 5;

    private boolean estado = true;

    @OneToMany(mappedBy = "proveedor")
    private List<Producto> productos;

    public Proveedor() {}

    // GETTERS Y SETTERS
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getNit() { return nit; }
    public void setNit(String nit) { this.nit = nit; }

    public String getContactoNombre() { return contactoNombre; }
    public void setContactoNombre(String contactoNombre) { this.contactoNombre = contactoNombre; }

    public String getTelefono() { return telefono; }
    public void setTelefono(String telefono) { this.telefono = telefono; }

    public String getCorreo() { return correo; }
    public void setCorreo(String correo) { this.correo = correo; }

    public String getDireccion() { return direccion; }
    public void setDireccion(String direccion) { this.direccion = direccion; }

    public String getCiudad() { return ciudad; }
    public void setCiudad(String ciudad) { this.ciudad = ciudad; }

    public String getTipoInsumo() { return tipoInsumo; }
    public void setTipoInsumo(String tipoInsumo) { this.tipoInsumo = tipoInsumo; }

    public Integer getCalificacionCalidad() { return calificacionCalidad; }
    public void setCalificacionCalidad(Integer calificacionCalidad) { this.calificacionCalidad = calificacionCalidad; }

    public boolean isEstado() { return estado; }
    public void setEstado(boolean estado) { this.estado = estado; }

    public List<Producto> getProductos() { return productos; }
    public void setProductos(List<Producto> productos) { this.productos = productos; }
}
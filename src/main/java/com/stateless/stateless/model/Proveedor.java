package com.stateless.stateless.model;

import jakarta.persistence.*;

@Entity
@Table(name = "proveedores")
public class Proveedor {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String nombre;
    private String telefono;
    private String correo;
    private boolean estado = true;

    public Proveedor() {}
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getNombre() { return nombre; }
    public void setNombre(String val) { this.nombre = val; }
    public String getTelefono() { return telefono; }
    public void setTelefono(String val) { this.telefono = val; }
    public String getCorreo() { return correo; }
    public void setCorreo(String val) { this.correo = val; }
    public boolean isEstado() { return estado; }
    public void setEstado(boolean val) { this.estado = val; }
}
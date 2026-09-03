package com.stateless.stateless.model;

import java.time.LocalDateTime;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "envios")
public class Envio {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "venta_id")
    private Venta venta;

    private String direccion;
    private String ciudad;
    private String estado = "pendiente";
    private String transportadora;
    private String numeroGuia;

    private LocalDateTime fecha_envio;
    private LocalDateTime fecha_confirmado;
    private LocalDateTime fecha_preparando;
    private LocalDateTime fecha_en_curso;
    private LocalDateTime fecha_entregado;

    public Envio() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Venta getVenta() { return venta; }
    public void setVenta(Venta venta) { this.venta = venta; }
    public String getDireccion() { return direccion; }
    public void setDireccion(String direccion) { this.direccion = direccion; }
    public String getCiudad() { return ciudad; }
    public void setCiudad(String ciudad) { this.ciudad = ciudad; }
    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }
    public String getTransportadora() { return transportadora; }
    public void setTransportadora(String transportadora) { this.transportadora = transportadora; }
    public String getNumeroGuia() { return numeroGuia; }
    public void setNumeroGuia(String numeroGuia) { this.numeroGuia = numeroGuia; }

    // Métodos de fecha que pedía el error
    public void setFecha_confirmado(LocalDateTime f) { this.fecha_confirmado = f; }
    public void setFecha_preparando(LocalDateTime f) { this.fecha_preparando = f; }
    public void setFecha_en_curso(LocalDateTime f) { this.fecha_en_curso = f; }
    public void setFecha_entregado(LocalDateTime f) { this.fecha_entregado = f; }

    public int getPasoActual() {
        if (this.estado == null) {
            return 1;
        }
        return switch (this.estado.toLowerCase().trim()) {
            case "confirmado", "pago_confirmado", "pagado" -> 2;
            case "preparando", "en_preparacion" -> 3;
            case "en_curso", "enviado" -> 4;
            case "entregado" -> 5;
            default -> 1;
        };
    }
}
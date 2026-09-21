package com.stateless.stateless.model;

import java.time.LocalDateTime;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "garantias_reclamos")
public class GarantiaReclamo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "venta_id")
    private Venta venta;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "producto_id")
    private Producto producto;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "proveedor_id")
    private Proveedor proveedor;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_id")
    private User usuario;

    @Column(name = "tipo_incidencia")
    private String tipoIncidencia; // "PRENDA_DANADA", "COSTURA_ROTA", "DECOLORACION", "ERROR_TALLA", "SOLICITUD_CANCELACION"

    @Column(columnDefinition = "TEXT")
    private String descripcionCliente;

    private String estado = "RADICADO"; // RADICADO, EN_AUDITORIA, TRASLADADO_A_PROVEEDOR, APROBADO_GARANTIA, RECHAZADO

    @Column(columnDefinition = "TEXT")
    private String respuestaAdmin;

    @Column(name = "notificado_proveedor")
    private boolean notificadoProveedor = false;

    @Column(name = "created_at")
    private LocalDateTime createdAt = LocalDateTime.now();

    public GarantiaReclamo() {}

    // Getters y Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Venta getVenta() { return venta; }
    public void setVenta(Venta venta) { this.venta = venta; }
    public Producto getProducto() { return producto; }
    public void setProducto(Producto producto) { this.producto = producto; }
    public Proveedor getProveedor() { return proveedor; }
    public void setProveedor(Proveedor proveedor) { this.proveedor = proveedor; }
    public User getUsuario() { return usuario; }
    public void setUsuario(User usuario) { this.usuario = usuario; }
    public String getTipoIncidencia() { return tipoIncidencia; }
    public void setTipoIncidencia(String tipoIncidencia) { this.tipoIncidencia = tipoIncidencia; }
    public String getDescripcionCliente() { return descripcionCliente; }
    public void setDescripcionCliente(String descripcionCliente) { this.descripcionCliente = descripcionCliente; }
    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }
    public String getRespuestaAdmin() { return respuestaAdmin; }
    public void setRespuestaAdmin(String respuestaAdmin) { this.respuestaAdmin = respuestaAdmin; }
    public boolean isNotificadoProveedor() { return notificadoProveedor; }
    public void setNotificadoProveedor(boolean notificadoProveedor) { this.notificadoProveedor = notificadoProveedor; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
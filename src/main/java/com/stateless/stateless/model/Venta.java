package com.stateless.stateless.model;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "ventas")
public class Venta {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "tipo_venta")
    private String tipoVenta = "online";

    @Column(name = "metodo_pago")
    private String metodoPago;

    private BigDecimal total;

    @Column(name = "codigo_pago")
    private String codigoPago;

    private String estado = "pendiente";

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User usuario;

    @OneToOne(mappedBy = "venta", cascade = CascadeType.ALL)
    private Envio envio;

    @OneToMany(mappedBy = "venta", cascade = CascadeType.ALL)
    private List<VentaItem> items = new ArrayList<>();

        @Column(name = "motivo_cancelacion")
    private String motivoCancelacion;

    @Column(name = "justificacion_cancelacion", columnDefinition = "TEXT")
    private String justificacionCancelacion;

    @Column(name = "cancelled_at")
    private LocalDateTime cancelledAt;

    @Column(name = "created_at")
    private LocalDateTime createdAt; // Nombre exacto para el Repositorio

    public Venta() {}

    // GETTERS Y SETTERS MANUALES
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getTipoVenta() { return tipoVenta; }
    public void setTipoVenta(String val) { this.tipoVenta = val; }
    public String getMetodoPago() { return metodoPago; }
    public void setMetodoPago(String val) { this.metodoPago = val; }
    public BigDecimal getTotal() { return total; }
    public void setTotal(BigDecimal total) { this.total = total; }
    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }
    public User getUsuario() { return usuario; }
    public void setUsuario(User usuario) { this.usuario = usuario; }
    public Envio getEnvio() { return envio; }
    public void setEnvio(Envio envio) { this.envio = envio; }
    public List<VentaItem> getItems() { return items; }
    public void setItems(List<VentaItem> items) { this.items = items; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    public String getCodigoPago() { return codigoPago; }
    public void setCodigoPago(String val) { this.codigoPago = val; }
    public String getMotivoCancelacion() { return motivoCancelacion; }
    public void setMotivoCancelacion(String motivoCancelacion) { this.motivoCancelacion = motivoCancelacion; }
    public String getJustificacionCancelacion() { return justificacionCancelacion; }
    public void setJustificacionCancelacion(String justificacionCancelacion) { this.justificacionCancelacion = justificacionCancelacion; }
    public LocalDateTime getCancelledAt() { return cancelledAt; }
    public void setCancelledAt(LocalDateTime cancelledAt) { this.cancelledAt = cancelledAt; }
}
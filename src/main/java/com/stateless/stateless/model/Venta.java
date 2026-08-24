package com.stateless.stateless.model;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "ventas")
@Getter @Setter @NoArgsConstructor
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
    private String codigoPago; // Para Efecty

    private String estado = "pendiente";

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User usuario;

    @OneToOne(mappedBy = "venta", cascade = CascadeType.ALL)
    private Envio envio;

    @OneToMany(mappedBy = "venta", cascade = CascadeType.ALL)
    private List<VentaItem> items;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime created_at;
}
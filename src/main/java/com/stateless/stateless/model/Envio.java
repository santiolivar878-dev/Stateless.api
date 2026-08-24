package com.stateless.stateless.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;
import java.util.Map;

@Entity
@Table(name = "envios")
@Getter @Setter @NoArgsConstructor
public class Envio {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "venta_id")
    private Venta venta;

    private String direccion;
    private String ciudad;
    private String estado = "pendiente"; // pendiente, confirmado, preparando, en_curso, entregado

    @Column(name = "fecha_envio")
    private LocalDateTime fechaEnvio;

    // Hitos de tracking (Agregados en la migración de Laravel 2026_07_02)
    private LocalDateTime fecha_confirmado;
    private LocalDateTime fecha_preparando;
    private LocalDateTime fecha_en_curso;
    private LocalDateTime fecha_entregado;

    // Equivalente a const ESTADOS en Laravel
    public static final Map<String, Integer> ESTADOS_MAP = Map.of(
        "pendiente", 1,
        "confirmado", 2,
        "preparando", 3,
        "en_curso", 4,
        "entregado", 5
    );

    public Integer getPasoActual() {
        return ESTADOS_MAP.getOrDefault(this.estado, 1);
    }
}
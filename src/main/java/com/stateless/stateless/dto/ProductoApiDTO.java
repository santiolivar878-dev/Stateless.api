package com.stateless.stateless.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.math.BigDecimal;
import java.util.List;

public record ProductoApiDTO(
    Long id,
    String nombre,
    String descripcion,
    BigDecimal precio,
    String imagen,
    @JsonProperty("stock_actual") Integer stockActual,
    String estado,
    CategoriaSimpleDTO categoria,
    List<String> imagenes
) {}
package com.example.StockAndesBackend.dto;

import java.math.BigDecimal;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ProductoResponseDTO {
    private Long id;
    private String codigo;
    private String nombre;
    private BigDecimal costoUnitario;
    private Integer stock;
    private Integer stockMinimo;
    private Boolean estado;
    private Long categoriaId;
    private String categoriaNombre;
}

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
public class AreaResponseDTO {
    private Long id;
    private String codigo;
    private String nombre;
    private String responsable;
    private String email;
    private BigDecimal presupuestoMensual;
    private Boolean estado;
}

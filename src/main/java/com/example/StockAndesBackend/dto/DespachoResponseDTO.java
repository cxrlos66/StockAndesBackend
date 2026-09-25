package com.example.StockAndesBackend.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import com.example.StockAndesBackend.enums.EstadoDespacho;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class DespachoResponseDTO {
    private Long id;
    private LocalDateTime fecha;
    private Long areaId;
    private String areaCodigo;
    private String areaNombre;
    private String observacion;
    private EstadoDespacho estado;
    private Integer totalUnidades;
    private BigDecimal montoTotal;
    private List<DetalleDespachoResponseDTO> detalles;
}

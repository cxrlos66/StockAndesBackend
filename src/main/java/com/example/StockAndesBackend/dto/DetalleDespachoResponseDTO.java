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
public class DetalleDespachoResponseDTO {
    private Long productoId;
    private String codigoProducto;
    private String producto;
    private Integer cantidad;
    private BigDecimal costoUnitario;
    private BigDecimal importe;
}

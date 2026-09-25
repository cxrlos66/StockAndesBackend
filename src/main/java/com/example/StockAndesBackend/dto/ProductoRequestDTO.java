package com.example.StockAndesBackend.dto;

import java.math.BigDecimal;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ProductoRequestDTO {

    @NotBlank(message = "El codigo es obligatorio")
    @Pattern(
        regexp = "^[A-Z]{3}-[0-9]{3}$",
        message = "El codigo debe tener el formato OFI-001"
    )
    private String codigo;

    @NotBlank(message = "El nombre es obligatorio")
    @Size(min = 3, max = 150,
          message = "El nombre debe tener entre 3 y 150 caracteres")
    private String nombre;

    @NotNull(message = "El costo unitario es obligatorio")
    @DecimalMin(
        value = "0.01",
        message = "El costo unitario debe ser mayor que cero"
    )
    private BigDecimal costoUnitario;

    @NotNull(message = "El stock es obligatorio")
    @Min(value = 0, message = "El stock no puede ser negativo")
    private Integer stock;

    @NotNull(message = "El stock minimo es obligatorio")
    @Min(value = 0, message = "El stock minimo no puede ser negativo")
    private Integer stockMinimo;

    @NotNull(message = "El estado es obligatorio")
    private Boolean estado;

    @NotNull(message = "La categoria es obligatoria")
    @Positive(message = "La categoria debe ser valida")
    private Long categoriaId;
}
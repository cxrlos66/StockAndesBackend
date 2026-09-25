package com.example.StockAndesBackend.dto;

import java.math.BigDecimal;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AreaRequestDTO {

    @NotBlank(message = "El codigo es obligatorio")
    @Pattern(regexp = "^AR[0-9]{2}$", message = "El codigo debe tener el formato AR01")
    private String codigo;

    @NotBlank(message = "El nombre es obligatorio")
    private String nombre;

    @NotBlank(message = "El responsable es obligatorio")
    private String responsable;

    @NotBlank(message = "El email es obligatorio")
    @Email(message = "El email no tiene un formato valido")
    private String email;

    @NotNull(message = "El presupuesto mensual es obligatorio")
    @DecimalMin(value = "0.00", inclusive = true,
            message = "El presupuesto mensual debe ser mayor o igual a cero")
    private BigDecimal presupuestoMensual;

    @NotNull(message = "El estado es obligatorio")
    private Boolean estado;
}
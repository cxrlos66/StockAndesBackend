package com.example.StockAndesBackend.dto;

import java.util.List;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
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
public class DespachoRequestDTO {
    @NotNull(message = "El area es obligatoria")
    @Positive(message = "El area debe ser valida")
    private Long areaId;

    @Size(max = 200, message = "La observacion no puede superar 200 caracteres")
    private String observacion;

    @NotEmpty(message = "El despacho debe tener por lo menos un producto")
    private List<@Valid DetalleDespachoRequestDTO> detalles;
}

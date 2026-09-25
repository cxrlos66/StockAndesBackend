package com.example.StockAndesBackend.dto;

import java.time.LocalDateTime;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ErrorResponseDTO {
    private LocalDateTime fechaHora;
    private Integer codigoEstado;
    private String error;
    private String mensaje;
    private String ruta;

    @JsonProperty("validationErrors")
    private Map<String, String> erroresValidacion;
}

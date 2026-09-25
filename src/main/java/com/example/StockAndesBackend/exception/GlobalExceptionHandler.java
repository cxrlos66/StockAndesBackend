package com.example.StockAndesBackend.exception;

import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.example.StockAndesBackend.dto.ErrorResponseDTO;

import jakarta.servlet.http.HttpServletRequest;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger registro =
            LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(RecursoNoEncontradoException.class)
    public ResponseEntity<ErrorResponseDTO> manejarNoEncontrado(
            RecursoNoEncontradoException excepcion,
            HttpServletRequest solicitud) {

        ErrorResponseDTO error = construirError(
                HttpStatus.NOT_FOUND,
                excepcion.getMessage(),
                solicitud.getRequestURI(),
                null
        );

        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(error);
    }

    @ExceptionHandler(ReglaNegocioException.class)
    public ResponseEntity<ErrorResponseDTO> manejarReglaNegocio(
            ReglaNegocioException excepcion,
            HttpServletRequest solicitud) {

        registro.warn(
                "Regla de negocio incumplida en {}: {}",
                solicitud.getRequestURI(),
                excepcion.getMessage()
        );

        ErrorResponseDTO error = construirError(
                HttpStatus.CONFLICT,
                excepcion.getMessage(),
                solicitud.getRequestURI(),
                null
        );

        return ResponseEntity
                .status(HttpStatus.CONFLICT)
                .body(error);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponseDTO> manejarValidacion(
            MethodArgumentNotValidException excepcion,
            HttpServletRequest solicitud) {

        Map<String, String> errores = new LinkedHashMap<>();

        excepcion.getBindingResult()
                .getFieldErrors()
                .forEach(errorCampo ->
                        errores.put(
                                errorCampo.getField(),
                                errorCampo.getDefaultMessage()
                        )
                );

        ErrorResponseDTO error = construirError(
                HttpStatus.BAD_REQUEST,
                "Existen errores de validacion",
                solicitud.getRequestURI(),
                errores
        );

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(error);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ErrorResponseDTO> manejarCuerpoInvalido(
            HttpMessageNotReadableException excepcion,
            HttpServletRequest solicitud) {

        ErrorResponseDTO error = construirError(
                HttpStatus.BAD_REQUEST,
                "El cuerpo de la solicitud es obligatorio o tiene un formato incorrecto",
                solicitud.getRequestURI(),
                null
        );

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(error);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorResponseDTO> manejarSolicitudInvalida(
            IllegalArgumentException excepcion,
            HttpServletRequest solicitud) {

        ErrorResponseDTO error = construirError(
                HttpStatus.BAD_REQUEST,
                excepcion.getMessage(),
                solicitud.getRequestURI(),
                null
        );

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(error);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponseDTO> manejarErrorGeneral(
            Exception excepcion,
            HttpServletRequest solicitud) {

        registro.error(
                "Error interno en {}",
                solicitud.getRequestURI(),
                excepcion
        );

        ErrorResponseDTO error = construirError(
                HttpStatus.INTERNAL_SERVER_ERROR,
                "Ocurrio un error interno en el servidor",
                solicitud.getRequestURI(),
                null
        );

        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(error);
    }

    private ErrorResponseDTO construirError(
            HttpStatus estado,
            String mensaje,
            String ruta,
            Map<String, String> erroresValidacion) {

        return new ErrorResponseDTO(
                LocalDateTime.now(),
                estado.value(),
                estado.getReasonPhrase(),
                mensaje,
                ruta,
                erroresValidacion
        );
    }
}
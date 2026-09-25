package com.example.StockAndesBackend.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.StockAndesBackend.dto.DespachoRequestDTO;
import com.example.StockAndesBackend.dto.DespachoResponseDTO;
import com.example.StockAndesBackend.service.service.DespachoService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/v1/despachos")
public class DespachoController {
    private final DespachoService despachoService;

    public DespachoController(DespachoService despachoService) {
        this.despachoService = despachoService;
    }

    @PostMapping
    public ResponseEntity<DespachoResponseDTO> registrar(@Valid @RequestBody DespachoRequestDTO solicitud) {
        return ResponseEntity.status(201).body(despachoService.registrar(solicitud));
    }

    @GetMapping
    public ResponseEntity<List<DespachoResponseDTO>> listar() {
        return ResponseEntity.ok(despachoService.listar());
    }

    @GetMapping("/{id}")
    public ResponseEntity<DespachoResponseDTO> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(despachoService.buscarPorId(id));
    }

    @PatchMapping("/{id}/anular")
    public ResponseEntity<DespachoResponseDTO> anular(@PathVariable Long id) {
        return ResponseEntity.ok(despachoService.anular(id));
    }
}

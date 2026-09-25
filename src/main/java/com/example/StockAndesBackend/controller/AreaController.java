package com.example.StockAndesBackend.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.StockAndesBackend.dto.AreaRequestDTO;
import com.example.StockAndesBackend.dto.AreaResponseDTO;
import com.example.StockAndesBackend.service.service.AreaService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/v1/areas")
public class AreaController {
    private final AreaService areaService;

    public AreaController(AreaService areaService) {
        this.areaService = areaService;
    }

    @PostMapping
    public ResponseEntity<AreaResponseDTO> crear(@Valid @RequestBody AreaRequestDTO solicitud) {
        return ResponseEntity.status(201).body(areaService.crear(solicitud));
    }

    @GetMapping
    public ResponseEntity<Iterable<AreaResponseDTO>> listar() {
        return ResponseEntity.ok(areaService.listar());
    }

    @GetMapping("/{id}")
    public ResponseEntity<AreaResponseDTO> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(areaService.buscarPorId(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<AreaResponseDTO> actualizar(
            @PathVariable Long id,
            @Valid @RequestBody AreaRequestDTO solicitud) {
        return ResponseEntity.ok(areaService.actualizar(id, solicitud));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        areaService.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}

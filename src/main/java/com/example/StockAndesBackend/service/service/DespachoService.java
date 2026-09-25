package com.example.StockAndesBackend.service.service;

import java.util.List;

import com.example.StockAndesBackend.dto.DespachoRequestDTO;
import com.example.StockAndesBackend.dto.DespachoResponseDTO;

public interface DespachoService {
    DespachoResponseDTO registrar(DespachoRequestDTO solicitud);
    DespachoResponseDTO buscarPorId(Long id);
    List<DespachoResponseDTO> listar();
    DespachoResponseDTO anular(Long id);
}

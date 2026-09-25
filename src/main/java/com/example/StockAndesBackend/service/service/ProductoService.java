package com.example.StockAndesBackend.service.service;

import java.util.List;

import com.example.StockAndesBackend.dto.ProductoRequestDTO;
import com.example.StockAndesBackend.dto.ProductoResponseDTO;
import com.example.StockAndesBackend.service.generic.CrudService;

public interface ProductoService extends CrudService<ProductoRequestDTO, ProductoResponseDTO, Long> {
    List<ProductoResponseDTO> listarPorCategoria(Long categoriaId);
    List<ProductoResponseDTO> buscar(String nombre, Long categoriaId, Boolean stockBajo, String orden, String direccion);
}

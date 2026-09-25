package com.example.StockAndesBackend.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.StockAndesBackend.entity.DetalleDespacho;

public interface DetalleDespachoRepository extends JpaRepository<DetalleDespacho, Long> {
    boolean existsByProductoId(Long productoId);
}

package com.example.StockAndesBackend.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.StockAndesBackend.entity.Area;

public interface AreaRepository extends JpaRepository<Area, Long> {
    boolean existsByCodigoIgnoreCase(String codigo);
    boolean existsByCodigoIgnoreCaseAndIdNot(String codigo, Long id);
    boolean existsByNombreIgnoreCase(String nombre);
    boolean existsByNombreIgnoreCaseAndIdNot(String nombre, Long id);
}

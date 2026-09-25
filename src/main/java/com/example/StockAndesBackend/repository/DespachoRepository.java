package com.example.StockAndesBackend.repository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.example.StockAndesBackend.entity.Despacho;
import com.example.StockAndesBackend.enums.EstadoDespacho;

public interface DespachoRepository extends JpaRepository<Despacho, Long> {
    List<Despacho> findAllByOrderByFechaDesc();
    boolean existsByAreaId(Long areaId);

    @Query("select coalesce(sum(d.montoTotal), 0) from Despacho d "
            + "where d.area.id = :areaId "
            + "and d.estado = :estado "
            + "and d.fecha >= :inicioMes and d.fecha < :finMes")
    BigDecimal sumarMontoDelMes(
            @Param("areaId") Long areaId,
            @Param("estado") EstadoDespacho estado,
            @Param("inicioMes") LocalDateTime inicioMes,
            @Param("finMes") LocalDateTime finMes);
}

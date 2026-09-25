package com.example.StockAndesBackend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.example.StockAndesBackend.entity.Categoria;

public interface CategoriaRepository extends JpaRepository<Categoria, Long> {
    @Query("select count(c) from Categoria c "
            + "where lower(replace(trim(c.nombre), ' ', '')) = :nombreNormalizado")
    long contarPorNombreNormalizado(@Param("nombreNormalizado") String nombreNormalizado);

    @Query("select count(c) from Categoria c "
            + "where lower(replace(trim(c.nombre), ' ', '')) = :nombreNormalizado "
            + "and c.id <> :id")
    long contarPorNombreNormalizadoExceptoId(
            @Param("nombreNormalizado") String nombreNormalizado,
            @Param("id") Long id);
}

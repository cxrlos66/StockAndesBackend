package com.example.StockAndesBackend.service.impl;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.example.StockAndesBackend.dto.DespachoRequestDTO;
import com.example.StockAndesBackend.dto.DetalleDespachoRequestDTO;
import com.example.StockAndesBackend.entity.Area;
import com.example.StockAndesBackend.exception.ReglaNegocioException;
import com.example.StockAndesBackend.repository.AreaRepository;
import com.example.StockAndesBackend.repository.DespachoRepository;
import com.example.StockAndesBackend.repository.ProductoRepository;

@ExtendWith(MockitoExtension.class)
class DespachoServiceImplTest {
    @Mock
    private DespachoRepository despachoRepository;
    @Mock
    private AreaRepository areaRepository;
    @Mock
    private ProductoRepository productoRepository;

    private DespachoServiceImpl despachoService;

    @BeforeEach
    void preparar() {
        despachoService = new DespachoServiceImpl(despachoRepository, areaRepository, productoRepository);
    }

    @Test
    void noDebeDespacharAAreaInactiva() {
        Area area = new Area();
        area.setId(4L);
        area.setCodigo("AR04");
        area.setEstado(false);
        area.setPresupuestoMensual(new BigDecimal("1000.00"));
        when(areaRepository.findById(4L)).thenReturn(Optional.of(area));

        DespachoRequestDTO solicitud = new DespachoRequestDTO(
                4L,
                "Prueba",
                List.of(new DetalleDespachoRequestDTO(1L, 1)));

        assertThrows(ReglaNegocioException.class, () -> despachoService.registrar(solicitud));
    }

    @Test
    void noDebeAceptarProductoRepetido() {
        Area area = new Area();
        area.setId(1L);
        area.setCodigo("AR01");
        area.setEstado(true);
        area.setPresupuestoMensual(new BigDecimal("1500.00"));
        when(areaRepository.findById(1L)).thenReturn(Optional.of(area));

        DespachoRequestDTO solicitud = new DespachoRequestDTO(
                1L,
                "Prueba",
                List.of(
                        new DetalleDespachoRequestDTO(1L, 2),
                        new DetalleDespachoRequestDTO(1L, 3)));

        assertThrows(ReglaNegocioException.class, () -> despachoService.registrar(solicitud));
    }
}

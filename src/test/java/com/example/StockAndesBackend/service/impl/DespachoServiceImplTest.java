package com.example.StockAndesBackend.service.impl;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
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
import com.example.StockAndesBackend.entity.Producto;
import com.example.StockAndesBackend.enums.EstadoDespacho;
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

        despachoService = new DespachoServiceImpl(
                despachoRepository,
                areaRepository,
                productoRepository
        );
    }

    // RN-01
    @Test
    void noDebeDespacharAAreaInactiva() {

        Area area = new Area();
        area.setId(4L);
        area.setCodigo("AR04");
        area.setEstado(false);
        area.setPresupuestoMensual(new BigDecimal("1000.00"));

        when(areaRepository.findById(4L))
                .thenReturn(Optional.of(area));

        DespachoRequestDTO solicitud = new DespachoRequestDTO(
                4L,
                "Prueba area inactiva",
                List.of(
                        new DetalleDespachoRequestDTO(1L, 1)
                )
        );

        assertThrows(
                ReglaNegocioException.class,
                () -> despachoService.registrar(solicitud)
        );
    }

    // RN-04
    @Test
    void noDebeAceptarProductoRepetido() {

        Area area = new Area();
        area.setId(1L);
        area.setCodigo("AR01");
        area.setEstado(true);
        area.setPresupuestoMensual(new BigDecimal("1500.00"));

        when(areaRepository.findById(1L))
                .thenReturn(Optional.of(area));

        DespachoRequestDTO solicitud = new DespachoRequestDTO(
                1L,
                "Prueba producto repetido",
                List.of(
                        new DetalleDespachoRequestDTO(1L, 2),
                        new DetalleDespachoRequestDTO(1L, 3)
                )
        );

        assertThrows(
                ReglaNegocioException.class,
                () -> despachoService.registrar(solicitud)
        );
    }

    // RN-02
    @Test
    void noDebeDespacharSiNoHayStockSuficiente() {

        Area area = new Area();
        area.setId(2L);
        area.setCodigo("AR02");
        area.setEstado(true);
        area.setPresupuestoMensual(new BigDecimal("2500.00"));

        when(areaRepository.findById(2L))
                .thenReturn(Optional.of(area));

        Producto producto = new Producto();
        producto.setId(4L);
        producto.setCodigo("OFI-004");
        producto.setNombre("Engrapadora metalica");
        producto.setCostoUnitario(new BigDecimal("22.00"));
        producto.setStock(0);
        producto.setStockMinimo(5);
        producto.setEstado(true);

        when(productoRepository.findById(4L))
                .thenReturn(Optional.of(producto));

        DespachoRequestDTO solicitud = new DespachoRequestDTO(
                2L,
                "Prueba stock insuficiente",
                List.of(
                        new DetalleDespachoRequestDTO(4L, 1)
                )
        );

        assertThrows(
                ReglaNegocioException.class,
                () -> despachoService.registrar(solicitud)
        );
    }

    // RN-03
    @Test
    void noDebeSuperarPresupuestoMensual() {

        Area area = new Area();
        area.setId(3L);
        area.setCodigo("AR03");
        area.setEstado(true);
        area.setPresupuestoMensual(new BigDecimal("800.00"));

        when(areaRepository.findById(3L))
                .thenReturn(Optional.of(area));

        Producto producto = new Producto();
        producto.setId(7L);
        producto.setCodigo("TEC-001");
        producto.setNombre("Toner HP 105A");
        producto.setCostoUnitario(new BigDecimal("180.00"));
        producto.setStock(12);
        producto.setStockMinimo(4);
        producto.setEstado(true);

        when(productoRepository.findById(7L))
                .thenReturn(Optional.of(producto));

        when(despachoRepository.sumarMontoDelMes(
                eq(3L),
                eq(EstadoDespacho.REGISTRADO),
                any(),
                any()
        )).thenReturn(BigDecimal.ZERO);

        DespachoRequestDTO solicitud = new DespachoRequestDTO(
                3L,
                "Prueba presupuesto mensual",
                List.of(
                        new DetalleDespachoRequestDTO(7L, 5)
                )
        );

        assertThrows(
                ReglaNegocioException.class,
                () -> despachoService.registrar(solicitud)
        );
    }
}

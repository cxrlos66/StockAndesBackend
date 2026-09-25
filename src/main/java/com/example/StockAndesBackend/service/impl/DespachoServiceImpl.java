package com.example.StockAndesBackend.service.impl;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.StockAndesBackend.dto.DespachoRequestDTO;
import com.example.StockAndesBackend.dto.DespachoResponseDTO;
import com.example.StockAndesBackend.dto.DetalleDespachoRequestDTO;
import com.example.StockAndesBackend.dto.DetalleDespachoResponseDTO;
import com.example.StockAndesBackend.entity.Area;
import com.example.StockAndesBackend.entity.Despacho;
import com.example.StockAndesBackend.entity.DetalleDespacho;
import com.example.StockAndesBackend.entity.Producto;
import com.example.StockAndesBackend.enums.EstadoDespacho;
import com.example.StockAndesBackend.exception.RecursoNoEncontradoException;
import com.example.StockAndesBackend.exception.ReglaNegocioException;
import com.example.StockAndesBackend.repository.AreaRepository;
import com.example.StockAndesBackend.repository.DespachoRepository;
import com.example.StockAndesBackend.repository.ProductoRepository;
import com.example.StockAndesBackend.service.service.DespachoService;

@Service
public class DespachoServiceImpl implements DespachoService {
    private static final Logger registro = LoggerFactory.getLogger(DespachoServiceImpl.class);

    private final DespachoRepository despachoRepository;
    private final AreaRepository areaRepository;
    private final ProductoRepository productoRepository;

    public DespachoServiceImpl(
            DespachoRepository despachoRepository,
            AreaRepository areaRepository,
            ProductoRepository productoRepository) {
        this.despachoRepository = despachoRepository;
        this.areaRepository = areaRepository;
        this.productoRepository = productoRepository;
    }

    @Override
    @Transactional
    public DespachoResponseDTO registrar(DespachoRequestDTO solicitud) {
        Area area = areaRepository.findById(solicitud.getAreaId())
                .orElseThrow(() -> new RecursoNoEncontradoException(
                        "No existe el area con id " + solicitud.getAreaId()));

        validarAreaActiva(area);
        validarProductosNoRepetidos(solicitud.getDetalles());

        Despacho despacho = new Despacho();
        despacho.setArea(area);
        despacho.setObservacion(limpiarTexto(solicitud.getObservacion()));
        despacho.setEstado(EstadoDespacho.REGISTRADO);

        int totalUnidades = 0;
        BigDecimal montoTotal = BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP);

        for (DetalleDespachoRequestDTO item : solicitud.getDetalles()) {
            Producto producto = productoRepository.findById(item.getProductoId())
                    .orElseThrow(() -> new RecursoNoEncontradoException(
                            "No existe el producto con id " + item.getProductoId()));

            validarProductoActivo(producto);
            validarStock(producto, item.getCantidad());

            BigDecimal costoUnitario = producto.getCostoUnitario().setScale(2, RoundingMode.HALF_UP);
            BigDecimal importe = costoUnitario
                    .multiply(BigDecimal.valueOf(item.getCantidad()))
                    .setScale(2, RoundingMode.HALF_UP);

            DetalleDespacho detalle = new DetalleDespacho();
            detalle.setProducto(producto);
            detalle.setCantidad(item.getCantidad());
            detalle.setCostoUnitario(costoUnitario);
            detalle.setImporte(importe);
            despacho.agregarDetalle(detalle);

            producto.setStock(producto.getStock() - item.getCantidad());

            totalUnidades += item.getCantidad();
            montoTotal = montoTotal.add(importe).setScale(2, RoundingMode.HALF_UP);
        }

        validarPresupuestoMensual(area, montoTotal);

        despacho.setTotalUnidades(totalUnidades);
        despacho.setMontoTotal(montoTotal);

        Despacho guardado = despachoRepository.save(despacho);
        registro.info("Despacho registrado con id {} y monto {}", guardado.getId(), guardado.getMontoTotal());
        return convertirRespuesta(guardado);
    }

    @Override
    @Transactional(readOnly = true)
    public DespachoResponseDTO buscarPorId(Long id) {
        return convertirRespuesta(obtenerEntidad(id));
    }

    @Override
    @Transactional(readOnly = true)
    public List<DespachoResponseDTO> listar() {
        return despachoRepository.findAllByOrderByFechaDesc().stream()
                .map(this::convertirRespuesta)
                .toList();
    }

    @Override
    @Transactional
    public DespachoResponseDTO anular(Long id) {
        Despacho despacho = obtenerEntidad(id);
        if (despacho.getEstado() == EstadoDespacho.ANULADO) {
            registro.warn("Se intento anular nuevamente el despacho {}", id);
            throw new ReglaNegocioException("El despacho ya se encuentra anulado");
        }

        for (DetalleDespacho detalle : despacho.getDetalles()) {
            Producto producto = detalle.getProducto();
            producto.setStock(producto.getStock() + detalle.getCantidad());
        }

        despacho.setEstado(EstadoDespacho.ANULADO);
        Despacho guardado = despachoRepository.save(despacho);
        registro.info("Despacho anulado con id {}. Se devolvio el stock", id);
        return convertirRespuesta(guardado);
    }

    private void validarAreaActiva(Area area) {
        if (!Boolean.TRUE.equals(area.getEstado())) {
            registro.warn("RN-01: el area {} esta inactiva", area.getId());
            throw new ReglaNegocioException("RN-01: Solo se puede despachar a un area activa");
        }
    }

    private void validarProductoActivo(Producto producto) {
        if (!Boolean.TRUE.equals(producto.getEstado())) {
            registro.warn("RN-01: el producto {} esta inactivo", producto.getId());
            throw new ReglaNegocioException("RN-01: Solo se pueden despachar productos activos");
        }
    }

    private void validarStock(Producto producto, Integer cantidad) {
        if (cantidad > producto.getStock()) {
            registro.warn("RN-02: stock insuficiente para el producto {}", producto.getCodigo());
            throw new ReglaNegocioException(
                    "RN-02: Stock insuficiente para el producto " + producto.getCodigo()
                    + ". Disponible: " + producto.getStock());
        }
    }

    private void validarProductosNoRepetidos(List<DetalleDespachoRequestDTO> detalles) {
        Set<Long> productos = new HashSet<>();
        for (DetalleDespachoRequestDTO detalle : detalles) {
            if (!productos.add(detalle.getProductoId())) {
                registro.warn("RN-04: producto repetido {}", detalle.getProductoId());
                throw new ReglaNegocioException("RN-04: Un despacho no puede repetir productos");
            }
        }
    }

    private void validarPresupuestoMensual(Area area, BigDecimal montoNuevo) {
        YearMonth periodoActual = YearMonth.now();
        LocalDate inicio = periodoActual.atDay(1);
        LocalDate fin = periodoActual.plusMonths(1).atDay(1);
        LocalDateTime inicioMes = inicio.atStartOfDay();
        LocalDateTime finMes = fin.atStartOfDay();

        BigDecimal acumulado = despachoRepository.sumarMontoDelMes(
                area.getId(),
                EstadoDespacho.REGISTRADO,
                inicioMes,
                finMes);

        if (acumulado == null) {
            acumulado = BigDecimal.ZERO;
        }

        BigDecimal nuevoAcumulado = acumulado.add(montoNuevo).setScale(2, RoundingMode.HALF_UP);
        if (nuevoAcumulado.compareTo(area.getPresupuestoMensual()) > 0) {
            registro.warn(
                    "RN-03: presupuesto excedido para el area {}. Acumulado nuevo: {}",
                    area.getCodigo(),
                    nuevoAcumulado);
            throw new ReglaNegocioException(
                    "RN-03: El despacho supera el presupuesto mensual del area. "
                    + "Acumulado del mes con el nuevo despacho: S/ " + nuevoAcumulado
                    + ", presupuesto: S/ " + area.getPresupuestoMensual());
        }
    }

    private Despacho obtenerEntidad(Long id) {
        return despachoRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("No existe el despacho con id " + id));
    }

    private String limpiarTexto(String texto) {
        return texto == null ? null : texto.trim();
    }

    private DespachoResponseDTO convertirRespuesta(Despacho despacho) {
        List<DetalleDespachoResponseDTO> detalles = despacho.getDetalles().stream()
                .map(detalle -> new DetalleDespachoResponseDTO(
                        detalle.getProducto().getId(),
                        detalle.getProducto().getCodigo(),
                        detalle.getProducto().getNombre(),
                        detalle.getCantidad(),
                        detalle.getCostoUnitario(),
                        detalle.getImporte()))
                .toList();

        return new DespachoResponseDTO(
                despacho.getId(),
                despacho.getFecha(),
                despacho.getArea().getId(),
                despacho.getArea().getCodigo(),
                despacho.getArea().getNombre(),
                despacho.getObservacion(),
                despacho.getEstado(),
                despacho.getTotalUnidades(),
                despacho.getMontoTotal(),
                detalles);
    }
}

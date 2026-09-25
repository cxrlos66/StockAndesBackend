package com.example.StockAndesBackend.service.impl;

import java.math.RoundingMode;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.StockAndesBackend.dto.AreaRequestDTO;
import com.example.StockAndesBackend.dto.AreaResponseDTO;
import com.example.StockAndesBackend.entity.Area;
import com.example.StockAndesBackend.exception.RecursoNoEncontradoException;
import com.example.StockAndesBackend.exception.ReglaNegocioException;
import com.example.StockAndesBackend.repository.AreaRepository;
import com.example.StockAndesBackend.repository.DespachoRepository;
import com.example.StockAndesBackend.service.service.AreaService;

@Service
public class AreaServiceImpl implements AreaService {
    private static final Logger registro = LoggerFactory.getLogger(AreaServiceImpl.class);

    private final AreaRepository areaRepository;
    private final DespachoRepository despachoRepository;

    public AreaServiceImpl(AreaRepository areaRepository, DespachoRepository despachoRepository) {
        this.areaRepository = areaRepository;
        this.despachoRepository = despachoRepository;
    }

    @Override
    @Transactional
    public AreaResponseDTO crear(AreaRequestDTO solicitud) {
        validarUnicidad(solicitud, null);
        Area area = new Area();
        asignarDatos(area, solicitud);
        Area guardada = areaRepository.save(area);
        registro.info("Area creada con id {}", guardada.getId());
        return convertirRespuesta(guardada);
    }

    @Override
    @Transactional
    public AreaResponseDTO actualizar(Long id, AreaRequestDTO solicitud) {
        Area area = obtenerEntidad(id);
        validarUnicidad(solicitud, id);
        asignarDatos(area, solicitud);
        Area guardada = areaRepository.save(area);
        registro.info("Area actualizada con id {}", id);
        return convertirRespuesta(guardada);
    }

    @Override
    @Transactional(readOnly = true)
    public AreaResponseDTO buscarPorId(Long id) {
        return convertirRespuesta(obtenerEntidad(id));
    }

    @Override
    @Transactional
    public void eliminar(Long id) {
        Area area = obtenerEntidad(id);
        if (despachoRepository.existsByAreaId(id)) {
            registro.warn("No se puede eliminar el area {} porque tiene despachos", id);
            throw new ReglaNegocioException("No se puede eliminar el area porque tiene despachos asociados");
        }
        areaRepository.delete(area);
        registro.info("Area eliminada con id {}", id);
    }

    @Override
    @Transactional(readOnly = true)
    public Iterable<AreaResponseDTO> listar() {
        return areaRepository.findAll(Sort.by("codigo").ascending()).stream()
                .map(this::convertirRespuesta)
                .toList();
    }

    private void validarUnicidad(AreaRequestDTO solicitud, Long idActual) {
        boolean codigoExiste = idActual == null
                ? areaRepository.existsByCodigoIgnoreCase(solicitud.getCodigo().trim())
                : areaRepository.existsByCodigoIgnoreCaseAndIdNot(solicitud.getCodigo().trim(), idActual);
        if (codigoExiste) {
            registro.warn("Codigo de area duplicado: {}", solicitud.getCodigo());
            throw new ReglaNegocioException("Ya existe un area con ese codigo");
        }

        boolean nombreExiste = idActual == null
                ? areaRepository.existsByNombreIgnoreCase(solicitud.getNombre().trim())
                : areaRepository.existsByNombreIgnoreCaseAndIdNot(solicitud.getNombre().trim(), idActual);
        if (nombreExiste) {
            registro.warn("Nombre de area duplicado: {}", solicitud.getNombre());
            throw new ReglaNegocioException("Ya existe un area con ese nombre");
        }
    }

    private void asignarDatos(Area area, AreaRequestDTO solicitud) {
        area.setCodigo(solicitud.getCodigo().trim().toUpperCase());
        area.setNombre(solicitud.getNombre().trim());
        area.setResponsable(solicitud.getResponsable().trim());
        area.setEmail(solicitud.getEmail().trim().toLowerCase());
        area.setPresupuestoMensual(solicitud.getPresupuestoMensual().setScale(2, RoundingMode.HALF_UP));
        area.setEstado(solicitud.getEstado());
    }

    private Area obtenerEntidad(Long id) {
        return areaRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("No existe el area con id " + id));
    }

    private AreaResponseDTO convertirRespuesta(Area area) {
        return new AreaResponseDTO(
                area.getId(),
                area.getCodigo(),
                area.getNombre(),
                area.getResponsable(),
                area.getEmail(),
                area.getPresupuestoMensual(),
                area.getEstado());
    }
}

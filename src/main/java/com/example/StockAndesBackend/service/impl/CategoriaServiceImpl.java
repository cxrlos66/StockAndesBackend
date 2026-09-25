package com.example.StockAndesBackend.service.impl;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.StockAndesBackend.dto.CategoriaRequestDTO;
import com.example.StockAndesBackend.dto.CategoriaResponseDTO;
import com.example.StockAndesBackend.entity.Categoria;
import com.example.StockAndesBackend.exception.RecursoNoEncontradoException;
import com.example.StockAndesBackend.exception.ReglaNegocioException;
import com.example.StockAndesBackend.repository.CategoriaRepository;
import com.example.StockAndesBackend.repository.ProductoRepository;
import com.example.StockAndesBackend.service.service.CategoriaService;

@Service
public class CategoriaServiceImpl implements CategoriaService {
    private static final Logger registro = LoggerFactory.getLogger(CategoriaServiceImpl.class);

    private final CategoriaRepository categoriaRepository;
    private final ProductoRepository productoRepository;

    public CategoriaServiceImpl(CategoriaRepository categoriaRepository, ProductoRepository productoRepository) {
        this.categoriaRepository = categoriaRepository;
        this.productoRepository = productoRepository;
    }

    @Override
    @Transactional
    public CategoriaResponseDTO crear(CategoriaRequestDTO solicitud) {
        validarNombreUnico(solicitud.getNombre(), null);
        Categoria categoria = new Categoria();
        categoria.setNombre(solicitud.getNombre().trim());
        categoria.setDescripcion(limpiarTexto(solicitud.getDescripcion()));
        categoria.setEstado(solicitud.getEstado());
        Categoria guardada = categoriaRepository.save(categoria);
        registro.info("Categoria creada con id {}", guardada.getId());
        return convertirRespuesta(guardada);
    }

    @Override
    @Transactional
    public CategoriaResponseDTO actualizar(Long id, CategoriaRequestDTO solicitud) {
        Categoria categoria = obtenerEntidad(id);
        validarNombreUnico(solicitud.getNombre(), id);
        categoria.setNombre(solicitud.getNombre().trim());
        categoria.setDescripcion(limpiarTexto(solicitud.getDescripcion()));
        categoria.setEstado(solicitud.getEstado());
        Categoria guardada = categoriaRepository.save(categoria);
        registro.info("Categoria actualizada con id {}", id);
        return convertirRespuesta(guardada);
    }

    @Override
    @Transactional(readOnly = true)
    public CategoriaResponseDTO buscarPorId(Long id) {
        return convertirRespuesta(obtenerEntidad(id));
    }

    @Override
    @Transactional
    public void eliminar(Long id) {
        Categoria categoria = obtenerEntidad(id);
        if (productoRepository.existsByCategoriaId(id)) {
            registro.warn("No se puede eliminar la categoria {} porque tiene productos", id);
            throw new ReglaNegocioException("No se puede eliminar la categoria porque tiene productos asociados");
        }
        categoriaRepository.delete(categoria);
        registro.info("Categoria eliminada con id {}", id);
    }

    @Override
    @Transactional(readOnly = true)
    public Iterable<CategoriaResponseDTO> listar() {
        List<CategoriaResponseDTO> categorias = categoriaRepository.findAll().stream()
                .map(this::convertirRespuesta)
                .toList();
        return categorias;
    }

    private Categoria obtenerEntidad(Long id) {
        return categoriaRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("No existe la categoria con id " + id));
    }

    private void validarNombreUnico(String nombre, Long idActual) {
        String nombreNormalizado = normalizarNombre(nombre);
        long cantidad = idActual == null
                ? categoriaRepository.contarPorNombreNormalizado(nombreNormalizado)
                : categoriaRepository.contarPorNombreNormalizadoExceptoId(nombreNormalizado, idActual);
        if (cantidad > 0) {
            registro.warn("Nombre de categoria duplicado: {}", nombre);
            throw new ReglaNegocioException("Ya existe una categoria con ese nombre");
        }
    }

    private String normalizarNombre(String nombre) {
        return nombre.trim().toLowerCase().replace(" ", "");
    }

    private String limpiarTexto(String texto) {
        return texto == null ? null : texto.trim();
    }

    private CategoriaResponseDTO convertirRespuesta(Categoria categoria) {
        return new CategoriaResponseDTO(
                categoria.getId(),
                categoria.getNombre(),
                categoria.getDescripcion(),
                categoria.getEstado());
    }
}

package com.example.StockAndesBackend.service.impl;

import java.math.RoundingMode;
import java.util.List;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.StockAndesBackend.dto.ProductoRequestDTO;
import com.example.StockAndesBackend.dto.ProductoResponseDTO;
import com.example.StockAndesBackend.entity.Categoria;
import com.example.StockAndesBackend.entity.Producto;
import com.example.StockAndesBackend.exception.RecursoNoEncontradoException;
import com.example.StockAndesBackend.exception.ReglaNegocioException;
import com.example.StockAndesBackend.repository.CategoriaRepository;
import com.example.StockAndesBackend.repository.DetalleDespachoRepository;
import com.example.StockAndesBackend.repository.ProductoRepository;
import com.example.StockAndesBackend.service.service.ProductoService;

import jakarta.persistence.criteria.Predicate;

@Service
public class ProductoServiceImpl implements ProductoService {
    private static final Logger registro = LoggerFactory.getLogger(ProductoServiceImpl.class);
    private static final Set<String> CAMPOS_ORDEN_VALIDOS = Set.of("nombre", "costo", "stock");

    private final ProductoRepository productoRepository;
    private final CategoriaRepository categoriaRepository;
    private final DetalleDespachoRepository detalleDespachoRepository;

    public ProductoServiceImpl(
            ProductoRepository productoRepository,
            CategoriaRepository categoriaRepository,
            DetalleDespachoRepository detalleDespachoRepository) {
        this.productoRepository = productoRepository;
        this.categoriaRepository = categoriaRepository;
        this.detalleDespachoRepository = detalleDespachoRepository;
    }

    @Override
    @Transactional
    public ProductoResponseDTO crear(ProductoRequestDTO solicitud) {
        validarCodigoUnico(solicitud.getCodigo(), null);
        Categoria categoria = obtenerCategoria(solicitud.getCategoriaId());

        Producto producto = new Producto();
        asignarDatos(producto, solicitud, categoria);
        Producto guardado = productoRepository.save(producto);
        registro.info("Producto creado con id {}", guardado.getId());
        return convertirRespuesta(guardado);
    }

    @Override
    @Transactional
    public ProductoResponseDTO actualizar(Long id, ProductoRequestDTO solicitud) {
        Producto producto = obtenerEntidad(id);
        validarCodigoUnico(solicitud.getCodigo(), id);
        Categoria categoria = obtenerCategoria(solicitud.getCategoriaId());
        asignarDatos(producto, solicitud, categoria);
        Producto guardado = productoRepository.save(producto);
        registro.info("Producto actualizado con id {}", id);
        return convertirRespuesta(guardado);
    }

    @Override
    @Transactional(readOnly = true)
    public ProductoResponseDTO buscarPorId(Long id) {
        return convertirRespuesta(obtenerEntidad(id));
    }

    @Override
    @Transactional
    public void eliminar(Long id) {
        Producto producto = obtenerEntidad(id);
        if (detalleDespachoRepository.existsByProductoId(id)) {
            registro.warn("No se puede eliminar el producto {} porque aparece en despachos", id);
            throw new ReglaNegocioException("No se puede eliminar el producto porque tiene despachos asociados");
        }
        productoRepository.delete(producto);
        registro.info("Producto eliminado con id {}", id);
    }

    @Override
    @Transactional(readOnly = true)
    public Iterable<ProductoResponseDTO> listar() {
        return productoRepository.findAll(Sort.by("nombre").ascending()).stream()
                .map(this::convertirRespuesta)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProductoResponseDTO> listarPorCategoria(Long categoriaId) {
        obtenerCategoria(categoriaId);
        return productoRepository.findByCategoriaIdOrderByNombreAsc(categoriaId).stream()
                .map(this::convertirRespuesta)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProductoResponseDTO> buscar(
            String nombre,
            Long categoriaId,
            Boolean stockBajo,
            String orden,
            String direccion) {

        String campoOrden = orden == null || orden.isBlank() ? "nombre" : orden.toLowerCase();
        String sentido = direccion == null || direccion.isBlank() ? "asc" : direccion.toLowerCase();

        if (!CAMPOS_ORDEN_VALIDOS.contains(campoOrden)) {
            throw new IllegalArgumentException("El orden debe ser nombre, costo o stock");
        }
        if (!sentido.equals("asc") && !sentido.equals("desc")) {
            throw new IllegalArgumentException("La direccion debe ser asc o desc");
        }

        String propiedadOrden = campoOrden.equals("costo") ? "costoUnitario" : campoOrden;
        Sort ordenamiento = sentido.equals("desc")
                ? Sort.by(propiedadOrden).descending()
                : Sort.by(propiedadOrden).ascending();

        Specification<Producto> especificacion = (raiz, consulta, constructor) -> {
            Predicate condicion = constructor.conjunction();

            if (nombre != null && !nombre.isBlank()) {
                condicion = constructor.and(
                        condicion,
                        constructor.like(
                                constructor.lower(raiz.get("nombre")),
                                "%" + nombre.trim().toLowerCase() + "%"));
            }

            if (categoriaId != null) {
                condicion = constructor.and(
                        condicion,
                        constructor.equal(raiz.get("categoria").get("id"), categoriaId));
            }

            if (Boolean.TRUE.equals(stockBajo)) {
                condicion = constructor.and(
                        condicion,
                        constructor.lessThanOrEqualTo(raiz.<Integer>get("stock"), raiz.<Integer>get("stockMinimo")));
            }

            return condicion;
        };

        return productoRepository.findAll(especificacion, ordenamiento).stream()
                .map(this::convertirRespuesta)
                .toList();
    }

    private void asignarDatos(Producto producto, ProductoRequestDTO solicitud, Categoria categoria) {
        producto.setCodigo(solicitud.getCodigo().trim().toUpperCase());
        producto.setNombre(solicitud.getNombre().trim());
        producto.setCostoUnitario(solicitud.getCostoUnitario().setScale(2, RoundingMode.HALF_UP));
        producto.setStock(solicitud.getStock());
        producto.setStockMinimo(solicitud.getStockMinimo());
        producto.setEstado(solicitud.getEstado());
        producto.setCategoria(categoria);
    }

    private Producto obtenerEntidad(Long id) {
        return productoRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("No existe el producto con id " + id));
    }

    private Categoria obtenerCategoria(Long id) {
        return categoriaRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("No existe la categoria con id " + id));
    }

    private void validarCodigoUnico(String codigo, Long idActual) {
        boolean existe = idActual == null
                ? productoRepository.existsByCodigoIgnoreCase(codigo.trim())
                : productoRepository.existsByCodigoIgnoreCaseAndIdNot(codigo.trim(), idActual);
        if (existe) {
            registro.warn("Codigo de producto duplicado: {}", codigo);
            throw new ReglaNegocioException("Ya existe un producto con ese codigo");
        }
    }

    private ProductoResponseDTO convertirRespuesta(Producto producto) {
        return new ProductoResponseDTO(
                producto.getId(),
                producto.getCodigo(),
                producto.getNombre(),
                producto.getCostoUnitario(),
                producto.getStock(),
                producto.getStockMinimo(),
                producto.getEstado(),
                producto.getCategoria().getId(),
                producto.getCategoria().getNombre());
    }
}

package com.example.StockAndesBackend.service.generic;

public interface CrudService<SOLICITUD, RESPUESTA, IDENTIFICADOR> {
    RESPUESTA crear(SOLICITUD solicitud);
    RESPUESTA actualizar(IDENTIFICADOR id, SOLICITUD solicitud);
    RESPUESTA buscarPorId(IDENTIFICADOR id);
    void eliminar(IDENTIFICADOR id);
    Iterable<RESPUESTA> listar();
}

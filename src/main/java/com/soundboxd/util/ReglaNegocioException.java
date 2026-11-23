package com.soundboxd.util;

/**
 * Error de una regla de negocio o de acceso a datos.
 * El mensaje está en español y se puede mostrar tal cual en pantalla.
 * Es no verificada: los servicios la lanzan y la capa de vista la captura.
 */
public class ReglaNegocioException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public ReglaNegocioException(String mensaje) {
        super(mensaje);
    }

    /** Conserva la causa técnica (por ejemplo una SQLException) para el registro interno. */
    public ReglaNegocioException(String mensaje, Throwable causa) {
        super(mensaje, causa);
    }
}

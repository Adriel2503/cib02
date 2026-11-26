package com.soundboxd.util;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.function.Supplier;

/**
 * Ejecuta varias operaciones como una sola transacción: commit si todo sale bien,
 * rollback ante cualquier fallo (SQLException, RuntimeException o Error).
 */
public class Transaccion {

    /** Trabajo que usa la conexión de la transacción. No debe cerrarla ni hacer commit. */
    @FunctionalInterface
    public interface Operacion<T> {
        T ejecutar(Connection conexion) throws SQLException;
    }

    private final Supplier<Connection> conexiones;

    public Transaccion() {
        this(ConexionBD::obtener);
    }

    public Transaccion(Supplier<Connection> conexiones) {
        this.conexiones = conexiones;
    }

    /** mensajeError se usa solo si el fallo es una SQLException; las demás excepciones pasan tal cual. */
    public <T> T ejecutar(Operacion<T> operacion, String mensajeError) {
        try (Connection c = conexiones.get()) {
            c.setAutoCommit(false);
            boolean confirmada = false;
            try {
                T resultado = operacion.ejecutar(c);
                c.commit();
                confirmada = true;
                return resultado;
            } finally {
                if (!confirmada) {
                    c.rollback();
                }
            }
        } catch (SQLException e) {
            throw new ReglaNegocioException(mensajeError, e);
        }
    }
}

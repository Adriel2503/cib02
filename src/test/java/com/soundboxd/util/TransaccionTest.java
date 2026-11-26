package com.soundboxd.util;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

import java.lang.reflect.Proxy;
import java.sql.Connection;
import java.sql.SQLException;

/** No necesita base de datos: usa una conexión simulada cuyo rollback falla. */
class TransaccionTest {

    @Test
    void siElRollbackFallaSeConservaElErrorOriginal() {
        Connection conexion = (Connection) Proxy.newProxyInstance(
                getClass().getClassLoader(), new Class<?>[] {Connection.class},
                (proxy, metodo, argumentos) -> {
                    if (metodo.getName().equals("rollback")) {
                        throw new SQLException("conexión perdida");
                    }
                    return null;
                });
        ReglaNegocioException original = new ReglaNegocioException("El álbum no existe.");

        ReglaNegocioException recibido = assertThrows(ReglaNegocioException.class,
                () -> new Transaccion(() -> conexion).ejecutar(c -> {
                    throw original;
                }, "No se pudo crear la lista."));

        assertSame(original, recibido);
        assertEquals("conexión perdida", recibido.getSuppressed()[0].getMessage());
    }
}

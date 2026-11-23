package com.soundboxd.util;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

/**
 * Entrega conexiones a SQL Server leyendo db.properties.
 *
 * db.properties es local de cada integrante y NO se sube al repositorio.
 * Se crea copiando src/main/resources/db.properties.example.
 * Claves: db.url, db.user y db.password.
 */
public final class ConexionBD {

    private static final String ARCHIVO = "/db.properties";
    private static final String MARCADOR = "CAMBIAR_CLAVE";
    private static Properties propiedades;

    private ConexionBD() { }

    /** Abre una conexión nueva. Quien la pide es responsable de cerrarla (try-with-resources). */
    public static Connection obtener() {
        Properties p = cargar();
        try {
            return DriverManager.getConnection(
                    p.getProperty("db.url"), p.getProperty("db.user"), p.getProperty("db.password"));
        } catch (SQLException e) {
            throw new ReglaNegocioException(
                    "No se pudo conectar con la base de datos. Verifica db.properties y que SQL Server esté activo.", e);
        }
    }

    private static synchronized Properties cargar() {
        if (propiedades != null) {
            return propiedades;
        }
        try (InputStream in = ConexionBD.class.getResourceAsStream(ARCHIVO)) {
            if (in == null) {
                throw new ReglaNegocioException(
                        "Falta el archivo db.properties. Copia db.properties.example como db.properties en src/main/resources y completa tus datos.");
            }
            Properties p = new Properties();
            p.load(in);
            for (String clave : new String[] {"db.url", "db.user", "db.password"}) {
                if (p.getProperty(clave) == null || p.getProperty(clave).isBlank()) {
                    throw new ReglaNegocioException("En db.properties falta la clave " + clave + ".");
                }
            }
            if (MARCADOR.equals(p.getProperty("db.password"))) {
                throw new ReglaNegocioException(
                        "En db.properties reemplaza CAMBIAR_CLAVE por la clave real de soundboxd_app.");
            }
            propiedades = p;
            return propiedades;
        } catch (IOException e) {
            throw new ReglaNegocioException("No se pudo leer db.properties.", e);
        }
    }
}

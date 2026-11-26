package com.soundboxd.dao;

import java.sql.SQLException;

/** Reconoce los errores de SQL Server que los DAO traducen a mensajes claros. */
final class ErroresSql {

    private ErroresSql() { }

    /** Violación de PRIMARY KEY o UNIQUE. */
    static boolean esDuplicado(SQLException e) {
        return e.getErrorCode() == 2627 || e.getErrorCode() == 2601;
    }

    /** Violación de clave foránea o de CHECK. */
    static boolean esRestriccion(SQLException e) {
        return e.getErrorCode() == 547;
    }
}

package com.soundboxd.dao;

/** Arma el parámetro de un LIKE tratando como texto los comodines escritos por el usuario. */
final class PatronLike {

    private PatronLike() { }

    /** El resultado se envía como parámetro; nunca se une al SQL. */
    static String contiene(String texto) {
        String escapado = texto.replace("[", "[[]").replace("%", "[%]").replace("_", "[_]");
        return "%" + escapado + "%";
    }
}

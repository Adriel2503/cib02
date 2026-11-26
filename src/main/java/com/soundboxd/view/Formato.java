package com.soundboxd.view;

import com.soundboxd.model.Cancion;
import com.soundboxd.model.Resena;

/** Textos para mostrar en listas. Solo formatea; no consulta nada. */
public final class Formato {

    private Formato() { }

    /** "1. Come Together (4:19)" */
    public static String cancion(Cancion c) {
        return c.getNumeroPista() + ". " + c.getTitulo()
                + " (" + c.getDuracionSeg() / 60 + ":" + String.format("%02d", c.getDuracionSeg() % 60) + ")";
    }

    /** "ana · 4/5 · Texto de la reseña" */
    public static String resena(Resena r) {
        String texto = r.getTexto() == null ? "" : r.getTexto();
        return r.getNombreUsuario() + " · " + r.getCalificacion() + "/5 · " + texto;
    }
}

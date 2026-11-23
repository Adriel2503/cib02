package com.soundboxd.model;

import java.time.LocalDateTime;

/** Relación seguidor-seguido entre usuarios (tabla SEGUIMIENTO). */
public class Seguimiento {

    private int idSeguidor;
    private int idSeguido;
    private LocalDateTime fecha;

    public Seguimiento() { }

    public int getIdSeguidor() { return idSeguidor; }
    public void setIdSeguidor(int idSeguidor) { this.idSeguidor = idSeguidor; }

    public int getIdSeguido() { return idSeguido; }
    public void setIdSeguido(int idSeguido) { this.idSeguido = idSeguido; }

    public LocalDateTime getFecha() { return fecha; }
    public void setFecha(LocalDateTime fecha) { this.fecha = fecha; }
}

package com.soundboxd.model;

import java.time.LocalDateTime;

/** Reseña de un usuario sobre un álbum (tabla RESENA). nombreUsuario se llena en las consultas que unen con USUARIO. */
public class Resena {

    private int idResena;
    private int idUsuario;
    private String nombreUsuario;
    private int idAlbum;
    private int calificacion;
    private String texto;
    private EstadoResena estado;
    private LocalDateTime fecha;

    public Resena() { }

    public int getIdResena() { return idResena; }
    public void setIdResena(int idResena) { this.idResena = idResena; }

    public int getIdUsuario() { return idUsuario; }
    public void setIdUsuario(int idUsuario) { this.idUsuario = idUsuario; }

    public String getNombreUsuario() { return nombreUsuario; }
    public void setNombreUsuario(String nombreUsuario) { this.nombreUsuario = nombreUsuario; }

    public int getIdAlbum() { return idAlbum; }
    public void setIdAlbum(int idAlbum) { this.idAlbum = idAlbum; }

    public int getCalificacion() { return calificacion; }
    public void setCalificacion(int calificacion) { this.calificacion = calificacion; }

    public String getTexto() { return texto; }
    public void setTexto(String texto) { this.texto = texto; }

    public EstadoResena getEstado() { return estado; }
    public void setEstado(EstadoResena estado) { this.estado = estado; }

    public LocalDateTime getFecha() { return fecha; }
    public void setFecha(LocalDateTime fecha) { this.fecha = fecha; }
}

package com.soundboxd.model;

import java.time.LocalDateTime;

/** Lista de álbumes de un usuario (tabla LISTA). */
public class Lista {

    private int idLista;
    private int idUsuario;
    private String nombre;
    private String descripcion;
    private boolean esPublica;
    private LocalDateTime fechaCreacion;

    public Lista() { }

    public int getIdLista() { return idLista; }
    public void setIdLista(int idLista) { this.idLista = idLista; }

    public int getIdUsuario() { return idUsuario; }
    public void setIdUsuario(int idUsuario) { this.idUsuario = idUsuario; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }

    public boolean isEsPublica() { return esPublica; }
    public void setEsPublica(boolean esPublica) { this.esPublica = esPublica; }

    public LocalDateTime getFechaCreacion() { return fechaCreacion; }
    public void setFechaCreacion(LocalDateTime fechaCreacion) { this.fechaCreacion = fechaCreacion; }
}

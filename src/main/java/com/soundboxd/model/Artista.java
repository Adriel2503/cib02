package com.soundboxd.model;

/** Artista o banda (tabla ARTISTA). */
public class Artista {

    private int idArtista;
    private String nombre;
    private String pais;

    public Artista() { }

    public int getIdArtista() { return idArtista; }
    public void setIdArtista(int idArtista) { this.idArtista = idArtista; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getPais() { return pais; }
    public void setPais(String pais) { this.pais = pais; }
}

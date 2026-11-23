package com.soundboxd.model;

/** Canción de un álbum (tabla CANCION). */
public class Cancion {

    private int idCancion;
    private int idAlbum;
    private String titulo;
    private int numeroPista;
    private int duracionSeg;

    public Cancion() { }

    public int getIdCancion() { return idCancion; }
    public void setIdCancion(int idCancion) { this.idCancion = idCancion; }

    public int getIdAlbum() { return idAlbum; }
    public void setIdAlbum(int idAlbum) { this.idAlbum = idAlbum; }

    public String getTitulo() { return titulo; }
    public void setTitulo(String titulo) { this.titulo = titulo; }

    public int getNumeroPista() { return numeroPista; }
    public void setNumeroPista(int numeroPista) { this.numeroPista = numeroPista; }

    public int getDuracionSeg() { return duracionSeg; }
    public void setDuracionSeg(int duracionSeg) { this.duracionSeg = duracionSeg; }
}

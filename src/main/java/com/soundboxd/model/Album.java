package com.soundboxd.model;

/** Álbum del catálogo (tabla ALBUM). nombreArtista se llena en las consultas que unen con ARTISTA. */
public class Album {

    private int idAlbum;
    private String titulo;
    private int idArtista;
    private String nombreArtista;
    private int anio;
    private String genero;
    private String portada;

    public Album() { }

    public int getIdAlbum() { return idAlbum; }
    public void setIdAlbum(int idAlbum) { this.idAlbum = idAlbum; }

    public String getTitulo() { return titulo; }
    public void setTitulo(String titulo) { this.titulo = titulo; }

    public int getIdArtista() { return idArtista; }
    public void setIdArtista(int idArtista) { this.idArtista = idArtista; }

    public String getNombreArtista() { return nombreArtista; }
    public void setNombreArtista(String nombreArtista) { this.nombreArtista = nombreArtista; }

    public int getAnio() { return anio; }
    public void setAnio(int anio) { this.anio = anio; }

    public String getGenero() { return genero; }
    public void setGenero(String genero) { this.genero = genero; }

    public String getPortada() { return portada; }
    public void setPortada(String portada) { this.portada = portada; }
}

package com.soundboxd.model;

/** Álbum dentro de una lista, con su posición (tabla LISTA_ALBUM). */
public class ListaAlbum {

    private int idLista;
    private int idAlbum;
    private int posicion;

    public ListaAlbum() { }

    public int getIdLista() { return idLista; }
    public void setIdLista(int idLista) { this.idLista = idLista; }

    public int getIdAlbum() { return idAlbum; }
    public void setIdAlbum(int idAlbum) { this.idAlbum = idAlbum; }

    public int getPosicion() { return posicion; }
    public void setPosicion(int posicion) { this.posicion = posicion; }
}

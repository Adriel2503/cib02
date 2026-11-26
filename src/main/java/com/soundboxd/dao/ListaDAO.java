package com.soundboxd.dao;

import com.soundboxd.model.Lista;
import com.soundboxd.model.ListaAlbum;

import java.sql.Connection;
import java.util.List;
import java.util.Optional;

/** Acceso a datos de LISTA y LISTA_ALBUM. Los métodos con Connection participan en una transacción del servicio. */
public interface ListaDAO {

    /** Usa la conexión recibida (transacción abierta por el servicio). Devuelve el id generado. */
    int insertar(Connection conexion, Lista lista);

    /** Usa la conexión recibida (transacción abierta por el servicio). */
    void agregarAlbum(Connection conexion, int idLista, int idAlbum, int posicion);

    Optional<Lista> buscarPorId(int idLista);

    List<Lista> listarPorUsuario(int idUsuario);

    List<ListaAlbum> listarAlbumes(int idLista);

    void quitarAlbum(int idLista, int idAlbum);

    void actualizarPosicion(int idLista, int idAlbum, int posicion);

    /** Usa la conexión recibida, para reordenar toda la lista en una sola transacción. */
    void actualizarPosicion(Connection conexion, int idLista, int idAlbum, int posicion);

    void eliminar(int idLista);
}

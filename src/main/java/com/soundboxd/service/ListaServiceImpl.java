package com.soundboxd.service;

import com.soundboxd.dao.ListaDAO;
import com.soundboxd.dao.ListaDAOImpl;
import com.soundboxd.model.Lista;
import com.soundboxd.model.ListaAlbum;
import com.soundboxd.model.Usuario;
import com.soundboxd.security.ControlAcceso;
import com.soundboxd.util.ReglaNegocioException;
import com.soundboxd.util.Transaccion;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/** Reglas del módulo M4: listas. */
public class ListaServiceImpl implements ListaService {

    private final ListaDAO listas;
    private final Transaccion transaccion;

    public ListaServiceImpl() {
        this(new ListaDAOImpl(), new Transaccion());
    }

    public ListaServiceImpl(ListaDAO listas, Transaccion transaccion) {
        this.listas = listas;
        this.transaccion = transaccion;
    }

    @Override
    public Lista crearListaConAlbumes(Usuario actor, String nombre, String descripcion,
                                      boolean esPublica, List<Integer> idsAlbum) {
        ControlAcceso.exigirSesion(actor);
        validar(nombre, descripcion);
        List<Integer> ids = idsAlbum == null ? List.of() : idsAlbum;
        if (new HashSet<>(ids).size() != ids.size()) {
            throw new ReglaNegocioException("La lista no puede repetir álbumes.");
        }

        Lista lista = new Lista();
        lista.setIdUsuario(actor.getIdUsuario());
        lista.setNombre(nombre);
        lista.setDescripcion(descripcion);
        lista.setEsPublica(esPublica);
        lista.setFechaCreacion(LocalDateTime.now());

        // Atómico: si falla un álbum no queda ni la lista ni ningún álbum.
        int idLista = transaccion.ejecutar(conexion -> {
            int id = listas.insertar(conexion, lista);
            for (int i = 0; i < ids.size(); i++) {
                listas.agregarAlbum(conexion, id, ids.get(i), i + 1);
            }
            return id;
        }, "No se pudo crear la lista.");

        lista.setIdLista(idLista);
        return lista;
    }

    @Override
    public void agregarAlbum(Usuario actor, int idLista, int idAlbum) {
        exigirDueno(actor, idLista);
        int posicion = listas.listarAlbumes(idLista).stream()
                .mapToInt(ListaAlbum::getPosicion)
                .max()
                .orElse(0) + 1;
        transaccion.ejecutar(conexion -> {
            listas.agregarAlbum(conexion, idLista, idAlbum, posicion);
            return null;
        }, "No se pudo agregar el álbum a la lista.");
    }

    @Override
    public void quitarAlbum(Usuario actor, int idLista, int idAlbum) {
        exigirDueno(actor, idLista);
        listas.quitarAlbum(idLista, idAlbum);
    }

    @Override
    public void reordenar(Usuario actor, int idLista, List<Integer> idsAlbumEnOrden) {
        exigirDueno(actor, idLista);
        Set<Integer> actuales = listas.listarAlbumes(idLista).stream()
                .map(ListaAlbum::getIdAlbum)
                .collect(Collectors.toSet());
        if (idsAlbumEnOrden == null
                || idsAlbumEnOrden.size() != actuales.size()
                || !new HashSet<>(idsAlbumEnOrden).equals(actuales)) {
            throw new ReglaNegocioException("El orden debe incluir exactamente los álbumes de la lista.");
        }
        transaccion.ejecutar(conexion -> {
            for (int i = 0; i < idsAlbumEnOrden.size(); i++) {
                listas.actualizarPosicion(conexion, idLista, idsAlbumEnOrden.get(i), i + 1);
            }
            return null;
        }, "No se pudo reordenar la lista.");
    }

    @Override
    public List<Lista> listarMisListas(Usuario actor) {
        ControlAcceso.exigirSesion(actor);
        return listas.listarPorUsuario(actor.getIdUsuario());
    }

    @Override
    public Lista obtenerLista(int idLista) {
        return listas.buscarPorId(idLista)
                .orElseThrow(() -> new ReglaNegocioException("La lista no existe."));
    }

    @Override
    public void eliminarLista(Usuario actor, int idLista) {
        exigirDueno(actor, idLista);
        listas.eliminar(idLista);
    }

    private void exigirDueno(Usuario actor, int idLista) {
        ControlAcceso.exigirPropietario(actor, obtenerLista(idLista).getIdUsuario());
    }

    private static void validar(String nombre, String descripcion) {
        if (nombre == null || nombre.isBlank()) {
            throw new ReglaNegocioException("El nombre de la lista es obligatorio.");
        }
        if (nombre.length() > 100) {
            throw new ReglaNegocioException("El nombre no puede superar los 100 caracteres.");
        }
        if (descripcion != null && descripcion.length() > 500) {
            throw new ReglaNegocioException("La descripción no puede superar los 500 caracteres.");
        }
    }
}

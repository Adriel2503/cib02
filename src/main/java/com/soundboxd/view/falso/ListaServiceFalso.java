package com.soundboxd.view.falso;

import com.soundboxd.model.Lista;
import com.soundboxd.model.Usuario;
import com.soundboxd.security.ControlAcceso;
import com.soundboxd.service.ListaService;
import com.soundboxd.util.ReglaNegocioException;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

/** Reemplaza temporalmente al servicio de listas (M4) de Amado. */
public class ListaServiceFalso implements ListaService {

    @Override
    public Lista crearListaConAlbumes(Usuario actor, String nombre, String descripcion,
                                      boolean esPublica, List<Integer> idsAlbum) {
        ControlAcceso.exigirSesion(actor);
        if (nombre == null || nombre.isBlank()) {
            throw new ReglaNegocioException("El nombre de la lista es obligatorio.");
        }
        List<Integer> ids = idsAlbum == null ? List.of() : idsAlbum;
        // Se valida todo antes de crear nada: o queda la lista completa o no queda nada.
        for (int id : ids) {
            DatosFalsos.albumPorId(id);
        }
        Lista lista = new Lista();
        lista.setIdLista(DatosFalsos.nuevoIdLista());
        lista.setIdUsuario(actor.getIdUsuario());
        lista.setNombre(nombre);
        lista.setDescripcion(descripcion);
        lista.setEsPublica(esPublica);
        lista.setFechaCreacion(LocalDateTime.now());
        DatosFalsos.LISTAS.add(lista);
        DatosFalsos.ALBUMES_DE_LISTA.put(lista.getIdLista(), new ArrayList<>(ids));
        return lista;
    }

    @Override
    public void agregarAlbum(Usuario actor, int idLista, int idAlbum) {
        Lista lista = DatosFalsos.listaPorId(idLista);
        ControlAcceso.exigirPropietario(actor, lista.getIdUsuario());
        DatosFalsos.albumPorId(idAlbum);
        List<Integer> albumes = DatosFalsos.ALBUMES_DE_LISTA.get(idLista);
        if (albumes.contains(idAlbum)) {
            throw new ReglaNegocioException("El álbum ya está en la lista.");
        }
        albumes.add(idAlbum);
    }

    @Override
    public void quitarAlbum(Usuario actor, int idLista, int idAlbum) {
        Lista lista = DatosFalsos.listaPorId(idLista);
        ControlAcceso.exigirPropietario(actor, lista.getIdUsuario());
        DatosFalsos.ALBUMES_DE_LISTA.get(idLista).remove(Integer.valueOf(idAlbum));
    }

    @Override
    public void reordenar(Usuario actor, int idLista, List<Integer> idsAlbumEnOrden) {
        Lista lista = DatosFalsos.listaPorId(idLista);
        ControlAcceso.exigirPropietario(actor, lista.getIdUsuario());
        List<Integer> actuales = DatosFalsos.ALBUMES_DE_LISTA.get(idLista);
        if (idsAlbumEnOrden == null
                || idsAlbumEnOrden.size() != actuales.size()
                || !new HashSet<>(idsAlbumEnOrden).equals(new HashSet<>(actuales))) {
            throw new ReglaNegocioException("El orden debe incluir exactamente los álbumes de la lista.");
        }
        DatosFalsos.ALBUMES_DE_LISTA.put(idLista, new ArrayList<>(idsAlbumEnOrden));
    }

    @Override
    public List<Lista> listarMisListas(Usuario actor) {
        ControlAcceso.exigirSesion(actor);
        List<Lista> resultado = new ArrayList<>();
        for (Lista l : DatosFalsos.LISTAS) {
            if (l.getIdUsuario() == actor.getIdUsuario()) {
                resultado.add(l);
            }
        }
        return resultado;
    }

    @Override
    public Lista obtenerLista(int idLista) {
        return DatosFalsos.listaPorId(idLista);
    }

    @Override
    public void eliminarLista(Usuario actor, int idLista) {
        Lista lista = DatosFalsos.listaPorId(idLista);
        ControlAcceso.exigirPropietario(actor, lista.getIdUsuario());
        DatosFalsos.LISTAS.remove(lista);
        DatosFalsos.ALBUMES_DE_LISTA.remove(idLista);
    }
}

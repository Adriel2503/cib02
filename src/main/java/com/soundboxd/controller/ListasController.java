package com.soundboxd.controller;

import com.soundboxd.model.Album;
import com.soundboxd.model.Lista;
import com.soundboxd.model.Usuario;
import com.soundboxd.view.Navegador;
import com.soundboxd.view.Servicios;
import com.soundboxd.view.Sesion;
import com.soundboxd.view.Tareas;
import javafx.fxml.FXML;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;

import java.util.ArrayList;
import java.util.List;

/** Pantalla listas.fxml (M4): crear listas, agregar/quitar álbumes, reordenar y eliminar. */
public class ListasController {

    private record Cargado(List<Lista> listas, List<Album> catalogo) { }

    @FXML
    private ListView<String> lstListas;
    @FXML
    private ListView<String> lstAlbumes;
    @FXML
    private TextField txtNombre;
    @FXML
    private TextField txtDescripcion;
    @FXML
    private CheckBox chkPublica;
    @FXML
    private ComboBox<String> cmbCatalogo;
    @FXML
    private Label lblMensaje;

    private List<Lista> listas = new ArrayList<>();       // misma posición que lstListas
    private List<Album> catalogo = new ArrayList<>();     // misma posición que cmbCatalogo
    private List<Album> albumesLista = new ArrayList<>(); // misma posición que lstAlbumes

    @FXML
    private void initialize() {
        if (!Sesion.haySesion()) {
            lblMensaje.setText("Debes iniciar sesión para ver tus listas.");
            return;
        }
        lstListas.getSelectionModel().selectedIndexProperty().addListener((obs, antes, ahora) -> cargarAlbumes(-1));
        recargar(-1);
    }

    @FXML
    private void onVolver() {
        Navegador.ir("catalogo");
    }

    @FXML
    private void onCrear() {
        lblMensaje.setText("");
        Usuario actor = Sesion.getUsuario();
        String nombre = txtNombre.getText().trim();
        String descripcion = txtDescripcion.getText().trim();
        boolean publica = chkPublica.isSelected();
        Tareas.ejecutarAccion(
                () -> Servicios.listas().crearListaConAlbumes(actor, nombre, descripcion, publica, List.of()),
                () -> {
                    txtNombre.setText("");
                    txtDescripcion.setText("");
                    lblMensaje.setText("Lista creada.");
                    recargar(Integer.MAX_VALUE);   // selecciona la última (la nueva)
                },
                lblMensaje::setText);
    }

    @FXML
    private void onEliminar() {
        Lista lista = listaSeleccionada();
        if (lista == null) {
            return;
        }
        Usuario actor = Sesion.getUsuario();
        Tareas.ejecutarAccion(
                () -> Servicios.listas().eliminarLista(actor, lista.getIdLista()),
                () -> {
                    lblMensaje.setText("Lista eliminada.");
                    recargar(-1);
                },
                lblMensaje::setText);
    }

    @FXML
    private void onAgregar() {
        Lista lista = listaSeleccionada();
        if (lista == null) {
            return;
        }
        int pos = cmbCatalogo.getSelectionModel().getSelectedIndex();
        if (pos < 0) {
            lblMensaje.setText("Elige el álbum que quieres agregar.");
            return;
        }
        Album album = catalogo.get(pos);
        Usuario actor = Sesion.getUsuario();
        Tareas.ejecutarAccion(
                () -> Servicios.listas().agregarAlbum(actor, lista.getIdLista(), album.getIdAlbum()),
                () -> {
                    lblMensaje.setText("Álbum agregado.");
                    cargarAlbumes(-1);
                },
                lblMensaje::setText);
    }

    @FXML
    private void onQuitar() {
        Lista lista = listaSeleccionada();
        int pos = lstAlbumes.getSelectionModel().getSelectedIndex();
        if (lista == null) {
            return;
        }
        if (pos < 0 || pos >= albumesLista.size()) {
            lblMensaje.setText("Selecciona el álbum que quieres quitar.");
            return;
        }
        Album album = albumesLista.get(pos);
        Usuario actor = Sesion.getUsuario();
        Tareas.ejecutarAccion(
                () -> Servicios.listas().quitarAlbum(actor, lista.getIdLista(), album.getIdAlbum()),
                () -> {
                    lblMensaje.setText("Álbum quitado.");
                    cargarAlbumes(-1);
                },
                lblMensaje::setText);
    }

    @FXML
    private void onSubir() {
        mover(-1);
    }

    @FXML
    private void onBajar() {
        mover(1);
    }

    private void mover(int delta) {
        Lista lista = listaSeleccionada();
        int pos = lstAlbumes.getSelectionModel().getSelectedIndex();
        if (lista == null) {
            return;
        }
        int destino = pos + delta;
        if (pos < 0 || destino < 0 || destino >= albumesLista.size()) {
            lblMensaje.setText("Selecciona un álbum que se pueda mover en esa dirección.");
            return;
        }
        List<Integer> orden = new ArrayList<>();
        for (Album a : albumesLista) {
            orden.add(a.getIdAlbum());
        }
        int movido = orden.remove(pos);
        orden.add(destino, movido);
        Usuario actor = Sesion.getUsuario();
        Tareas.ejecutarAccion(
                () -> Servicios.listas().reordenar(actor, lista.getIdLista(), orden),
                () -> cargarAlbumes(destino),
                lblMensaje::setText);
    }

    /** Recarga las listas y el catálogo; luego selecciona la lista en la posición indicada (-1 = ninguna). */
    private void recargar(int seleccionar) {
        Usuario actor = Sesion.getUsuario();
        Tareas.ejecutar(
                () -> new Cargado(Servicios.listas().listarMisListas(actor), Servicios.catalogo().buscarAlbumes("")),
                c -> {
                    listas = new ArrayList<>(c.listas());
                    lstListas.getItems().clear();
                    for (Lista l : listas) {
                        lstListas.getItems().add(l.getNombre() + (l.isEsPublica() ? " (pública)" : " (privada)"));
                    }
                    catalogo = new ArrayList<>(c.catalogo());
                    cmbCatalogo.getItems().clear();
                    for (Album a : catalogo) {
                        cmbCatalogo.getItems().add(a.getTitulo() + " — " + a.getNombreArtista());
                    }
                    if (seleccionar >= 0 && !listas.isEmpty()) {
                        lstListas.getSelectionModel().select(Math.min(seleccionar, listas.size() - 1));
                    } else {
                        albumesLista = new ArrayList<>();
                        lstAlbumes.getItems().clear();
                    }
                },
                lblMensaje::setText);
    }

    /** Carga los álbumes de la lista elegida; luego selecciona la posición indicada (-1 = ninguna). */
    private void cargarAlbumes(int seleccionar) {
        int posLista = lstListas.getSelectionModel().getSelectedIndex();
        Lista lista = posLista >= 0 && posLista < listas.size() ? listas.get(posLista) : null;   // sin avisos
        if (lista == null) {
            albumesLista = new ArrayList<>();
            lstAlbumes.getItems().clear();
            return;
        }
        Tareas.ejecutar(
                () -> Servicios.albumesDeLista(lista.getIdLista()),
                albumes -> {
                    albumesLista = new ArrayList<>(albumes);
                    lstAlbumes.getItems().clear();
                    int n = 1;
                    for (Album a : albumesLista) {
                        lstAlbumes.getItems().add(n++ + ". " + a.getTitulo() + " — " + a.getNombreArtista());
                    }
                    if (seleccionar >= 0 && seleccionar < albumesLista.size()) {
                        lstAlbumes.getSelectionModel().select(seleccionar);
                    }
                },
                lblMensaje::setText);
    }

    /** Devuelve la lista elegida, o null avisando en pantalla. */
    private Lista listaSeleccionada() {
        int pos = lstListas.getSelectionModel().getSelectedIndex();
        if (pos < 0 || pos >= listas.size()) {
            lblMensaje.setText("Selecciona una lista.");
            return null;
        }
        return listas.get(pos);
    }
}

package com.soundboxd.controller;

import com.soundboxd.model.Album;
import com.soundboxd.model.Cancion;
import com.soundboxd.model.Lista;
import com.soundboxd.model.Resena;
import com.soundboxd.model.Usuario;
import com.soundboxd.view.Formato;
import com.soundboxd.view.Navegador;
import com.soundboxd.view.Servicios;
import com.soundboxd.view.Sesion;
import com.soundboxd.view.Tareas;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;

import java.util.ArrayList;
import java.util.List;

/** Pantalla detalle.fxml (M2, M3): datos del álbum, canciones y reseñas publicadas. */
public class DetalleController {

    /** Todo lo que necesita la pantalla, leído en un solo trabajo en segundo plano. */
    private record Datos(Album album, double promedio, List<Cancion> canciones,
                         List<Resena> resenas, List<Lista> misListas) { }

    @FXML
    private Label lblTitulo;
    @FXML
    private Label lblInfo;
    @FXML
    private Label lblPromedio;
    @FXML
    private ListView<String> lstCanciones;
    @FXML
    private ListView<String> lstResenas;
    @FXML
    private Button btnEscribir;
    @FXML
    private Button btnSeguirAutor;
    @FXML
    private Button btnReportar;
    @FXML
    private ComboBox<String> cmbListas;
    @FXML
    private Button btnAgregarALista;
    @FXML
    private Label lblMensaje;

    private int idAlbum;
    private List<Resena> resenas = new ArrayList<>();   // misma posición que lstResenas
    private List<Lista> misListas = new ArrayList<>();  // misma posición que cmbListas

    @FXML
    private void initialize() {
        boolean sesion = Sesion.haySesion();
        btnEscribir.setDisable(!sesion);
        btnSeguirAutor.setDisable(!sesion);
        btnReportar.setDisable(!sesion);
        btnAgregarALista.setDisable(!sesion);
        cmbListas.setDisable(!sesion);
        if (!sesion) {
            lblMensaje.setText("Inicia sesión para escribir reseñas, seguir autores o guardar el álbum en una lista.");
        }
    }

    /** Lo llama el Navegador justo después de cargar la pantalla. */
    public void mostrar(int idAlbum) {
        this.idAlbum = idAlbum;
        Usuario actor = Sesion.getUsuario();
        Tareas.ejecutar(
                () -> new Datos(
                        Servicios.catalogo().obtenerAlbum(idAlbum),
                        Servicios.catalogo().promedioAlbum(idAlbum),
                        Servicios.catalogo().listarCanciones(idAlbum),
                        Servicios.resenas().listarPorAlbum(idAlbum),
                        actor == null ? List.<Lista>of() : Servicios.listas().listarMisListas(actor)),
                this::pintar,
                lblMensaje::setText);
    }

    private void pintar(Datos d) {
        lblTitulo.setText(d.album().getTitulo());
        lblInfo.setText(d.album().getNombreArtista() + " · " + d.album().getAnio() + " · " + d.album().getGenero());
        lblPromedio.setText(d.promedio() == 0 ? "Sin reseñas todavía" : "Promedio: " + String.format("%.1f", d.promedio()) + " / 5");

        lstCanciones.getItems().clear();
        for (Cancion c : d.canciones()) {
            lstCanciones.getItems().add(Formato.cancion(c));
        }

        resenas = new ArrayList<>(d.resenas());
        lstResenas.getItems().clear();
        for (Resena r : resenas) {
            lstResenas.getItems().add(Formato.resena(r));
        }

        misListas = new ArrayList<>(d.misListas());
        cmbListas.getItems().clear();
        for (Lista l : misListas) {
            cmbListas.getItems().add(l.getNombre());
        }
    }

    @FXML
    private void onVolver() {
        Navegador.ir("catalogo");
    }

    @FXML
    private void onEscribir() {
        int id = idAlbum;
        Navegador.<EditorResenaController>ir("editor", c -> c.mostrar(id));
    }

    @FXML
    private void onSeguirAutor() {
        Resena r = resenaSeleccionada();
        if (r == null) {
            return;
        }
        Usuario actor = Sesion.getUsuario();
        Tareas.ejecutarAccion(
                () -> Servicios.social().seguir(actor, r.getIdUsuario()),
                () -> lblMensaje.setText("Ahora sigues a " + r.getNombreUsuario() + "."),
                lblMensaje::setText);
    }

    @FXML
    private void onReportar() {
        Resena r = resenaSeleccionada();
        if (r == null) {
            return;
        }
        Usuario actor = Sesion.getUsuario();
        Tareas.ejecutarAccion(
                () -> Servicios.moderacion().reportar(actor, r.getIdResena()),
                () -> {
                    lblMensaje.setText("Reseña reportada. Un moderador la revisará.");
                    mostrar(idAlbum);   // la reseña reportada deja de aparecer en la lista
                },
                lblMensaje::setText);
    }

    @FXML
    private void onAgregarALista() {
        int pos = cmbListas.getSelectionModel().getSelectedIndex();
        if (pos < 0) {
            lblMensaje.setText("Elige una de tus listas.");
            return;
        }
        Lista lista = misListas.get(pos);
        Usuario actor = Sesion.getUsuario();
        int id = idAlbum;
        Tareas.ejecutarAccion(
                () -> Servicios.listas().agregarAlbum(actor, lista.getIdLista(), id),
                () -> lblMensaje.setText("Álbum agregado a \"" + lista.getNombre() + "\"."),
                lblMensaje::setText);
    }

    private Resena resenaSeleccionada() {
        int pos = lstResenas.getSelectionModel().getSelectedIndex();
        if (pos < 0 || pos >= resenas.size()) {
            lblMensaje.setText("Selecciona una reseña de la lista.");
            return null;
        }
        return resenas.get(pos);
    }
}

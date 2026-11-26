package com.soundboxd.controller;

import com.soundboxd.model.Album;
import com.soundboxd.model.EstadoResena;
import com.soundboxd.model.Resena;
import com.soundboxd.model.Usuario;
import com.soundboxd.view.Navegador;
import com.soundboxd.view.Servicios;
import com.soundboxd.view.Sesion;
import com.soundboxd.view.Tareas;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;

/** Pantalla editor.fxml (M3): calificación 1 a 5 y texto; guardar como borrador o publicar. */
public class EditorResenaController {

    private record Inicio(Album album, Resena borrador) { }

    @FXML
    private Label lblAlbum;
    @FXML
    private ComboBox<Integer> cmbCalificacion;
    @FXML
    private TextArea txtTexto;
    @FXML
    private Button btnGuardar;
    @FXML
    private Button btnPublicar;
    @FXML
    private Label lblMensaje;

    private int idAlbum;

    @FXML
    private void initialize() {
        cmbCalificacion.getItems().addAll(1, 2, 3, 4, 5);
        if (!Sesion.haySesion()) {
            btnGuardar.setDisable(true);
            btnPublicar.setDisable(true);
            lblMensaje.setText("Debes iniciar sesión para escribir una reseña.");
        }
    }

    /** Lo llama el Navegador justo después de cargar la pantalla. Si ya hay un borrador, lo carga. */
    public void mostrar(int idAlbum) {
        this.idAlbum = idAlbum;
        Usuario actor = Sesion.getUsuario();
        Tareas.ejecutar(
                () -> {
                    Album album = Servicios.catalogo().obtenerAlbum(idAlbum);
                    Resena borrador = null;
                    if (actor != null) {
                        for (Resena r : Servicios.resenas().listarMias(actor)) {
                            if (r.getIdAlbum() == idAlbum && r.getEstado() == EstadoResena.BORRADOR) {
                                borrador = r;
                            }
                        }
                    }
                    return new Inicio(album, borrador);
                },
                inicio -> {
                    lblAlbum.setText("Reseña de: " + inicio.album().getTitulo());
                    if (inicio.borrador() != null) {
                        cmbCalificacion.setValue(inicio.borrador().getCalificacion());
                        txtTexto.setText(inicio.borrador().getTexto());
                        lblMensaje.setText("Se cargó tu borrador.");
                    }
                },
                lblMensaje::setText);
    }

    @FXML
    private void onGuardar() {
        lblMensaje.setText("");
        Usuario actor = Sesion.getUsuario();
        int nota = calificacion();
        String texto = txtTexto.getText();
        int id = idAlbum;
        Tareas.ejecutarAccion(
                () -> Servicios.resenas().guardarBorrador(actor, id, nota, texto),
                () -> lblMensaje.setText("Borrador guardado."),
                lblMensaje::setText);
    }

    @FXML
    private void onPublicar() {
        lblMensaje.setText("");
        Usuario actor = Sesion.getUsuario();
        int nota = calificacion();
        String texto = txtTexto.getText();
        int id = idAlbum;
        Tareas.ejecutarAccion(
                () -> {
                    Resena borrador = Servicios.resenas().guardarBorrador(actor, id, nota, texto);
                    Servicios.resenas().publicar(actor, borrador.getIdResena());
                },
                () -> Navegador.<DetalleController>ir("detalle", c -> c.mostrar(id)),
                lblMensaje::setText);
    }

    @FXML
    private void onCancelar() {
        int id = idAlbum;
        Navegador.<DetalleController>ir("detalle", c -> c.mostrar(id));
    }

    /** Sin selección se envía 0, para que el servicio responda con su mensaje de rango (1 a 5). */
    private int calificacion() {
        Integer valor = cmbCalificacion.getValue();
        return valor == null ? 0 : valor;
    }
}

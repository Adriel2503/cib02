package com.soundboxd.controller;

import com.soundboxd.model.Resena;
import com.soundboxd.model.Usuario;
import com.soundboxd.view.Navegador;
import com.soundboxd.view.Servicios;
import com.soundboxd.view.Sesion;
import com.soundboxd.view.Tareas;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;

import java.util.ArrayList;
import java.util.List;

/** Pantalla perfil.fxml (M5): a quién sigo, dejar de seguir y actividad reciente de mis seguidos. */
public class PerfilController {

    private static final int LIMITE_ACTIVIDAD = 20;

    private record Cargado(List<Usuario> seguidos, List<String> actividad) { }

    @FXML
    private Label lblNombre;
    @FXML
    private ListView<String> lstSeguidos;
    @FXML
    private Button btnDejar;
    @FXML
    private ListView<String> lstActividad;
    @FXML
    private Label lblMensaje;

    private List<Usuario> seguidos = new ArrayList<>();   // misma posición que lstSeguidos

    @FXML
    private void initialize() {
        if (!Sesion.haySesion()) {
            btnDejar.setDisable(true);
            lblNombre.setText("Visitante");
            lblMensaje.setText("Debes iniciar sesión para ver tu perfil.");
            return;
        }
        lblNombre.setText(Sesion.getUsuario().getNombreUsuario());
        recargar();
    }

    @FXML
    private void onVolver() {
        Navegador.ir("catalogo");
    }

    @FXML
    private void onDejarDeSeguir() {
        int pos = lstSeguidos.getSelectionModel().getSelectedIndex();
        if (pos < 0 || pos >= seguidos.size()) {
            lblMensaje.setText("Selecciona a quién quieres dejar de seguir.");
            return;
        }
        Usuario otro = seguidos.get(pos);
        Usuario actor = Sesion.getUsuario();
        Tareas.ejecutarAccion(
                () -> Servicios.social().dejarDeSeguir(actor, otro.getIdUsuario()),
                () -> {
                    lblMensaje.setText("Dejaste de seguir a " + otro.getNombreUsuario() + ".");
                    recargar();
                },
                lblMensaje::setText);
    }

    private void recargar() {
        Usuario actor = Sesion.getUsuario();
        Tareas.ejecutar(
                () -> {
                    List<String> actividad = new ArrayList<>();
                    for (Resena r : Servicios.social().actividadReciente(actor, LIMITE_ACTIVIDAD)) {
                        String album = Servicios.catalogo().obtenerAlbum(r.getIdAlbum()).getTitulo();
                        actividad.add(r.getNombreUsuario() + " reseñó \"" + album + "\" · " + r.getCalificacion() + "/5");
                    }
                    return new Cargado(Servicios.social().listarSeguidos(actor), actividad);
                },
                c -> {
                    seguidos = new ArrayList<>(c.seguidos());
                    lstSeguidos.getItems().clear();
                    for (Usuario u : seguidos) {
                        lstSeguidos.getItems().add(u.getNombreUsuario());
                    }
                    lstActividad.getItems().setAll(c.actividad());
                    if (c.actividad().isEmpty()) {
                        lblMensaje.setText("Aún no hay actividad de las personas que sigues.");
                    }
                },
                lblMensaje::setText);
    }
}

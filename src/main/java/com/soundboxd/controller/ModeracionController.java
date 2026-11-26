package com.soundboxd.controller;

import com.soundboxd.model.Resena;
import com.soundboxd.model.Usuario;
import com.soundboxd.view.Formato;
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

/** Pantalla moderacion.fxml (M6): solo rol MODERADOR. Reseñas reportadas: ocultar o restituir. */
public class ModeracionController {

    @FXML
    private ListView<String> lstReportadas;
    @FXML
    private Button btnOcultar;
    @FXML
    private Button btnRestituir;
    @FXML
    private Label lblMensaje;

    private List<Resena> reportadas = new ArrayList<>();   // misma posición que lstReportadas

    @FXML
    private void initialize() {
        // Ocultar los botones es solo comodidad: la validación real la hace el servicio (ControlAcceso).
        if (!Sesion.esModerador()) {
            btnOcultar.setDisable(true);
            btnRestituir.setDisable(true);
            lblMensaje.setText("Esta pantalla es solo para moderadores.");
            return;
        }
        recargar();
    }

    @FXML
    private void onVolver() {
        Navegador.ir("catalogo");
    }

    @FXML
    private void onOcultar() {
        Resena r = seleccionada();
        if (r == null) {
            return;
        }
        Usuario actor = Sesion.getUsuario();
        Tareas.ejecutarAccion(
                () -> Servicios.moderacion().ocultar(actor, r.getIdResena()),
                () -> {
                    lblMensaje.setText("Reseña ocultada.");
                    recargar();
                },
                lblMensaje::setText);
    }

    @FXML
    private void onRestituir() {
        Resena r = seleccionada();
        if (r == null) {
            return;
        }
        Usuario actor = Sesion.getUsuario();
        Tareas.ejecutarAccion(
                () -> Servicios.moderacion().restituir(actor, r.getIdResena()),
                () -> {
                    lblMensaje.setText("Reseña restituida (vuelve a estar publicada).");
                    recargar();
                },
                lblMensaje::setText);
    }

    private void recargar() {
        Usuario actor = Sesion.getUsuario();
        Tareas.ejecutar(
                () -> Servicios.moderacion().listarReportadas(actor),
                lista -> {
                    reportadas = new ArrayList<>(lista);
                    lstReportadas.getItems().clear();
                    for (Resena r : reportadas) {
                        lstReportadas.getItems().add(Formato.resena(r));
                    }
                    if (reportadas.isEmpty()) {
                        lblMensaje.setText("No hay reseñas reportadas.");
                    }
                },
                lblMensaje::setText);
    }

    private Resena seleccionada() {
        int pos = lstReportadas.getSelectionModel().getSelectedIndex();
        if (pos < 0 || pos >= reportadas.size()) {
            lblMensaje.setText("Selecciona una reseña reportada.");
            return null;
        }
        return reportadas.get(pos);
    }
}

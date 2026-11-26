package com.soundboxd.controller;

import com.soundboxd.model.Album;
import com.soundboxd.view.Navegador;
import com.soundboxd.view.Servicios;
import com.soundboxd.view.Sesion;
import com.soundboxd.view.Tareas;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;

import java.util.ArrayList;
import java.util.List;

/** Pantalla catalogo.fxml (M2): búsqueda de álbumes con su promedio y acceso al resto de pantallas. */
public class CatalogoController {

    /** Una fila de la tabla: el álbum y su promedio ya calculado (se pide una sola vez por búsqueda). */
    private record FilaAlbum(Album album, double promedio) { }

    @FXML
    private Label lblUsuario;
    @FXML
    private Button btnSesion;
    @FXML
    private Button btnMisListas;
    @FXML
    private Button btnPerfil;
    @FXML
    private Button btnModeracion;
    @FXML
    private TextField txtBuscar;
    @FXML
    private TableView<FilaAlbum> tabla;
    @FXML
    private TableColumn<FilaAlbum, String> colTitulo;
    @FXML
    private TableColumn<FilaAlbum, String> colArtista;
    @FXML
    private TableColumn<FilaAlbum, String> colAnio;
    @FXML
    private TableColumn<FilaAlbum, String> colGenero;
    @FXML
    private TableColumn<FilaAlbum, String> colPromedio;
    @FXML
    private Label lblMensaje;

    @FXML
    private void initialize() {
        colTitulo.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().album().getTitulo()));
        colArtista.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().album().getNombreArtista()));
        colAnio.setCellValueFactory(c -> new SimpleStringProperty(String.valueOf(c.getValue().album().getAnio())));
        colGenero.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().album().getGenero()));
        colPromedio.setCellValueFactory(c -> new SimpleStringProperty(formatoPromedio(c.getValue().promedio())));

        boolean sesion = Sesion.haySesion();
        if (sesion) {
            lblUsuario.setText("Hola, " + Sesion.getUsuario().getNombreUsuario());
            btnSesion.setText("Cerrar sesión");
        } else {
            lblUsuario.setText("Visitante");
            btnSesion.setText("Ingresar");
        }
        mostrarSi(btnMisListas, sesion);
        mostrarSi(btnPerfil, sesion);
        mostrarSi(btnModeracion, Sesion.esModerador());   // la validación real está en el servicio
        cargar("");
    }

    @FXML
    private void onBuscar() {
        cargar(txtBuscar.getText());
    }

    @FXML
    private void onDetalle() {
        FilaAlbum fila = tabla.getSelectionModel().getSelectedItem();
        if (fila == null) {
            lblMensaje.setText("Selecciona un álbum de la tabla.");
            return;
        }
        int id = fila.album().getIdAlbum();
        Navegador.<DetalleController>ir("detalle", c -> c.mostrar(id));
    }

    @FXML
    private void onMisListas() {
        Navegador.ir("listas");
    }

    @FXML
    private void onPerfil() {
        Navegador.ir("perfil");
    }

    @FXML
    private void onModeracion() {
        Navegador.ir("moderacion");
    }

    @FXML
    private void onSesion() {
        if (Sesion.haySesion()) {
            Servicios.auth().cerrarSesion();
            Sesion.cerrar();   // por si la implementación real de cerrarSesion no limpia la sesión
        }
        Navegador.ir("login");
    }

    private void cargar(String texto) {
        lblMensaje.setText("Cargando...");
        Tareas.ejecutar(
                () -> {
                    List<FilaAlbum> filas = new ArrayList<>();
                    for (Album album : Servicios.catalogo().buscarAlbumes(texto)) {
                        filas.add(new FilaAlbum(album, Servicios.catalogo().promedioAlbum(album.getIdAlbum())));
                    }
                    return filas;
                },
                filas -> {
                    ObservableList<FilaAlbum> items = FXCollections.observableArrayList();
                    items.addAll(filas);
                    tabla.setItems(items);
                    lblMensaje.setText(filas.isEmpty() ? "No se encontraron álbumes." : filas.size() + " álbum(es)");
                },
                lblMensaje::setText);
    }

    private static void mostrarSi(Button boton, boolean visible) {
        boton.setVisible(visible);
        boton.setManaged(visible);
    }

    private static String formatoPromedio(double promedio) {
        return promedio == 0 ? "Sin reseñas" : String.format("%.1f", promedio);
    }
}

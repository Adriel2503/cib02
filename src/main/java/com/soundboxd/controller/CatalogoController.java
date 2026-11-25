package com.soundboxd.controller;

import com.soundboxd.model.Album;
import com.soundboxd.util.ReglaNegocioException;
import com.soundboxd.view.Navegador;
import com.soundboxd.view.Servicios;
import com.soundboxd.view.Sesion;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;

import java.util.List;

/** Pantalla catalogo.fxml (M2): búsqueda de álbumes con su promedio. Solo llama a servicios. */
public class CatalogoController {

    /** Una fila de la tabla: el álbum y su promedio ya calculado (se pide una sola vez por búsqueda). */
    private record FilaAlbum(Album album, double promedio) { }

    @FXML
    private Label lblUsuario;
    @FXML
    private Button btnSesion;
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

        if (Sesion.haySesion()) {
            lblUsuario.setText("Hola, " + Sesion.getUsuario().getNombreUsuario());
            btnSesion.setText("Cerrar sesión");
        } else {
            lblUsuario.setText("Visitante");
            btnSesion.setText("Ingresar");
        }
        cargar("");
    }

    @FXML
    private void onBuscar() {
        cargar(txtBuscar.getText());
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
        try {
            List<Album> albumes = Servicios.catalogo().buscarAlbumes(texto);
            ObservableList<FilaAlbum> filas = FXCollections.observableArrayList();
            for (Album album : albumes) {
                filas.add(new FilaAlbum(album, Servicios.catalogo().promedioAlbum(album.getIdAlbum())));
            }
            tabla.setItems(filas);
            lblMensaje.setText(albumes.isEmpty() ? "No se encontraron álbumes." : albumes.size() + " álbum(es)");
        } catch (ReglaNegocioException e) {
            lblMensaje.setText(e.getMessage());
        } catch (RuntimeException e) {
            lblMensaje.setText("Ocurrió un error inesperado. Inténtalo de nuevo.");
            e.printStackTrace();
        }
    }

    private static String formatoPromedio(double promedio) {
        return promedio == 0 ? "Sin reseñas" : String.format("%.1f", promedio);
    }
}

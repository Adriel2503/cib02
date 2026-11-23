package com.soundboxd;

import javafx.application.Application;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

/**
 * Punto de entrada de la interfaz JavaFX de Soundboxd.
 *
 * Por ahora solo muestra una ventana de comprobación. Cuando existan las
 * pantallas FXML, el Navegador (paquete view) cargará aquí la primera vista.
 *
 * Para ejecutar desde la consola:  mvn javafx:run
 */
public class Main extends Application {

    public static final String TITULO = "Soundboxd";

    @Override
    public void start(Stage escenario) {
        Label titulo = new Label(TITULO);
        titulo.setStyle("-fx-font-size: 32px; -fx-font-weight: bold;");

        Label detalle = new Label("Esqueleto Maven listo · Java "
                + System.getProperty("java.version")
                + " · JavaFX " + System.getProperty("javafx.version"));

        VBox raiz = new VBox(12, titulo, detalle);
        raiz.setAlignment(Pos.CENTER);

        escenario.setTitle(TITULO);
        escenario.setScene(new Scene(raiz, 960, 600));
        escenario.show();
    }

    public static void main(String[] args) {
        Application.launch(Main.class, args);
    }
}

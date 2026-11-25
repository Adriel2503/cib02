package com.soundboxd;

import com.soundboxd.view.Navegador;
import javafx.application.Application;
import javafx.stage.Stage;

/**
 * Punto de entrada de la interfaz JavaFX de Soundboxd.
 * Abre la primera pantalla (login) a través del Navegador.
 *
 * Para ejecutar desde la consola:  mvn javafx:run
 */
public class Main extends Application {

    public static final String TITULO = "Soundboxd";

    @Override
    public void start(Stage escenario) {
        Navegador.iniciar(escenario);
    }

    public static void main(String[] args) {
        Application.launch(Main.class, args);
    }
}

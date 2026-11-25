package com.soundboxd.view;

import com.soundboxd.Main;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.function.Consumer;

/**
 * Único lugar donde se cambia de pantalla.
 *
 * Uso:   Navegador.ir("catalogo");
 * Con parámetros (se ejecuta justo después de cargar el FXML):
 *        Navegador.<DetalleController>ir("detalle", c -> c.mostrar(idAlbum));
 */
public final class Navegador {

    private static final String CSS = "/css/soundboxd.css";

    private static Stage escenario;

    private Navegador() { }

    /** Llamado una sola vez desde Main.start: configura la ventana y abre la primera pantalla. */
    public static void iniciar(Stage ventana) {
        escenario = ventana;
        escenario.setTitle(Main.TITULO);
        escenario.setMinWidth(900);
        escenario.setMinHeight(600);
        ir("login");
        escenario.show();
    }

    /** Carga /fxml/NOMBRE.fxml y lo muestra en la ventana. */
    public static void ir(String nombre) {
        Navegador.<Object>ir(nombre, null);
    }

    public static <T> void ir(String nombre, Consumer<T> configurar) {
        try {
            FXMLLoader cargador = new FXMLLoader(Navegador.class.getResource("/fxml/" + nombre + ".fxml"));
            Parent raiz = cargador.load();
            if (configurar != null) {
                T controlador = cargador.getController();
                configurar.accept(controlador);
            }
            Scene escena = escenario.getScene();
            if (escena == null) {
                escena = new Scene(raiz, 1000, 650);
                escena.getStylesheets().add(Navegador.class.getResource(CSS).toExternalForm());
                escenario.setScene(escena);
            } else {
                escena.setRoot(raiz);
            }
        } catch (IOException e) {
            throw new IllegalStateException("No se pudo cargar la pantalla " + nombre, e);
        }
    }
}

package com.soundboxd.view;

import com.soundboxd.util.ReglaNegocioException;
import javafx.concurrent.Task;

import java.util.function.Consumer;
import java.util.function.Supplier;

/**
 * Ejecuta llamadas a servicios fuera del hilo de la interfaz para no congelar la ventana.
 * Los callbacks alExito y alError SIEMPRE corren en el hilo de JavaFX, así que pueden tocar controles.
 *
 * IMPORTANTE: lee todo lo que necesites de los controles (textos, selección, Sesion.getUsuario())
 * ANTES de llamar a ejecutar; el trabajo corre en otro hilo y no debe tocar controles.
 */
public final class Tareas {

    public static final String ERROR_INESPERADO = "Ocurrió un error inesperado. Inténtalo de nuevo.";

    private Tareas() { }

    /** Trabajo que devuelve un resultado (consultas). */
    public static <T> void ejecutar(Supplier<T> trabajo, Consumer<T> alExito, Consumer<String> alError) {
        Task<T> tarea = new Task<>() {
            @Override
            protected T call() {
                return trabajo.get();
            }
        };
        tarea.setOnSucceeded(e -> alExito.accept(tarea.getValue()));
        tarea.setOnFailed(e -> {
            Throwable causa = tarea.getException();
            if (causa instanceof ReglaNegocioException) {
                alError.accept(causa.getMessage());
            } else {
                if (causa != null) {
                    causa.printStackTrace();   // solo en la consola del desarrollador
                }
                alError.accept(ERROR_INESPERADO);
            }
        });
        Thread hilo = new Thread(tarea, "soundboxd-tarea");
        hilo.setDaemon(true);
        hilo.start();
    }

    /** Trabajo sin resultado (acciones: guardar, publicar, seguir...). */
    public static void ejecutarAccion(Runnable trabajo, Runnable alExito, Consumer<String> alError) {
        ejecutar(() -> {
            trabajo.run();
            return Boolean.TRUE;
        }, ok -> alExito.run(), alError);
    }
}

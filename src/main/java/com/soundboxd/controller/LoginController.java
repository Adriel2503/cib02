package com.soundboxd.controller;

import com.soundboxd.model.Usuario;
import com.soundboxd.util.ReglaNegocioException;
import com.soundboxd.view.Navegador;
import com.soundboxd.view.Servicios;
import com.soundboxd.view.Sesion;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

/** Pantalla login.fxml (M1): ingreso, registro y entrada como visitante. Solo llama a servicios. */
public class LoginController {

    private static final String ERROR_INESPERADO = "Ocurrió un error inesperado. Inténtalo de nuevo.";

    @FXML
    private TextField txtUsuario;
    @FXML
    private PasswordField txtClave;
    @FXML
    private Label lblErrorLogin;

    @FXML
    private TextField txtNuevoUsuario;
    @FXML
    private TextField txtCorreo;
    @FXML
    private PasswordField txtNuevaClave;
    @FXML
    private Label lblErrorRegistro;

    @FXML
    private void onIngresar() {
        lblErrorLogin.setText("");
        try {
            Usuario usuario = Servicios.auth().iniciarSesion(txtUsuario.getText().trim(), txtClave.getText());
            Sesion.iniciar(usuario);
            Navegador.ir("catalogo");
        } catch (ReglaNegocioException e) {
            lblErrorLogin.setText(e.getMessage());
        } catch (RuntimeException e) {
            lblErrorLogin.setText(ERROR_INESPERADO);
            e.printStackTrace();   // solo en la consola del desarrollador, nunca en pantalla
        }
    }

    @FXML
    private void onRegistrar() {
        lblErrorRegistro.setText("");
        try {
            String nombre = txtNuevoUsuario.getText().trim();
            String clave = txtNuevaClave.getText();
            Servicios.auth().registrar(nombre, txtCorreo.getText().trim(), clave);
            Sesion.iniciar(Servicios.auth().iniciarSesion(nombre, clave));
            Navegador.ir("catalogo");
        } catch (ReglaNegocioException e) {
            lblErrorRegistro.setText(e.getMessage());
        } catch (RuntimeException e) {
            lblErrorRegistro.setText(ERROR_INESPERADO);
            e.printStackTrace();
        }
    }

    @FXML
    private void onVisitante() {
        Sesion.cerrar();
        Navegador.ir("catalogo");
    }
}

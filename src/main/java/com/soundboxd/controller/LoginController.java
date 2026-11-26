package com.soundboxd.controller;

import com.soundboxd.model.Usuario;
import com.soundboxd.view.Navegador;
import com.soundboxd.view.Servicios;
import com.soundboxd.view.Sesion;
import com.soundboxd.view.Tareas;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

/** Pantalla login.fxml (M1): ingreso, registro y entrada como visitante. Solo llama a servicios. */
public class LoginController {

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
        String usuario = txtUsuario.getText().trim();
        String clave = txtClave.getText();
        Tareas.ejecutar(
                () -> Servicios.auth().iniciarSesion(usuario, clave),
                this::entrar,
                lblErrorLogin::setText);
    }

    @FXML
    private void onRegistrar() {
        lblErrorRegistro.setText("");
        String nombre = txtNuevoUsuario.getText().trim();
        String correo = txtCorreo.getText().trim();
        String clave = txtNuevaClave.getText();
        Tareas.ejecutar(
                () -> {
                    Servicios.auth().registrar(nombre, correo, clave);
                    return Servicios.auth().iniciarSesion(nombre, clave);
                },
                this::entrar,
                lblErrorRegistro::setText);
    }

    @FXML
    private void onVisitante() {
        Sesion.cerrar();
        Navegador.ir("catalogo");
    }

    private void entrar(Usuario usuario) {
        Sesion.iniciar(usuario);
        Navegador.ir("catalogo");
    }
}

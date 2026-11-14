/*
 * SPDX-FileCopyrightText: 2026 inici4rsesi0n
 * SPDX-License-Identifier: GPL-3.0-or-later
 */
package presentacion.controlador;
import aplicacion.casosdeuso.portaldeingreso.sistema.AutenticarUsuario;
import dominio.modelo.Usuario;
import dominio.puerto.externo.LoggerPort;
import infraestructura.seguridad.UtilLimpieza;
import presentacion.dialogos.Dialogos;
import presentacion.estadousuario.PersistenciaSesion;
import presentacion.estadousuario.SesionUsuario;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
@Component
@Scope("prototype")
public class LoginController implements Initializable {
    private final AutenticarUsuario autenticarUsuario;
    private final LoggerPort logger;
    @FXML private TextField txtCodigo;
    @FXML private PasswordField txtPassword;
    @FXML private TextField txtPasswordRevealed;
    @FXML private Label btnTogglePassword;
    @FXML private Button btnLogin;
    @FXML private Button btnRegresar;
    @FXML private CheckBox chkMantenerSesion;
    @FXML private ImageView imgLogo;
    private String modo;
    private Usuario usuarioAutenticado;
    private boolean regresando = false;
    public LoginController(AutenticarUsuario autenticarUsuario, LoggerPort logger) {
        this.autenticarUsuario = autenticarUsuario;
        this.logger = logger;
    }
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        sincronizarPasswordFields();
        btnTogglePassword.setOnMouseClicked(this::togglePasswordVisibility);
        txtPasswordRevealed.textProperty().addListener((obs, oldVal, newVal) -> {
            if (txtPasswordRevealed.isVisible()) txtPassword.setText(newVal);
        });
        txtPassword.textProperty().addListener((obs, oldVal, newVal) -> {
            if (!txtPasswordRevealed.isVisible()) txtPasswordRevealed.setText(newVal);
        });
    }
    public void limpiarCampos() {
        txtCodigo.clear();
        txtPassword.clear();
        txtPasswordRevealed.clear();
        chkMantenerSesion.setSelected(false);
        usuarioAutenticado = null;
        regresando = false;
    }
    public void setModo(String modo) { this.modo = modo; }
    public boolean isLoginExitoso() { return usuarioAutenticado != null; }
    public Usuario getUsuarioAutenticado() { return usuarioAutenticado; }
    public boolean isRegresando() { return regresando; }
    @FXML
    private void handleLogin() {
        String codigo = txtCodigo.getText();
        char[] contraseña = obtenerContrasena();
        boolean rememberMe = chkMantenerSesion.isSelected();
        logger.audit("[SESSION] login_intento codigo={} rememberMe={}", codigo, rememberMe);
        if (codigo == null || codigo.isBlank() || contraseña == null || contraseña.length == 0) {
            logger.audit("[SESSION] login_fallido codigo={} motivo=campos_vacios", codigo);
            Dialogos.info("Debe ingresar un código y una contraseña.");
            return;
        }
        try {
            Usuario usuario = autenticarUsuario.ejecutar(codigo, contraseña);
            if (usuario != null) {
                this.usuarioAutenticado = usuario;
                SesionUsuario.iniciarSesion(usuario);
                String persistencia;
                if (rememberMe) {
                    persistencia = PersistenciaSesion.guardar(usuario.getCodigo()) ? "escrita" : "error_escritura";
                } else {
                    PersistenciaSesion.borrar();
                    persistencia = "borrada";
                }
                logger.audit("[SESSION] login_exitoso codigo={} rol={} rememberMe={} persistencia={}",
                        usuario.getCodigo(), usuario.getRol(), rememberMe, persistencia);
                Stage stage = (Stage) btnLogin.getScene().getWindow();
                stage.close();
            } else {
                logger.audit("[SESSION] login_fallido codigo={} motivo=credenciales_invalidas", codigo);
                Dialogos.info("Código o contraseña incorrectos.");
            }
        } finally {
            UtilLimpieza.limpiarContraseña(contraseña);
        }
    }
    @FXML
    private void handleRegresar() {
        logger.audit("[SESSION] login_cancelado");
        regresando = true;
        Stage stage = (Stage) btnRegresar.getScene().getWindow();
        stage.close();
    }
    private char[] obtenerContrasena() {
        if (txtPasswordRevealed.isVisible()) {
            String pwd = txtPasswordRevealed.getText();
            return (pwd != null) ? pwd.toCharArray() : null;
        } else {
            String pwd = txtPassword.getText();
            return (pwd != null) ? pwd.toCharArray() : null;
        }
    }
    private void togglePasswordVisibility(MouseEvent event) {
        boolean revelado = txtPasswordRevealed.isVisible();
        txtPasswordRevealed.setVisible(!revelado);
        txtPassword.setVisible(revelado);
        txtPasswordRevealed.setManaged(!revelado);
        txtPassword.setManaged(revelado);
        if (!revelado) {
            txtPasswordRevealed.setText(txtPassword.getText());
            txtPasswordRevealed.requestFocus();
            txtPasswordRevealed.positionCaret(txtPasswordRevealed.getText().length());
        } else {
            txtPassword.setText(txtPasswordRevealed.getText());
            txtPassword.requestFocus();
            txtPassword.positionCaret(txtPassword.getText().length());
        }
        btnTogglePassword.setText(revelado ? "👁" : "🙈");
    }
    private void sincronizarPasswordFields() {
        txtPasswordRevealed.setVisible(false);
        txtPasswordRevealed.setManaged(false);
        txtPassword.setVisible(true);
        txtPassword.setManaged(true);
    }
}

/*
 * SPDX-FileCopyrightText: 2026 inici4rsesi0n
 * SPDX-License-Identifier: GPL-3.0-or-later
 */
package presentacion.controlador;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import aplicacion.puerto.ProveedorDatosIniciales;
import javafx.fxml.FXML;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import presentacion.dialogos.Dialogos;
/**
 *
 * @author inici4rsesi0n
 */
@Component
@Scope("prototype")
public class BootstrapController implements ProveedorDatosIniciales {
    @FXML private TextField campoCodigo;
    @FXML private TextField campoNombre;
    @FXML private TextField campoApellido;
    @FXML private TextField campoDni;
    @FXML private PasswordField campoContrasena;
    private boolean confirmado = false;
    private String codigo;
    private String nombre;
    private String apellido;
    private String dni;
    private char[] contrasena;
    @FXML
    private void initialize() {}
    @FXML
    private void confirmar() {
        String codigoTexto = campoCodigo.getText();
        String nombreTexto = campoNombre.getText();
        String apellidoTexto = campoApellido.getText();
        String dniTexto = campoDni.getText();
        String contrasenaTexto = campoContrasena.getText();
        if (codigoTexto == null || codigoTexto.isBlank()
                || nombreTexto == null || nombreTexto.isBlank()
                || apellidoTexto == null || apellidoTexto.isBlank()
                || dniTexto == null || dniTexto.isBlank()
                || contrasenaTexto == null || contrasenaTexto.isBlank()) {
            return;
        }
        if (!dniTexto.trim().matches("[A-Za-z0-9]{4,20}")) {
            Dialogos.info("DNI debe ser alfanumérico de 4 a 20 caracteres");
            return;
        }
        this.codigo = codigoTexto.trim();
        this.nombre = nombreTexto.trim();
        this.apellido = apellidoTexto.trim();
        this.dni = dniTexto.trim();
        this.contrasena = contrasenaTexto.toCharArray();
        this.confirmado = true;
        campoCodigo.getScene().getWindow().hide();
    }
    @FXML
    private void cancelar() {
        this.confirmado = false;
        campoCodigo.getScene().getWindow().hide();
    }
    @Override public String getCodigo() { return codigo; }
    @Override public String getNombre() { return nombre; }
    @Override public String getApellido() { return apellido; }
    @Override public String getDni() { return dni; }
    @Override public char[] getContrasena() { return contrasena; }
    @Override public boolean isConfirmado() { return confirmado; }
}

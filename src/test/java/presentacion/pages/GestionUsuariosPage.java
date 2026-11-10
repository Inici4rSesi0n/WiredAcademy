/*
 * SPDX-FileCopyrightText: 2026 inici4rsesi0n
 * SPDX-License-Identifier: GPL-3.0-or-later
 */
package presentacion.pages;
import javafx.scene.control.ComboBox;
import org.testfx.api.FxRobot;
import org.testfx.util.WaitForAsyncUtils;
/**
 *
 * @author inici4rsesi0n
 */
public class GestionUsuariosPage {
    private final FxRobot robot;
    public GestionUsuariosPage(FxRobot robot) {this.robot = robot;}
    public GestionUsuariosPage clickNuevoUsuario() {
        robot.clickOn("#btnNuevoUsuario");
        return this;
    }
    @SuppressWarnings("unchecked")
    public GestionUsuariosPage seleccionarRol(String rol) {
        ComboBox<String> cmb = robot.lookup("#cmbRol").queryComboBox();
        WaitForAsyncUtils.waitForAsyncFx(1000, () -> cmb.getSelectionModel().select(rol));
        return this;
    }
    public GestionUsuariosPage escribirNombre(String nombre) {
        robot.clickOn("#txtNombre").write(nombre);
        return this;
    }
    public GestionUsuariosPage escribirApellido(String apellido) {
        robot.clickOn("#txtApellido").write(apellido);
        return this;
    }
    public GestionUsuariosPage escribirDni(String dni) {
        robot.clickOn("#txtDni").write(dni);
        return this;
    }
    public GestionUsuariosPage escribirEdad(String edad) {
        robot.clickOn("#txtEdad").write(edad);
        return this;
    }
    public GestionUsuariosPage escribirContrasena(String pwd) {
        robot.clickOn("#txtContrasena").write(pwd);
        return this;
    }
    public GestionUsuariosPage clickGuardar() {
        robot.clickOn("#btnGuardar");
        return this;
    }
}

/*
 * SPDX-FileCopyrightText: 2026 inici4rsesi0n
 * SPDX-License-Identifier: GPL-3.0-or-later
 */
package presentacion.portaldeingreso.administraciondelsistema.gestionacademica.asignaciones.pages;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import org.testfx.api.FxRobot;
import org.testfx.util.WaitForAsyncUtils;
import presentacion.pages.GestionAcademicaPage;
/**
 *
 * @author inici4rsesi0n
 */
public class AsignacionCoordinadorPage {
    private final FxRobot robot;
    public AsignacionCoordinadorPage(FxRobot robot) {this.robot = robot;}
    public AsignacionCoordinadorPage irACategoriaAsignaciones() {
        new GestionAcademicaPage(robot).seleccionarCategoria("Asignaciones");
        return this;
    }
    public AsignacionCoordinadorPage clickTabAsigCoordinador() {
        robot.clickOn("#tabAsigCoordinador");
        return this;
    }
    @SuppressWarnings("unchecked")
    public AsignacionCoordinadorPage seleccionarCoordinador(String codigoCoordinador) {
        ComboBox<String> cmb = robot.lookup("#cmbCoordinador").queryComboBox();
        WaitForAsyncUtils.waitForAsyncFx(1000, () -> {
            cmb.getItems().stream()
                .filter(item -> item.startsWith(codigoCoordinador + " - "))
                .findFirst()
                .ifPresent(cmb.getSelectionModel()::select);
        });
        return this;
    }
    @SuppressWarnings("unchecked")
    public AsignacionCoordinadorPage seleccionarGrado(String nombreGrado) {
        ComboBox<String> cmb = robot.lookup("#cmbGradoCoordinacion").queryComboBox();
        WaitForAsyncUtils.waitForAsyncFx(1000, () -> {
            cmb.getItems().stream()
                .filter(item -> item.startsWith(nombreGrado + " ("))
                .findFirst()
                .ifPresent(cmb.getSelectionModel()::select);
        });
        return this;
    }
    public AsignacionCoordinadorPage clickAsignar() {
        Button btn = robot.lookup("#btnAsignarCoordinacion").queryAs(Button.class);
        WaitForAsyncUtils.waitForAsyncFx(1000, btn::fire);
        return this;
    }
}

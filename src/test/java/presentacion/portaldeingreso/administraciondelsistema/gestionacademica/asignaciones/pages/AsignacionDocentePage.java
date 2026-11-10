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
public class AsignacionDocentePage {
    private final FxRobot robot;
    public AsignacionDocentePage(FxRobot robot) {this.robot = robot;}
    public AsignacionDocentePage irACategoriaAsignaciones() {
        new GestionAcademicaPage(robot).seleccionarCategoria("Asignaciones");
        return this;
    }
    public AsignacionDocentePage clickTabAsigDocente() {
        robot.clickOn("#tabAsigDocente");
        return this;
    }
    @SuppressWarnings("unchecked")
    public AsignacionDocentePage seleccionarDocente(String codigoDocente) {
        ComboBox<String> cmb = robot.lookup("#cmbDocenteAsignatura").queryComboBox();
        WaitForAsyncUtils.waitForAsyncFx(1000, () -> {
            cmb.getItems().stream()
                .filter(item -> item.startsWith(codigoDocente + " - "))
                .findFirst()
                .ifPresent(cmb.getSelectionModel()::select);
        });
        return this;
    }
    @SuppressWarnings("unchecked")
    public AsignacionDocentePage seleccionarAsignatura(String nombreAsignatura) {
        ComboBox<String> cmb = robot.lookup("#cmbAsignaturaDocente").queryComboBox();
        WaitForAsyncUtils.waitForAsyncFx(1000, () -> cmb.getSelectionModel().select(nombreAsignatura));
        return this;
    }
    public AsignacionDocentePage clickAgregar() {
        Button btn = robot.lookup("#btnAgregarAsignaturaDocente").queryAs(Button.class);
        WaitForAsyncUtils.waitForAsyncFx(1000, btn::fire);
        return this;
    }
}

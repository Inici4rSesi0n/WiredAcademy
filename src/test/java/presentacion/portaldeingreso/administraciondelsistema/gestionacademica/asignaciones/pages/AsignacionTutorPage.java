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
public class AsignacionTutorPage {
    private final FxRobot robot;
    public AsignacionTutorPage(FxRobot robot) {this.robot = robot;}
    public AsignacionTutorPage irACategoriaAsignaciones() {
        new GestionAcademicaPage(robot).seleccionarCategoria("Asignaciones");
        return this;
    }
    public AsignacionTutorPage clickTabAsigTutor() {
        robot.clickOn("#tabAsigTutor");
        return this;
    }
    @SuppressWarnings("unchecked")
    public AsignacionTutorPage seleccionarDocente(String codigoDocente) {
        ComboBox<String> cmb = robot.lookup("#cmbDocenteTutor").queryComboBox();
        WaitForAsyncUtils.waitForAsyncFx(1000, () -> {
            cmb.getItems().stream()
                .filter(item -> item.startsWith(codigoDocente + " - "))
                .findFirst()
                .ifPresent(cmb.getSelectionModel()::select);
        });
        return this;
    }
    @SuppressWarnings("unchecked")
    public AsignacionTutorPage seleccionarGrupo(String nombreGrupo) {
        ComboBox<String> cmb = robot.lookup("#cmbGrupoTutor").queryComboBox();
        WaitForAsyncUtils.waitForAsyncFx(1000, () -> {
            cmb.getItems().stream()
                .filter(item -> item.startsWith(nombreGrupo + " ("))
                .findFirst()
                .ifPresent(cmb.getSelectionModel()::select);
        });
        return this;
    }
    public AsignacionTutorPage clickAsignar() {
        Button btn = robot.lookup("#btnAsignarTutor").queryAs(Button.class);
        WaitForAsyncUtils.waitForAsyncFx(1000, btn::fire);
        return this;
    }
}

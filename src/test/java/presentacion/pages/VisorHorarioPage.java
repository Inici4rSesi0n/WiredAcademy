/*
 * SPDX-FileCopyrightText: 2026 inici4rsesi0n
 * SPDX-License-Identifier: GPL-3.0-or-later
 */
package presentacion.pages;
import javafx.scene.control.ComboBox;
import javafx.scene.layout.GridPane;
import org.testfx.api.FxRobot;
import org.testfx.util.WaitForAsyncUtils;
/**
 *
 * @author inici4rsesi0n
 */
public class VisorHorarioPage {
    private final FxRobot robot;
    public VisorHorarioPage(FxRobot robot) {this.robot = robot;}
    @SuppressWarnings("unchecked")
    public VisorHorarioPage seleccionarGrupo(String grupo) {
        ComboBox<String> cmb = robot.lookup("#cmbGrupoVisor").queryComboBox();
        WaitForAsyncUtils.waitForAsyncFx(1000, () -> cmb.getSelectionModel().select(grupo));
        return this;
    }
    @SuppressWarnings("unchecked")
    public VisorHorarioPage seleccionarPeriodo(String periodo) {
        ComboBox<String> cmb = robot.lookup("#cmbPeriodoVisor").queryComboBox();
        WaitForAsyncUtils.waitForAsyncFx(1000, () -> cmb.getSelectionModel().select(periodo));
        return this;
    }
    public VisorHorarioPage clickGenerarHorario() {
        robot.clickOn("#btnGenerarHorario");
        return this;
    }
    @SuppressWarnings("unchecked")
    public VisorHorarioPage seleccionarAula(String aula) {
        robot.clickOn("Por Aula");
        ComboBox<String> cmb = robot.lookup("#cmbAulaVisor").queryComboBox();
        WaitForAsyncUtils.waitForAsyncFx(1000, () -> cmb.getSelectionModel().select(aula));
        return this;
    }
    @SuppressWarnings("unchecked")
    public VisorHorarioPage seleccionarPeriodoAula(String periodo) {
        ComboBox<String> cmb = robot.lookup("#cmbPeriodoAula").queryComboBox();
        WaitForAsyncUtils.waitForAsyncFx(1000, () -> cmb.getSelectionModel().select(periodo));
        return this;
    }
    public VisorHorarioPage clickGenerarHorarioAula() {
        robot.clickOn("#btnGenerarHorarioAula");
        return this;
    }
    public GridPane getGridHorario() {
        return robot.lookup("#gridHorario").queryAs(GridPane.class);
    }
}

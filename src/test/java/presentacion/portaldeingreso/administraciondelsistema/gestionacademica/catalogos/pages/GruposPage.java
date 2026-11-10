/*
 * SPDX-FileCopyrightText: 2026 inici4rsesi0n
 * SPDX-License-Identifier: GPL-3.0-or-later
 */
package presentacion.portaldeingreso.administraciondelsistema.gestionacademica.catalogos.pages;
import javafx.scene.control.ComboBox;
import org.testfx.api.FxRobot;
import org.testfx.util.WaitForAsyncUtils;
/**
 *
 * @author inici4rsesi0n
 */
public class GruposPage {
    private final FxRobot robot;
    public GruposPage(FxRobot robot) {this.robot = robot;}
    public GruposPage clickTabGrupos() {
        robot.clickOn("#tabGrupos");
        return this;
    }
    public GruposPage clickNuevoGrupo() {
        robot.clickOn("#btnNuevoGlobal");
        return this;
    }
    public GruposPage escribirNombre(String nombre) {
        robot.clickOn("#txtNombreGrupo").write(nombre);
        return this;
    }
    @SuppressWarnings("unchecked")
    public GruposPage seleccionarGrado(String gradoNombre) {
        ComboBox<String> cmb = robot.lookup("#cmbGradoGrupo").queryComboBox();
        WaitForAsyncUtils.waitForAsyncFx(1000, () -> {
            cmb.getItems().stream()
                .filter(item -> item.startsWith(gradoNombre + " ("))
                .findFirst()
                .ifPresent(cmb.getSelectionModel()::select);
        });
        return this;
    }
    public GruposPage clickGuardar() {
        robot.clickOn("#btnGuardarGrupo");
        return this;
    }
}

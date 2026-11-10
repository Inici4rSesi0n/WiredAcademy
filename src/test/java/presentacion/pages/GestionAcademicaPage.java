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
public class GestionAcademicaPage {
    private final FxRobot robot;
    public GestionAcademicaPage(FxRobot robot) {this.robot = robot;}
    @SuppressWarnings("unchecked")
    public GestionAcademicaPage seleccionarCategoria(String categoria) {
        ComboBox<String> cmb = robot.lookup("#cmbCategoria").queryComboBox();
        WaitForAsyncUtils.waitForAsyncFx(1000, () -> cmb.getSelectionModel().select(categoria));
        return this;
    }
    public boolean esVisible(String nodoId) {
        try {
            return robot.lookup(nodoId).query().isVisible();
        } catch (Exception e) {
            return false;
        }
    }
}

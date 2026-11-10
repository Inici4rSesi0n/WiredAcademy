/*
 * SPDX-FileCopyrightText: 2026 inici4rsesi0n
 * SPDX-License-Identifier: GPL-3.0-or-later
 */
package presentacion.portaldeingreso.administraciondelsistema.gestionacademica.catalogos.pages;
import org.testfx.api.FxRobot;
/**
 *
 * @author inici4rsesi0n
 */
public class GradosPage {
    private final FxRobot robot;
    public GradosPage(FxRobot robot) {this.robot = robot;}
    public GradosPage clickTabGrados() {
        robot.clickOn("#tabGrados");
        return this;
    }
    public GradosPage clickNuevoGrado() {
        robot.clickOn("#btnNuevoGlobal");
        return this;
    }
    public GradosPage escribirNombre(String nombre) {
        robot.clickOn("#txtNombreGrado").write(nombre);
        return this;
    }
    public GradosPage escribirNivel(String nivel) {
        robot.clickOn("#txtNivelGrado").write(nivel);
        return this;
    }
    public GradosPage clickGuardar() {
        robot.clickOn("#btnGuardarGrado");
        return this;
    }
}

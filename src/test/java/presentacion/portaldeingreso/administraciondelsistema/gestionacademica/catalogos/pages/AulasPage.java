/*
 * SPDX-FileCopyrightText: 2026 inici4rsesi0n
 * SPDX-License-Identifier: GPL-3.0-or-later
 */
package presentacion.portaldeingreso.administraciondelsistema.gestionacademica.catalogos.pages;
import org.testfx.api.FxRobot;
public class AulasPage {
    private final FxRobot robot;
    public AulasPage(FxRobot robot) {this.robot = robot;}
    public AulasPage clickTabAulas() {
        robot.clickOn("#tabAulas");
        return this;
    }
    public AulasPage clickNuevaAula() {
        robot.clickOn("#btnNuevoGlobal");
        return this;
    }
    public AulasPage escribirNombre(String nombre) {
        robot.clickOn("#txtNombreAula").write(nombre);
        return this;
    }
    public AulasPage escribirCapacidad(String capacidad) {
        robot.clickOn("#txtCapacidadAula").write(capacidad);
        return this;
    }
    public AulasPage escribirUbicacion(String ubicacion) {
        robot.clickOn("#txtUbicacionAula").write(ubicacion);
        return this;
    }
    public AulasPage escribirTipo(String tipo) {
        robot.clickOn("#txtTipoAula").write(tipo);
        return this;
    }
    public AulasPage clickGuardar() {
        robot.clickOn("#btnGuardarAula");
        return this;
    }
}

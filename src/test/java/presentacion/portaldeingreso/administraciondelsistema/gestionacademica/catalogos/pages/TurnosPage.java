package presentacion.portaldeingreso.administraciondelsistema.gestionacademica.catalogos.pages;
import org.testfx.api.FxRobot;
/**
 *
 * @author inici4rsesi0n
 */
public class TurnosPage {
    private final FxRobot robot;
    public TurnosPage(FxRobot robot) {this.robot = robot;}
    public TurnosPage clickTabTurnos() {
        robot.clickOn("#tabTurnos");
        return this;
    }
    public TurnosPage clickNuevoTurno() {
        robot.clickOn("#btnNuevoGlobal");
        return this;
    }
    public TurnosPage escribirNombre(String nombre) {
        robot.clickOn("#txtNombreTurno").write(nombre);
        return this;
    }
    public TurnosPage clickGuardar() {
        robot.clickOn("#btnGuardarTurno");
        return this;
    }
}

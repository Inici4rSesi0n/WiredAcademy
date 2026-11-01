package presentacion.portaldeingreso.administraciondelsistema.gestionacademica.catalogos.pages;
import org.testfx.api.FxRobot;
/**
 *
 * @author inici4rsesi0n
 */
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
    public AulasPage clickGuardar() {
        robot.clickOn("#btnGuardarAula");
        return this;
    }
}

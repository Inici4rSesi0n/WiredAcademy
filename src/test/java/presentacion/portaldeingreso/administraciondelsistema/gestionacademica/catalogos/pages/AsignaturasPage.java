package presentacion.portaldeingreso.administraciondelsistema.gestionacademica.catalogos.pages;
import org.testfx.api.FxRobot;
/**
 *
 * @author inici4rsesi0n
 */
public class AsignaturasPage {
    private final FxRobot robot;
    public AsignaturasPage(FxRobot robot) {this.robot = robot;}
    public AsignaturasPage clickTabAsignaturas() {
        robot.clickOn("#tabAsignaturas");
        return this;
    }
    public AsignaturasPage clickNuevaAsignatura() {
        robot.clickOn("#btnNuevoGlobal");
        return this;
    }
    public AsignaturasPage escribirNombre(String nombre) {
        robot.clickOn("#txtNombreAsignatura").write(nombre);
        return this;
    }
    public AsignaturasPage clickGuardar() {
        robot.clickOn("#btnGuardarAsignatura");
        return this;
    }
}

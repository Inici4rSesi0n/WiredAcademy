package presentacion.pages;
import javafx.scene.control.ComboBox;
import org.testfx.api.FxRobot;
import org.testfx.util.WaitForAsyncUtils;
/**
 *
 * @author inici4rsesi0n
 */
public class SeccionMatriculaPage {
    private final FxRobot robot;
    public SeccionMatriculaPage(FxRobot robot) {this.robot = robot;}
    public SeccionMatriculaPage clickNuevaMatricula() {
        robot.clickOn("#btnNuevaMatricula");
        return this;
    }
    @SuppressWarnings("unchecked")
    public SeccionMatriculaPage seleccionarEstudiante(String codigo) {
        ComboBox<String> cmb = robot.lookup("#cmbEstudianteMatricula").queryComboBox();
        WaitForAsyncUtils.waitForAsyncFx(1000, () -> {
            cmb.getItems().stream()
                .filter(item -> item.startsWith(codigo + " - "))
                .findFirst()
                .ifPresent(cmb.getSelectionModel()::select);
        });
        return this;
    }
    @SuppressWarnings("unchecked")
    public SeccionMatriculaPage seleccionarPeriodo(String nombre) {
        ComboBox<String> cmb = robot.lookup("#cmbPeriodoMatricula").queryComboBox();
        WaitForAsyncUtils.waitForAsyncFx(1000, () -> cmb.getSelectionModel().select(nombre));
        return this;
    }
    @SuppressWarnings("unchecked")
    public SeccionMatriculaPage seleccionarGrupo(String nombreGrupo) {
        ComboBox<String> cmb = robot.lookup("#cmbGrupoMatricula").queryComboBox();
        WaitForAsyncUtils.waitForAsyncFx(1000, () -> {
            cmb.getItems().stream()
                .filter(item -> item.startsWith(nombreGrupo + " ("))
                .findFirst()
                .ifPresent(cmb.getSelectionModel()::select);
        });
        return this;
    }
    public SeccionMatriculaPage escribirFecha(String fecha) {
        robot.clickOn("#txtFechaMatricula").write(fecha);
        return this;
    }
    @SuppressWarnings("unchecked")
    public SeccionMatriculaPage seleccionarEstado(String estado) {
        ComboBox<String> cmb = robot.lookup("#cmbEstadoMatricula").queryComboBox();
        WaitForAsyncUtils.waitForAsyncFx(1000, () -> cmb.getSelectionModel().select(estado));
        return this;
    }
    public SeccionMatriculaPage clickGuardar() {
        robot.clickOn("#btnGuardarMatricula");
        return this;
    }
}

package presentacion.pages;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TableView;
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
    public TableView<?> getTablaHorario() {
        return robot.lookup("#tablaHorario").queryTableView();
    }
}

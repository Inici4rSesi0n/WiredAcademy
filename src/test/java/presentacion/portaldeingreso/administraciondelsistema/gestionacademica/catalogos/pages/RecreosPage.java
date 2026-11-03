package presentacion.portaldeingreso.administraciondelsistema.gestionacademica.catalogos.pages;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import org.testfx.api.FxRobot;
import org.testfx.util.WaitForAsyncUtils;
/**
 *
 * @author inici4rsesi0n
 */
public class RecreosPage {
    private final FxRobot robot;
    public RecreosPage(FxRobot robot) {this.robot = robot;}
    public RecreosPage clickTabRecreos() {
        robot.clickOn("#tabRecreos");
        return this;
    }
    public RecreosPage clickNuevoRecreo() {
        robot.clickOn("#btnNuevoGlobal");
        return this;
    }
    @SuppressWarnings("unchecked")
    public RecreosPage seleccionarDia(String dia) {
        ComboBox<String> cmb = robot.lookup("#cmbDiaRecreo").queryComboBox();
        WaitForAsyncUtils.waitForAsyncFx(1000, () -> cmb.getSelectionModel().select(dia));
        return this;
    }
    @SuppressWarnings("unchecked")
    public RecreosPage seleccionarPeriodo(String nombrePeriodo) {
        ComboBox<String> cmb = robot.lookup("#cmbPeriodoRecreo").queryComboBox();
        WaitForAsyncUtils.waitForAsyncFx(1000, () -> cmb.getSelectionModel().select(nombrePeriodo));
        return this;
    }
    public RecreosPage clickAgregarPeriodo() {
        Button btn = robot.lookup("#btnAgregarPeriodo").queryAs(Button.class);
        WaitForAsyncUtils.waitForAsyncFx(1000, btn::fire);
        return this;
    }
    public RecreosPage escribirHoraInicio(String hora) {
        TextField tf = robot.lookup("#txtInicioRecreo").queryAs(TextField.class);
        WaitForAsyncUtils.waitForAsyncFx(1000, () -> tf.setText(hora));
        return this;
    }
    public RecreosPage escribirHoraFin(String hora) {
        TextField tf = robot.lookup("#txtFinRecreo").queryAs(TextField.class);
        WaitForAsyncUtils.waitForAsyncFx(1000, () -> tf.setText(hora));
        return this;
    }
    public RecreosPage escribirDescripcion(String descripcion) {
        TextField tf = robot.lookup("#txtDescripcionRecreo").queryAs(TextField.class);
        WaitForAsyncUtils.waitForAsyncFx(1000, () -> tf.setText(descripcion));
        return this;
    }
    public RecreosPage clickGuardar() {
        Button btn = robot.lookup("#btnGuardarRecreo").queryAs(Button.class);
        WaitForAsyncUtils.waitForAsyncFx(1000, btn::fire);
        return this;
    }
}

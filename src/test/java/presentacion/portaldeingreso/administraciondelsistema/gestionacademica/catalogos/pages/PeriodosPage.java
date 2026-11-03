package presentacion.portaldeingreso.administraciondelsistema.gestionacademica.catalogos.pages;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import org.testfx.api.FxRobot;
import org.testfx.util.WaitForAsyncUtils;
import java.time.LocalDate;
/**
 *
 * @author inici4rsesi0n
 */
public class PeriodosPage {
    private final FxRobot robot;
    public PeriodosPage(FxRobot robot) {this.robot = robot;}
    public PeriodosPage clickTabPeriodos() {
        robot.clickOn("#tabPeriodos");
        return this;
    }
    public PeriodosPage clickNuevoPeriodo() {
        robot.clickOn("#btnNuevoGlobal");
        return this;
    }
    public PeriodosPage escribirNombre(String nombre) {
        robot.clickOn("#txtNombrePeriodo").write(nombre);
        return this;
    }
    public PeriodosPage escribirFechaInicio(String fecha) {
        DatePicker dp = robot.lookup("#dateFechaInicio").queryAs(DatePicker.class);
        WaitForAsyncUtils.waitForAsyncFx(1000, () -> dp.setValue(LocalDate.parse(fecha)));
        return this;
    }
    public PeriodosPage escribirFechaFin(String fecha) {
        DatePicker dp = robot.lookup("#dateFechaFin").queryAs(DatePicker.class);
        WaitForAsyncUtils.waitForAsyncFx(1000, () -> dp.setValue(LocalDate.parse(fecha)));
        return this;
    }
    @SuppressWarnings("unchecked")
    public PeriodosPage seleccionarEstado(String estado) {
        ComboBox<String> cmb = robot.lookup("#cmbEstadoPeriodo").queryComboBox();
        WaitForAsyncUtils.waitForAsyncFx(1000, () -> cmb.getSelectionModel().select(estado));
        return this;
    }
    public PeriodosPage clickGuardar() {
        robot.clickOn("#btnGuardarPeriodo");
        return this;
    }
}

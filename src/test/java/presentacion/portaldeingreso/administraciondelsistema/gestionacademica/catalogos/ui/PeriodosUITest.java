package presentacion.portaldeingreso.administraciondelsistema.gestionacademica.catalogos.ui;
import dominio.modelo.PeriodoAcademico;
import javafx.scene.control.TableView;
import javafx.stage.Stage;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.testfx.util.WaitForAsyncUtils;
import presentacion.BaseUITest;
import presentacion.cleanup.CatalogCleanupHelper;
import presentacion.portaldeingreso.administraciondelsistema.gestionacademica.catalogos.pages.PeriodosPage;
import java.util.concurrent.TimeUnit;
/**
 *
 * @author inici4rsesi0n
 */
public class PeriodosUITest extends BaseUITest {
    @Override
    public void start(Stage stage) throws Exception {
        loadAndShow(stage, "/fxml/portaldeingreso/administraciondelsistema/gestionacademica/seccion_gestion_academica.fxml");
    }
    @BeforeEach
    public void setUp() {
        new CatalogCleanupHelper(springContext).limpiarTodo();
    }
    @AfterEach
    public void tearDown() {
        new CatalogCleanupHelper(springContext).limpiarTodo();
    }
    @Test
    public void testCrearPeriodo() throws Exception {
        PeriodosPage page = new PeriodosPage(this);
        page.clickTabPeriodos()
            .clickNuevoPeriodo()
            .escribirNombre("UITest-Periodo-1")
            .escribirFechaInicio("2026-01-01")
            .escribirFechaFin("2026-12-31")
            .seleccionarEstado("Activo")
            .clickGuardar();
        WaitForAsyncUtils.waitFor(10, TimeUnit.SECONDS, () -> {
            TableView<?> tabla = lookup("#tablaPeriodos").queryTableView();
            return tabla.getItems().stream()
                .anyMatch(o -> ((PeriodoAcademico) o).getNombre().equals("UITest-Periodo-1"));
        });
    }
}

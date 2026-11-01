package presentacion.ui;
import javafx.stage.Stage;
import org.junit.jupiter.api.Test;
import org.testfx.util.WaitForAsyncUtils;
import presentacion.BaseUITest;
import presentacion.pages.GestionAcademicaPage;
import java.util.concurrent.TimeUnit;
import static org.junit.jupiter.api.Assertions.*;
/**
 *
 * @author inici4rsesi0n
 */
public class GestionAcademicaUITest extends BaseUITest {
    @Override
    public void start(Stage stage) throws Exception {
        loadAndShow(stage, "/fxml/portaldeingreso/administraciondelsistema/gestionacademica/seccion_gestion_academica.fxml");
    }
    @Test
    public void testNavegacionEntreCategorias() throws Exception {
        GestionAcademicaPage page = new GestionAcademicaPage(this);
        WaitForAsyncUtils.waitFor(5, TimeUnit.SECONDS, () -> page.esVisible("#tabPaneCatalogos"));
        assertTrue(page.esVisible("#tabPaneCatalogos"), "Catálogos debe estar visible por defecto");
        assertFalse(page.esVisible("#btnVerHorarioGlobal"), "Ver Horario debe ocultarse fuera de Horarios");
        page.seleccionarCategoria("Asignaciones");
        WaitForAsyncUtils.waitFor(5, TimeUnit.SECONDS, () -> !page.esVisible("#tabPaneCatalogos"));
        assertFalse(page.esVisible("#tabPaneCatalogos"), "Catálogos debe ocultarse al cambiar a Asignaciones");
        assertTrue(page.esVisible("#tabPaneAsignacionesGroup"), "Asignaciones debe estar visible");
        page.seleccionarCategoria("Horarios");
        WaitForAsyncUtils.waitFor(5, TimeUnit.SECONDS, () -> page.esVisible("#btnVerHorarioGlobal"));
        assertFalse(page.esVisible("#tabPaneAsignacionesGroup"), "Asignaciones debe ocultarse");
        assertTrue(page.esVisible("#btnVerHorarioGlobal"), "Ver Horario debe aparecer en Horarios");
    }
}

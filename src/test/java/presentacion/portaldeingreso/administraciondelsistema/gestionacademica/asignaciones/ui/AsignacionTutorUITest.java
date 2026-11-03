package presentacion.portaldeingreso.administraciondelsistema.gestionacademica.asignaciones.ui;
import aplicacion.casosdeuso.portaldeingreso.administraciondelsistema.academico.GestionGrados;
import aplicacion.casosdeuso.portaldeingreso.administraciondelsistema.academico.GestionGrupos;
import aplicacion.casosdeuso.portaldeingreso.administraciondelsistema.usuarios.GestionDocentes;
import dominio.modelo.Docente;
import dominio.modelo.Grado;
import javafx.scene.control.TableView;
import javafx.stage.Stage;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.testfx.util.WaitForAsyncUtils;
import presentacion.BaseUITest;
import presentacion.cleanup.CatalogCleanupHelper;
import presentacion.dto.TutorItem;
import presentacion.portaldeingreso.administraciondelsistema.gestionacademica.asignaciones.pages.AsignacionTutorPage;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;
/**
 *
 * @author inici4rsesi0n
 */
public class AsignacionTutorUITest extends BaseUITest {
    private static final String DOC_CODIGO = "UITest-DOC";
    private static final String DOC_DNI = "99990002";
    private static final String GRADO_NOMBRE = "UITest-GradoTutor";
    private static final String GRUPO_NOMBRE = "UITest-GrupoTutor";
    @Override
    public void start(Stage stage) throws Exception {
        loadAndShow(stage, "/fxml/portaldeingreso/administraciondelsistema/gestionacademica/seccion_gestion_academica.fxml");
    }
    @BeforeEach
    public void setUp() {
        new CatalogCleanupHelper(springContext).limpiarTodo();
        GestionDocentes gd = springContext.getBean(GestionDocentes.class);
        GestionGrados gg = springContext.getBean(GestionGrados.class);
        GestionGrupos ggr = springContext.getBean(GestionGrupos.class);
        gd.agregar(new Docente(DOC_CODIGO, "hash", DOC_DNI, "Test", "Docente", 30, "Mat", new ArrayList<>()));
        gg.crearGrado(GRADO_NOMBRE, "Primaria");
        Grado grado = gg.buscarPorNombreYNivel(GRADO_NOMBRE, "Primaria");
        ggr.crearGrupo(GRUPO_NOMBRE, grado);
        WaitForAsyncUtils.waitForFxEvents();
    }
    @AfterEach
    public void tearDown() {
        new CatalogCleanupHelper(springContext).limpiarTodo();
    }
    @Test
    public void testAsignarTutorAGrupo() throws Exception {
        AsignacionTutorPage page = new AsignacionTutorPage(this);
        page.irACategoriaAsignaciones()
            .clickTabAsigTutor()
            .seleccionarDocente(DOC_CODIGO)
            .seleccionarGrupo(GRUPO_NOMBRE)
            .clickAsignar();
        WaitForAsyncUtils.waitFor(10, TimeUnit.SECONDS, () -> {
            TableView<?> tabla = lookup("#tablaTutores").queryTableView();
            return tabla.getItems().stream()
                .anyMatch(o -> {
                    TutorItem item = (TutorItem) o;
                    return GRUPO_NOMBRE.equals(item.getNombreGrupo());
                });
        });
    }
}

package presentacion.ui;
import aplicacion.casosdeuso.portaldeingreso.administraciondelsistema.academico.GestionGrados;
import aplicacion.casosdeuso.portaldeingreso.administraciondelsistema.academico.GestionGrupos;
import aplicacion.casosdeuso.portaldeingreso.administraciondelsistema.academico.GestionPeriodos;
import aplicacion.casosdeuso.portaldeingreso.administraciondelsistema.usuarios.GestionEstudiantes;
import aplicacion.casosdeuso.portaldeingreso.gestiondetramites.GestionMatriculas;
import dominio.modelo.Estudiante;
import dominio.modelo.Grado;
import dominio.modelo.Matricula;
import javafx.scene.control.TableView;
import javafx.stage.Stage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.testfx.util.WaitForAsyncUtils;
import presentacion.BaseUITest;
import presentacion.pages.SeccionMatriculaPage;
import java.util.concurrent.TimeUnit;
import static org.junit.jupiter.api.Assertions.*;
/**
 *
 * @author inici4rsesi0n
 */
public class SeccionMatriculaUITest extends BaseUITest {
    @Override
    public void start(Stage stage) throws Exception {
        loadAndShow(stage, "/fxml/portaldeingreso/gestiondetramites/matricula/seccion_matricula.fxml");
    }
    @BeforeEach
    public void cleanUp() {
        GestionMatriculas gm = springContext.getBean(GestionMatriculas.class);
        GestionGrupos ggr = springContext.getBean(GestionGrupos.class);
        GestionEstudiantes ge = springContext.getBean(GestionEstudiantes.class);
        GestionPeriodos gp = springContext.getBean(GestionPeriodos.class);
        GestionGrados gg = springContext.getBean(GestionGrados.class);
        try {
            gm.listarTodas().stream()
                .filter(m -> m.getEstudiante() != null && m.getEstudiante().getCodigo().startsWith("UITest-"))
                .forEach(gm::eliminarMatricula);
        } catch (Exception ignored) {}
        try {
            ggr.listarTodos().stream()
                .filter(g -> g.getNombre().startsWith("UITest-"))
                .forEach(ggr::eliminarGrupo);
        } catch (Exception ignored) {}
        try {
            ge.listarTodos().stream()
                .filter(e -> e.getCodigo().startsWith("UITest-"))
                .forEach(ge::eliminar);
        } catch (Exception ignored) {}
        try {
            gp.listarTodos().stream()
                .filter(p -> p.getNombre().startsWith("UITest-"))
                .forEach(gp::eliminarPeriodo);
        } catch (Exception ignored) {}
        try {
            gg.listarTodos().stream()
                .filter(g -> g.getNombre().startsWith("UITest-"))
                .forEach(gg::eliminarGrado);
        } catch (Exception ignored) {}
    }
    @Test
    public void testCrearMatriculaDesdeUI() throws Exception {
        GestionGrados gg = springContext.getBean(GestionGrados.class);
        GestionGrupos ggr = springContext.getBean(GestionGrupos.class);
        GestionPeriodos gp = springContext.getBean(GestionPeriodos.class);
        GestionEstudiantes ge = springContext.getBean(GestionEstudiantes.class);
        GestionMatriculas gm = springContext.getBean(GestionMatriculas.class);
        gg.crearGrado("UITest-Grado", "Primaria");
        Grado grado = gg.buscarPorNombreYNivel("UITest-Grado", "Primaria");
        ggr.crearGrupo("UITest-Grupo", grado);
        gp.crearPeriodo("UITest-Periodo", "2026-01-01", "2026-12-31", "Activo");
        ge.agregar(new Estudiante("UITest-EST", "hash", "99999901", "Est", "UITest", 15));
        SeccionMatriculaPage page = new SeccionMatriculaPage(this);
        page.clickNuevaMatricula()
            .seleccionarEstudiante("UITest-EST")
            .seleccionarPeriodo("UITest-Periodo")
            .seleccionarGrupo("UITest-Grupo")
            .escribirFecha("2026-01-15")
            .seleccionarEstado("ACTIVA")
            .clickGuardar();
        WaitForAsyncUtils.waitFor(10, TimeUnit.SECONDS,
            () -> gm.buscarMatricula("UITest-EST", "UITest-Periodo") != null);
        WaitForAsyncUtils.waitForFxEvents();
        TableView<?> tabla = lookup("#tablaMatriculas").queryTableView();
        long count = tabla.getItems().stream()
            .filter(o -> {
                Matricula m = (Matricula) o;
                return m.getEstudiante() != null && "UITest-EST".equals(m.getEstudiante().getCodigo());
            })
            .count();
        assertTrue(count >= 1, "La matrícula debe aparecer en la tabla");
    }
}

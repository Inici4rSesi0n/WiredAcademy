package presentacion.ui;
import aplicacion.casosdeuso.portaldeingreso.administraciondelsistema.academico.GestionAulas;
import aplicacion.casosdeuso.portaldeingreso.administraciondelsistema.academico.GestionClases;
import aplicacion.casosdeuso.portaldeingreso.administraciondelsistema.academico.GestionGrados;
import aplicacion.casosdeuso.portaldeingreso.administraciondelsistema.academico.GestionGrupos;
import aplicacion.casosdeuso.portaldeingreso.administraciondelsistema.academico.GestionPeriodos;
import aplicacion.casosdeuso.portaldeingreso.administraciondelsistema.academico.GestionAsignaturas;
import aplicacion.casosdeuso.portaldeingreso.administraciondelsistema.usuarios.GestionDocentes;
import dominio.modelo.*;
import javafx.stage.Stage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.testfx.util.WaitForAsyncUtils;
import presentacion.BaseUITest;
import presentacion.pages.VisorHorarioPage;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;
import static org.junit.jupiter.api.Assertions.*;
/**
 *
 * @author inici4rsesi0n
 */
public class VisorHorarioUITest extends BaseUITest {
    private static final String GRADO = "VISOR-Grado";
    private static final String GRUPO = "VISOR-Grupo";
    private static final String PERIODO = "VISOR-Periodo";
    private static final String ASIGNATURA = "VISOR-Asignatura";
    private static final String AULA = "VISOR-Aula";
    private static final String DOCENTE = "VISOR-DOC";
    @Override
    public void start(Stage stage) throws Exception {
        loadAndShow(stage, "/fxml/portaldeingreso/administraciondelsistema/gestionacademica/horarios/visor_horario.fxml");
    }
    @BeforeEach
    public void cleanUp() {
        GestionGrados gg = springContext.getBean(GestionGrados.class);
        GestionGrupos ggr = springContext.getBean(GestionGrupos.class);
        GestionPeriodos gp = springContext.getBean(GestionPeriodos.class);
        GestionAsignaturas ga = springContext.getBean(GestionAsignaturas.class);
        GestionAulas gau = springContext.getBean(GestionAulas.class);
        GestionDocentes gd = springContext.getBean(GestionDocentes.class);
        GestionClases gc = springContext.getBean(GestionClases.class);
        try {
            Clase clase = gc.buscarClase(GRADO, GRUPO, "Lunes", "08:00", "10:00");
            if (clase != null) gc.eliminarClase(clase);
        } catch (Exception ignored) {}
        try {
            Grupo g = ggr.buscarPorNombre(GRUPO);
            if (g != null) ggr.eliminarGrupo(g);
        } catch (Exception ignored) {}
        try {
            Docente d = gd.buscarPorCodigo(DOCENTE);
            if (d != null) gd.eliminar(d);
        } catch (Exception ignored) {}
        try {
            Asignatura a = ga.buscarAsignatura(ASIGNATURA);
            if (a != null) ga.eliminarAsignatura(a);
        } catch (Exception ignored) {}
        try {
            Aula au = gau.buscarAula(AULA);
            if (au != null) gau.eliminarAula(au);
        } catch (Exception ignored) {}
        try {
            PeriodoAcademico p = gp.buscarPorNombre(PERIODO);
            if (p != null) gp.eliminarPeriodo(p);
        } catch (Exception ignored) {}
        try {
            Grado gr = gg.buscarPorNombreYNivel(GRADO, "Primaria");
            if (gr != null) gg.eliminarGrado(gr);
        } catch (Exception ignored) {}
    }
    @Test
    public void testGenerarHorarioConDatos() throws Exception {
        GestionGrados gg = springContext.getBean(GestionGrados.class);
        GestionGrupos ggr = springContext.getBean(GestionGrupos.class);
        GestionPeriodos gp = springContext.getBean(GestionPeriodos.class);
        GestionAsignaturas ga = springContext.getBean(GestionAsignaturas.class);
        GestionAulas gau = springContext.getBean(GestionAulas.class);
        GestionDocentes gd = springContext.getBean(GestionDocentes.class);
        GestionClases gc = springContext.getBean(GestionClases.class);
        gg.crearGrado(GRADO, "Primaria");
        Grado grado = gg.buscarPorNombreYNivel(GRADO, "Primaria");
        ggr.crearGrupo(GRUPO, grado);
        gp.crearPeriodo(PERIODO, "2026-01-01", "2026-12-31", "Activo");
        PeriodoAcademico periodo = gp.buscarPorNombre(PERIODO);
        ga.crearAsignatura(ASIGNATURA);
        gau.crearAula(AULA, 30, "Piso 1", "Teoría", ModalidadAula.PRESENCIAL);
        Docente docente = new Docente(DOCENTE, "hash", "00000001", "Visor", "Doc", 30, "Mat", new ArrayList<>());
        gd.agregar(docente);
        Grupo grupo = ggr.buscarPorNombre(GRUPO);
        Asignatura asig = ga.buscarAsignatura(ASIGNATURA);
        Aula aula = gau.buscarAula(AULA);
        gc.crearClase("Lunes", "08:00", "10:00", asig, docente, grupo, aula, periodo);
        VisorHorarioPage page = new VisorHorarioPage(this);
        page.seleccionarGrupo(GRUPO)
            .seleccionarPeriodo(PERIODO)
            .clickGenerarHorario();
        WaitForAsyncUtils.waitFor(5, TimeUnit.SECONDS,
            () -> !page.getTablaHorario().getItems().isEmpty());
        assertFalse(page.getTablaHorario().getItems().isEmpty());
    }
}

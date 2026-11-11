/*
 * SPDX-FileCopyrightText: 2026 inici4rsesi0n
 * SPDX-License-Identifier: GPL-3.0-or-later
 */
package presentacion.ui;
import aplicacion.casosdeuso.portaldeingreso.administraciondelsistema.academico.*;
import aplicacion.casosdeuso.portaldeingreso.administraciondelsistema.usuarios.GestionDocentes;
import dominio.modelo.*;
import javafx.stage.Stage;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.testfx.util.WaitForAsyncUtils;
import presentacion.BaseUITest;
import presentacion.cleanup.CatalogCleanupHelper;
import presentacion.pages.VisorHorarioPage;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;
import static org.junit.jupiter.api.Assertions.*;
/**
 *
 * @author inici4rsesi0n
 */
public class VisorHorarioUITest extends BaseUITest {
    private static final String PREFIJO = "VISOR-";
    @Override
    public void start(Stage stage) throws Exception {
        loadAndShow(stage, "/fxml/portaldeingreso/administraciondelsistema/gestionacademica/horarios/visor_horario.fxml");
    }
    @BeforeEach
    public void setUp() {
        new CatalogCleanupHelper(springContext, PREFIJO).limpiarTodo();
    }
    @AfterEach
    public void tearDown() {
        new CatalogCleanupHelper(springContext, PREFIJO).limpiarTodo();
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
        gg.crearGrado(PREFIJO + "Grado", "Primaria");
        Grado grado = gg.buscarPorNombreYNivel(PREFIJO + "Grado", "Primaria");
        ggr.crearGrupo(PREFIJO + "Grupo", grado);
        gp.crearPeriodo(PREFIJO + "Periodo", "2026-01-01", "2026-12-31", "Activo");
        PeriodoAcademico periodo = gp.buscarPorNombre(PREFIJO + "Periodo");
        ga.crearAsignatura(PREFIJO + "Asignatura");
        gau.crearAula(PREFIJO + "Aula", 30, "Piso 1", "Teoría", ModalidadAula.PRESENCIAL);
        Docente docente = new Docente(PREFIJO + "DOC", "hash", "00000001", "Visor", "Doc", 30, "Mat", new ArrayList<>());
        gd.agregar(docente);
        Grupo grupo = ggr.buscarPorNombre(PREFIJO + "Grupo");
        Asignatura asig = ga.buscarAsignatura(PREFIJO + "Asignatura");
        Aula aula = gau.buscarAula(PREFIJO + "Aula");
        gc.crearClase("Lunes", "08:00", "10:00", asig, docente, grupo, aula, periodo);
        VisorHorarioPage page = new VisorHorarioPage(this);
        page.seleccionarGrupo(PREFIJO + "Grupo")
            .seleccionarPeriodo(PREFIJO + "Periodo")
            .clickGenerarHorario();
        WaitForAsyncUtils.waitFor(5, TimeUnit.SECONDS,
            () -> !page.getGridHorario().getChildren().isEmpty());
        assertFalse(page.getGridHorario().getChildren().isEmpty());
    }
    @Test
    public void testGenerarHorarioAula() throws Exception {
        GestionGrados gg = springContext.getBean(GestionGrados.class);
        GestionGrupos ggr = springContext.getBean(GestionGrupos.class);
        GestionPeriodos gp = springContext.getBean(GestionPeriodos.class);
        GestionAsignaturas ga = springContext.getBean(GestionAsignaturas.class);
        GestionAulas gau = springContext.getBean(GestionAulas.class);
        GestionDocentes gd = springContext.getBean(GestionDocentes.class);
        GestionClases gc = springContext.getBean(GestionClases.class);
        gg.crearGrado(PREFIJO + "Grado", "Primaria");
        Grado grado = gg.buscarPorNombreYNivel(PREFIJO + "Grado", "Primaria");
        ggr.crearGrupo(PREFIJO + "Grupo", grado);
        gp.crearPeriodo(PREFIJO + "Periodo", "2026-01-01", "2026-12-31", "Activo");
        PeriodoAcademico periodo = gp.buscarPorNombre(PREFIJO + "Periodo");
        ga.crearAsignatura(PREFIJO + "Asignatura");
        gau.crearAula(PREFIJO + "Aula", 30, "Piso 1", "Teoría", ModalidadAula.PRESENCIAL);
        Docente docente = new Docente(PREFIJO + "DOC", "hash", "00000002", "Visor", "Doc", 30, "Mat", new ArrayList<>());
        gd.agregar(docente);
        Grupo grupo = ggr.buscarPorNombre(PREFIJO + "Grupo");
        Asignatura asig = ga.buscarAsignatura(PREFIJO + "Asignatura");
        Aula aula = gau.buscarAula(PREFIJO + "Aula");
        gc.crearClase("Martes", "08:00", "10:00", asig, docente, grupo, aula, periodo);
        VisorHorarioPage page = new VisorHorarioPage(this);
        page.seleccionarAula(PREFIJO + "Aula")
            .seleccionarPeriodoAula(PREFIJO + "Periodo")
            .clickGenerarHorarioAula();
        WaitForAsyncUtils.waitFor(5, TimeUnit.SECONDS,
            () -> !page.getGridHorario().getChildren().isEmpty());
        assertFalse(page.getGridHorario().getChildren().isEmpty());
    }
}

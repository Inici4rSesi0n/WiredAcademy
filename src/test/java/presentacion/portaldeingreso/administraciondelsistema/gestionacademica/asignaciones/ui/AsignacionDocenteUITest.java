/*
 * SPDX-FileCopyrightText: 2026 inici4rsesi0n
 * SPDX-License-Identifier: GPL-3.0-or-later
 */
package presentacion.portaldeingreso.administraciondelsistema.gestionacademica.asignaciones.ui;
import aplicacion.casosdeuso.portaldeingreso.administraciondelsistema.academico.GestionAsignaturas;
import aplicacion.casosdeuso.portaldeingreso.administraciondelsistema.usuarios.GestionDocentes;
import dominio.modelo.Docente;
import javafx.scene.control.TableView;
import javafx.stage.Stage;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.testfx.util.WaitForAsyncUtils;
import presentacion.BaseUITest;
import presentacion.cleanup.CatalogCleanupHelper;
import presentacion.dto.AsignacionItem;
import presentacion.portaldeingreso.administraciondelsistema.gestionacademica.asignaciones.pages.AsignacionDocentePage;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;
/**
 *
 * @author inici4rsesi0n
 */
public class AsignacionDocenteUITest extends BaseUITest {
    private static final String DOC_CODIGO = "UITest-DOC";
    private static final String DOC_DNI = "99990001";
    private static final String ASIG_NOMBRE = "UITest-Asig";
    @Override
    public void start(Stage stage) throws Exception {
        loadAndShow(stage, "/fxml/portaldeingreso/administraciondelsistema/gestionacademica/seccion_gestion_academica.fxml");
    }
    @BeforeEach
    public void setUp() {
        new CatalogCleanupHelper(springContext).limpiarTodo();
        GestionDocentes gd = springContext.getBean(GestionDocentes.class);
        GestionAsignaturas ga = springContext.getBean(GestionAsignaturas.class);
        gd.agregar(new Docente(DOC_CODIGO, "hash", DOC_DNI, "Test", "Docente", 30, "Mat", new ArrayList<>()));
        ga.crearAsignatura(ASIG_NOMBRE);
        WaitForAsyncUtils.waitForFxEvents();
    }
    @AfterEach
    public void tearDown() {
        new CatalogCleanupHelper(springContext).limpiarTodo();
    }
    @Test
    public void testAsignarAsignaturaADocente() throws Exception {
        AsignacionDocentePage page = new AsignacionDocentePage(this);
        page.irACategoriaAsignaciones()
            .clickTabAsigDocente()
            .seleccionarDocente(DOC_CODIGO)
            .seleccionarAsignatura(ASIG_NOMBRE)
            .clickAgregar();
        WaitForAsyncUtils.waitFor(10, TimeUnit.SECONDS, () -> {
            TableView<?> tabla = lookup("#tablaAsignaturasDocente").queryTableView();
            return tabla.getItems().stream()
                .anyMatch(o -> {
                    AsignacionItem item = (AsignacionItem) o;
                    return DOC_CODIGO.equals(item.getCodigoDocente())
                        && ASIG_NOMBRE.equals(item.getNombreAsignatura());
                });
        });
    }
}

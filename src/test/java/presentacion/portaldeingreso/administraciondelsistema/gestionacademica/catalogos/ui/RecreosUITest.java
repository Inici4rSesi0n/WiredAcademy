/*
 * SPDX-FileCopyrightText: 2026 inici4rsesi0n
 * SPDX-License-Identifier: GPL-3.0-or-later
 */
package presentacion.portaldeingreso.administraciondelsistema.gestionacademica.catalogos.ui;
import aplicacion.casosdeuso.portaldeingreso.administraciondelsistema.academico.GestionPeriodos;
import dominio.modelo.Recreo;
import javafx.scene.control.TableView;
import javafx.stage.Stage;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.testfx.util.WaitForAsyncUtils;
import presentacion.BaseUITest;
import presentacion.cleanup.CatalogCleanupHelper;
import presentacion.portaldeingreso.administraciondelsistema.gestionacademica.catalogos.pages.RecreosPage;
import java.util.concurrent.TimeUnit;
/**
 *
 * @author inici4rsesi0n
 */
public class RecreosUITest extends BaseUITest {
    private static final String PERIODO_PREREQ_NOMBRE = "UITest-PeriodoPrereq";
    @Override
    public void start(Stage stage) throws Exception {
        loadAndShow(stage, "/fxml/portaldeingreso/administraciondelsistema/gestionacademica/seccion_gestion_academica.fxml");
    }
    @BeforeEach
    public void setUp() {
        new CatalogCleanupHelper(springContext).limpiarTodo();
        GestionPeriodos gp = springContext.getBean(GestionPeriodos.class);
        gp.crearPeriodo(PERIODO_PREREQ_NOMBRE, "2026-01-01", "2026-12-31", "Activo");
        WaitForAsyncUtils.waitForFxEvents();
    }
    @AfterEach
    public void tearDown() {
        new CatalogCleanupHelper(springContext).limpiarTodo();
    }
    @Test
    public void testCrearRecreo() throws Exception {
        RecreosPage page = new RecreosPage(this);
        page.clickTabRecreos()
            .clickNuevoRecreo()
            .seleccionarDia("Lunes")
            .seleccionarPeriodo(PERIODO_PREREQ_NOMBRE)
            .clickAgregarPeriodo()
            .escribirHoraInicio("10:00")
            .escribirHoraFin("10:30")
            .escribirDescripcion("UITest-Recreo-1")
            .clickGuardar();
        WaitForAsyncUtils.waitFor(10, TimeUnit.SECONDS, () -> {
            TableView<?> tabla = lookup("#tablaRecreos").queryTableView();
            return tabla.getItems().stream()
                .anyMatch(o -> ((Recreo) o).getDescripcion().equals("UITest-Recreo-1"));
        });
    }
}

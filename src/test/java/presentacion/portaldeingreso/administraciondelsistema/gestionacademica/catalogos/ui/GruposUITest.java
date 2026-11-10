/*
 * SPDX-FileCopyrightText: 2026 inici4rsesi0n
 * SPDX-License-Identifier: GPL-3.0-or-later
 */
package presentacion.portaldeingreso.administraciondelsistema.gestionacademica.catalogos.ui;
import aplicacion.casosdeuso.portaldeingreso.administraciondelsistema.academico.GestionGrados;
import dominio.modelo.Grupo;
import javafx.scene.control.TableView;
import javafx.stage.Stage;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.testfx.util.WaitForAsyncUtils;
import presentacion.BaseUITest;
import presentacion.cleanup.CatalogCleanupHelper;
import presentacion.portaldeingreso.administraciondelsistema.gestionacademica.catalogos.pages.GruposPage;
import java.util.concurrent.TimeUnit;
/**
 *
 * @author inici4rsesi0n
 */
public class GruposUITest extends BaseUITest {
    private static final String GRADO_PREREQ_NOMBRE = "UITest-GradoPrereq";
    private static final String GRADO_PREREQ_NIVEL = "Primaria";
    @Override
    public void start(Stage stage) throws Exception {
        loadAndShow(stage, "/fxml/portaldeingreso/administraciondelsistema/gestionacademica/seccion_gestion_academica.fxml");
    }
    @BeforeEach
    public void setUp() {
        new CatalogCleanupHelper(springContext).limpiarTodo();
        GestionGrados gg = springContext.getBean(GestionGrados.class);
        gg.crearGrado(GRADO_PREREQ_NOMBRE, GRADO_PREREQ_NIVEL);
        WaitForAsyncUtils.waitForFxEvents();
    }
    @AfterEach
    public void tearDown() {
        new CatalogCleanupHelper(springContext).limpiarTodo();
    }
    @Test
    public void testCrearGrupo() throws Exception {
        GruposPage page = new GruposPage(this);
        page.clickTabGrupos()
            .clickNuevoGrupo()
            .escribirNombre("UITest-Grupo-1")
            .seleccionarGrado(GRADO_PREREQ_NOMBRE)
            .clickGuardar();
        WaitForAsyncUtils.waitFor(10, TimeUnit.SECONDS, () -> {
            TableView<?> tabla = lookup("#tablaGrupos").queryTableView();
            return tabla.getItems().stream()
                .anyMatch(o -> ((Grupo) o).getNombre().equals("UITest-Grupo-1"));
        });
    }
}

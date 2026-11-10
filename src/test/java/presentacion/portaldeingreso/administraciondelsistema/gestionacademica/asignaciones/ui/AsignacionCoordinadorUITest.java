/*
 * SPDX-FileCopyrightText: 2026 inici4rsesi0n
 * SPDX-License-Identifier: GPL-3.0-or-later
 */
package presentacion.portaldeingreso.administraciondelsistema.gestionacademica.asignaciones.ui;
import aplicacion.casosdeuso.portaldeingreso.administraciondelsistema.academico.GestionGrados;
import aplicacion.casosdeuso.portaldeingreso.administraciondelsistema.usuarios.GestionUsuarios;
import aplicacion.dto.CrearUsuarioCommand;
import dominio.modelo.Usuario;
import javafx.scene.control.TableView;
import javafx.stage.Stage;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.testfx.util.WaitForAsyncUtils;
import presentacion.BaseUITest;
import presentacion.cleanup.CatalogCleanupHelper;
import presentacion.dto.CoordinacionItem;
import presentacion.portaldeingreso.administraciondelsistema.gestionacademica.asignaciones.pages.AsignacionCoordinadorPage;
import java.util.concurrent.TimeUnit;
/**
 *
 * @author inici4rsesi0n
 */
public class AsignacionCoordinadorUITest extends BaseUITest {
    private static final String COORD_CODIGO = "UITest-COORD";
    private static final String COORD_DNI = "99990003";
    private static final String GRADO_NOMBRE = "UITest-GradoCoord";
    @Override
    public void start(Stage stage) throws Exception {
        loadAndShow(stage, "/fxml/portaldeingreso/administraciondelsistema/gestionacademica/seccion_gestion_academica.fxml");
    }
    @BeforeEach
    public void setUp() {
        new CatalogCleanupHelper(springContext).limpiarTodo();
        GestionUsuarios gu = springContext.getBean(GestionUsuarios.class);
        GestionGrados gg = springContext.getBean(GestionGrados.class);
        gu.agregarUsuario(new CrearUsuarioCommand(
            COORD_CODIGO, "hash", COORD_DNI, "Test", "Coord", 40, Usuario.Rol.COORDINADOR, null));
        gg.crearGrado(GRADO_NOMBRE, "Primaria");
        WaitForAsyncUtils.waitForFxEvents();
    }
    @AfterEach
    public void tearDown() {
        new CatalogCleanupHelper(springContext).limpiarTodo();
    }
    @Test
    public void testAsignarCoordinacion() throws Exception {
        AsignacionCoordinadorPage page = new AsignacionCoordinadorPage(this);
        page.irACategoriaAsignaciones()
            .clickTabAsigCoordinador()
            .seleccionarCoordinador(COORD_CODIGO)
            .seleccionarGrado(GRADO_NOMBRE)
            .clickAsignar();
        WaitForAsyncUtils.waitFor(10, TimeUnit.SECONDS, () -> {
            TableView<?> tabla = lookup("#tablaCoordinaciones").queryTableView();
            return tabla.getItems().stream()
                .anyMatch(o -> {
                    CoordinacionItem item = (CoordinacionItem) o;
                    return GRADO_NOMBRE.equals(item.getNombreGrado());
                });
        });
    }
}

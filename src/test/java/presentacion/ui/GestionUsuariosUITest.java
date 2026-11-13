/*
 * SPDX-FileCopyrightText: 2026 inici4rsesi0n
 * SPDX-License-Identifier: GPL-3.0-or-later
 */
package presentacion.ui;
import dominio.modelo.Usuario;
import javafx.scene.control.TableView;
import javafx.stage.Stage;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.testfx.util.WaitForAsyncUtils;
import presentacion.BaseUITest;
import presentacion.cleanup.CatalogCleanupHelper;
import presentacion.pages.GestionUsuariosPage;
import java.util.concurrent.TimeUnit;
/**
 *
 * @author inici4rsesi0n
 */
public class GestionUsuariosUITest extends BaseUITest {
    @Override
    public void start(Stage stage) throws Exception {
        loadAndShow(stage, "/fxml/portaldeingreso/administraciondelsistema/gestiondeusuarios/seccion_gestion_usuarios.fxml");
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
    public void testCrearUsuarioDesdeUI() throws Exception {
        GestionUsuariosPage page = new GestionUsuariosPage(this);
        page.clickNuevoUsuario()
            .seleccionarRol("ADMINISTRADOR")
            .escribirNombre("UITest-Usuario")
            .escribirApellido("UITest-Apellido")
            .escribirDni("99990001")
            .escribirEdad("30")
            .escribirContrasena("passUI123")
            .clickGuardar();
        WaitForAsyncUtils.waitFor(10, TimeUnit.SECONDS, () -> {
            TableView<?> tabla = lookup("#tablaUsuarios").queryTableView();
            return tabla.getItems().stream()
                .anyMatch(o -> ((Usuario) o).getNombre().equals("UITest-Usuario"));
        });
    }
}

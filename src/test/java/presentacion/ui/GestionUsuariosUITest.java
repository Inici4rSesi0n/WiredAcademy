package presentacion.ui;
import aplicacion.casosdeuso.portaldeingreso.administraciondelsistema.usuarios.GestionUsuarios;
import dominio.modelo.Usuario;
import javafx.scene.control.TableView;
import javafx.stage.Stage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.testfx.util.WaitForAsyncUtils;
import presentacion.BaseUITest;
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
    public void cleanUp() {
        GestionUsuarios gu = springContext.getBean(GestionUsuarios.class);
        gu.listarTodos().stream()
                .filter(u -> u.getNombre().startsWith("UITest"))
                .forEach(gu::eliminarUsuario);
    }
    @Test
    public void testCrearUsuarioDesdeUI() throws Exception {
        GestionUsuariosPage page = new GestionUsuariosPage(this);
        page.clickNuevoUsuario()
            .seleccionarRol("ADMINISTRADOR")
            .escribirNombre("UITestNombre")
            .escribirApellido("UITestApellido")
            .escribirDni("99000001")
            .escribirEdad("30")
            .escribirContrasena("passUI123")
            .clickGuardar();
        WaitForAsyncUtils.waitFor(10, TimeUnit.SECONDS, () -> {
            TableView<?> tabla = lookup("#tablaUsuarios").queryTableView();
            return tabla.getItems().stream()
                .anyMatch(o -> ((Usuario) o).getNombre().equals("UITestNombre"));
        });
    }
}

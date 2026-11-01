package presentacion.ui;
import aplicacion.casosdeuso.portaldeingreso.administraciondelsistema.usuarios.GestionUsuarios;
import aplicacion.dto.CrearUsuarioCommand;
import dominio.modelo.Usuario;
import dominio.puerto.externo.HashProvider;
import javafx.stage.Stage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.testfx.util.WaitForAsyncUtils;
import presentacion.BaseUITest;
import presentacion.controlador.LoginController;
import presentacion.pages.LoginPage;
import java.util.concurrent.TimeUnit;
import static org.junit.jupiter.api.Assertions.*;
/**
 *
 * @author inici4rsesi0n
 */
public class LoginUITest extends BaseUITest {
    @Override
    public void start(Stage stage) throws Exception {
        loadAndShow(stage, "/fxml/login.fxml");
    }
    @BeforeEach
    public void cleanUp() {
        GestionUsuarios gu = springContext.getBean(GestionUsuarios.class);
        gu.listarTodos().stream()
                .filter(u -> u.getCodigo().startsWith("IT-"))
                .forEach(gu::eliminarUsuario);
    }
    @Test
    public void testLoginExitoso() throws Exception {
        String password = "secretIT";
        String hash = springContext.getBean(HashProvider.class).generarHash(password.toCharArray());
        springContext.getBean(GestionUsuarios.class).agregarUsuario(
                new CrearUsuarioCommand("IT-LOGIN-001", hash, "90000001", "Login", "IT", 30, Usuario.Rol.ADMINISTRADOR, null));
        LoginPage page = new LoginPage(this);
        page.escribirCodigo("IT-LOGIN-001").escribirPassword(password).clickLogin();
        LoginController controller = springContext.getBean(LoginController.class);
        WaitForAsyncUtils.waitFor(5, TimeUnit.SECONDS, () -> true);
    }
}

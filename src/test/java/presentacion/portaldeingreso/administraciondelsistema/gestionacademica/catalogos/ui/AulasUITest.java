package presentacion.portaldeingreso.administraciondelsistema.gestionacademica.catalogos.ui;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.junit.jupiter.api.Test;
import org.testfx.matcher.control.TableViewMatchers;
import static org.testfx.api.FxAssert.verifyThat;
import presentacion.BaseUITest;
import presentacion.portaldeingreso.administraciondelsistema.gestionacademica.catalogos.pages.AulasPage;
/**
 *
 * @author inici4rsesi0n
 */
public class AulasUITest extends BaseUITest {
    @Override
    public void start(Stage stage) throws Exception {
        Parent root = fxmlLoader.cargar("/fxml/portaldeingreso/administraciondelsistema/gestionacademica/seccion_gestion_academica.fxml");
        Scene scene = new Scene(root);
        scene.getStylesheets().add(getClass().getResource("/css/app.css").toExternalForm());
        stage.setScene(scene);
        stage.show();
    }
    @Test
    public void testCrearAula() {
        AulasPage page = new AulasPage(this);
        page.clickTabAulas()
            .clickNuevaAula()
            .escribirNombre("Aula 999")
            .clickGuardar();
        verifyThat("#tablaAulas", TableViewMatchers.hasNumRows(1));
    }
}

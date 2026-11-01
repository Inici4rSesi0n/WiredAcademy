package presentacion;
import boot.WiredAcademyApp;
import infraestructura.configuracion.SpringFxmlLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ConfigurableApplicationContext;
import org.testfx.framework.junit5.ApplicationTest;
/**
 *
 * @author inici4rsesi0n
 */
public abstract class BaseUITest extends ApplicationTest {
    protected static final int SCENE_WIDTH = 1280;
    protected static final int SCENE_HEIGHT = 800;
    protected ConfigurableApplicationContext springContext;
    protected SpringFxmlLoader fxmlLoader;
    @Override
    public void init() {
        springContext = new SpringApplicationBuilder(WiredAcademyApp.class)
                .properties("spring.main.web-application-type=none")
                .profiles("test")
                .run();
        fxmlLoader = springContext.getBean(SpringFxmlLoader.class);
    }
    @Override
    public void start(Stage stage) throws Exception {
        loadAndShow(stage, "/fxml/main.fxml");
    }
    protected void loadAndShow(Stage stage, String fxmlPath) throws Exception {
        Parent root = fxmlLoader.cargar(fxmlPath);
        Scene scene = new Scene(root, SCENE_WIDTH, SCENE_HEIGHT);
        scene.getStylesheets().add(getClass().getResource("/css/app.css").toExternalForm());
        stage.setScene(scene);
        stage.setWidth(SCENE_WIDTH);
        stage.setHeight(SCENE_HEIGHT);
        stage.show();
    }
    @Override
    public void stop() {
        if (springContext != null) springContext.close();
    }
}

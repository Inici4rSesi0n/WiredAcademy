/*
 * SPDX-FileCopyrightText: 2026 inici4rsesi0n
 * SPDX-License-Identifier: GPL-3.0-or-later
 */
package boot;
import infraestructura.configuracion.SpringFxmlLoader;
import javafx.application.Application;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
/**
 *
 * @author inici4rsesi0n
 */
@SpringBootApplication(scanBasePackages = {"infraestructura", "aplicacion", "presentacion", "boot"})
@EntityScan(basePackages = "infraestructura.persistencia.entidades")
@EnableJpaRepositories(basePackages = "infraestructura.persistencia.jpa")
public class WiredAcademyApp extends Application {
    private static ConfigurableApplicationContext contextoSpring;
    @Override
    public void init() {
        contextoSpring = SpringApplication.run(WiredAcademyApp.class);
    }
    @Override
    public void start(Stage stage) throws Exception {
        if ("true".equalsIgnoreCase(System.getenv("WIRED_SEED"))) {
            contextoSpring.getBean(boot.DataSeeder.class).run();
            contextoSpring.close();
            System.exit(0);
        }

        contextoSpring.getBean(BootstrapRunner.class).inicializar(stage);
        SpringFxmlLoader fxmlLoader = contextoSpring.getBean(SpringFxmlLoader.class);
        Parent root = fxmlLoader.cargar("/fxml/main.fxml");
        Scene scene = new Scene(root, 1200, 800);
        scene.getStylesheets().add(getClass().getResource("/css/app.css").toExternalForm());
        stage.setTitle("WiredAcademy · Ecosistema Educativo Digital");
        stage.setScene(scene);
        stage.show();
    }
    @Override
    public void stop() {
        contextoSpring.close();
    }
    public static void main(String[] args) {
        launch(args);
    }
}

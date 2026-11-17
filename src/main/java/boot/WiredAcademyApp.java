/*
 * SPDX-FileCopyrightText: 2026 inici4rsesi0n
 * SPDX-License-Identifier: GPL-3.0-or-later
 */
package boot;
import aplicacion.servicio.BuscadorUsuario;
import dominio.modelo.Usuario;
import dominio.puerto.externo.LoggerPort;
import infraestructura.configuracion.SpringFxmlLoader;
import presentacion.controlador.portaldeingreso.DashboardController;
import presentacion.estadousuario.PersistenciaSesion;
import presentacion.estadousuario.SesionUsuario;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import java.util.Optional;
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
        contextoSpring.getBean(BootstrapRunner.class).inicializar(stage);
        SpringFxmlLoader fxmlLoader = contextoSpring.getBean(SpringFxmlLoader.class);
        LoggerPort logger = contextoSpring.getBean(LoggerPort.class);
        Usuario usuarioPersistido = intentarAutoLogin(logger);
        if (usuarioPersistido != null) {
            FXMLLoader loader = fxmlLoader.crearLoader("/fxml/portaldeingreso/dashboard.fxml");
            Parent root = loader.load();
            DashboardController dashboardController = loader.getController();
            dashboardController.setUsuario(usuarioPersistido);
            Scene scene = new Scene(root, 1200, 800);
            scene.getStylesheets().add(getClass().getResource("/css/app.css").toExternalForm());
            stage.setTitle("WiredAcademy · Ecosistema Educativo Digital");
            stage.setScene(scene);
            stage.setOnCloseRequest(event -> javafx.application.Platform.exit());
            stage.setOnHidden(event -> navegarAMain(stage, fxmlLoader, logger));
            stage.show();
        } else {
            Parent root = fxmlLoader.cargar("/fxml/main.fxml");
            Scene scene = new Scene(root, 1200, 800);
            scene.getStylesheets().add(getClass().getResource("/css/app.css").toExternalForm());
            stage.setTitle("WiredAcademy · Ecosistema Educativo Digital");
            stage.setScene(scene);
            stage.show();
        }
    }
    private void navegarAMain(Stage dashboardStage, SpringFxmlLoader fxmlLoader, LoggerPort logger) {
        if (!PersistenciaSesion.leer().isPresent()) {
            Usuario activo = SesionUsuario.getUsuarioActual();
            if (activo != null) {
                logger.audit("[SESSION] logout_auto motivo=volver_a_main_sin_persistencia codigo={}", activo.getCodigo());
                SesionUsuario.cerrarSesion();
                logger.setUsuario(null);
            }
        }
        try {
            FXMLLoader mainLoader = fxmlLoader.crearLoader("/fxml/main.fxml");
            Parent mainRoot = mainLoader.load();
            Stage mainStage = new Stage(StageStyle.DECORATED);
            Scene mainScene = new Scene(mainRoot);
            mainScene.getStylesheets().add(getClass().getResource("/css/app.css").toExternalForm());
            mainStage.setScene(mainScene);
            mainStage.setTitle("WiredAcademy · Ecosistema Educativo Digital");
            mainStage.setX(dashboardStage.getX());
            mainStage.setY(dashboardStage.getY());
            mainStage.setWidth(dashboardStage.getWidth());
            mainStage.setHeight(dashboardStage.getHeight());
            mainStage.setMaximized(dashboardStage.isMaximized());
            mainStage.show();
        } catch (Exception e) {
            logger.error("Error al navegar a main desde autologin: " + e.getMessage(), e);
        }
    }
    private Usuario intentarAutoLogin(LoggerPort logger) {
        Optional<String> codigoOpt = PersistenciaSesion.leer();
        if (codigoOpt.isEmpty()) {
            logger.audit("[SESSION] autologin_fallido motivo=sin_archivo");
            return null;
        }
        String codigo = codigoOpt.get();
        logger.audit("[SESSION] autologin_intento codigo={}", codigo);
        Usuario usuario = contextoSpring.getBean(BuscadorUsuario.class).buscarPorCodigo(codigo);
        if (usuario == null) {
            PersistenciaSesion.borrar();
            logger.audit("[SESSION] autologin_fallido codigo={} motivo=usuario_inexistente archivo_borrado=true", codigo);
            return null;
        }
        SesionUsuario.iniciarSesion(usuario);
        logger.setUsuario(codigo);
        logger.audit("[SESSION] autologin_exitoso codigo={} rol={}", codigo, usuario.getRol());
        return usuario;
    }
    @Override
    public void stop() {
        if (contextoSpring != null) {
            contextoSpring.close();
        }
    }
    public static void main(String[] args) {
        launch(args);
    }
}

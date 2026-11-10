/*
 * SPDX-FileCopyrightText: 2026 inici4rsesi0n
 * SPDX-License-Identifier: GPL-3.0-or-later
 */
package boot;
import aplicacion.casosdeuso.portaldeingreso.sistema.BootstrapUseCase;
import dominio.puerto.repositorio.RepositorioAdministradores;
import infraestructura.configuracion.SpringFxmlLoader;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Modality;
import javafx.stage.Stage;
import presentacion.controlador.BootstrapController;
import org.springframework.stereotype.Component;
/**
 *
 * @author inici4rsesi0n
 */
@Component
public class BootstrapRunner {
    private final BootstrapUseCase bootstrapUseCase;
    private final SpringFxmlLoader fxmlLoader;
    private final RepositorioAdministradores repoAdmin;
    public BootstrapRunner(BootstrapUseCase bootstrapUseCase, SpringFxmlLoader fxmlLoader, RepositorioAdministradores repoAdmin) {
        this.bootstrapUseCase = bootstrapUseCase;
        this.fxmlLoader = fxmlLoader;
        this.repoAdmin = repoAdmin;
    }
    public void inicializar(Stage stage) {
        if (repoAdmin.listarTodos().isEmpty()) {
            try {
                FXMLLoader loader = fxmlLoader.crearLoader("/fxml/configuracion_inicial.fxml");
                Parent root = loader.load();
                BootstrapController controller = loader.getController();
                Stage configStage = new Stage();
                configStage.setTitle("Configuración Inicial del Sistema");
                Scene scene = new Scene(root);
                scene.getStylesheets().add(getClass().getResource("/css/app.css").toExternalForm());
                configStage.setScene(scene);
                configStage.initModality(Modality.APPLICATION_MODAL);
                configStage.initOwner(stage);
                configStage.showAndWait();
                if (controller.isConfirmado()) {
                    bootstrapUseCase.inicializarSistema(controller);
                } else {
                    javafx.application.Platform.exit(); // Si cancela, se cierra la app
                }
            } catch (Exception e) {
                throw new RuntimeException("No se pudo cargar la ventana de configuración inicial.", e);
            }
        } else {
            // Si ya hay admin, solo aseguramos que los catálogos existan
            bootstrapUseCase.inicializarCatalogos();
        }
    }
}

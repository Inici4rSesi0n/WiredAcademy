/*
 * SPDX-FileCopyrightText: 2026 inici4rsesi0n
 * SPDX-License-Identifier: GPL-3.0-or-later
 */
package presentacion.controlador;
import presentacion.controlador.portaldeingreso.DashboardController;
import aplicacion.casosdeuso.portaldeingreso.sistema.GestionPermisos;
import dominio.puerto.externo.LoggerPort;
import dominio.modelo.Usuario;
import infraestructura.configuracion.SpringFxmlLoader;
import presentacion.dialogos.Dialogos;
import presentacion.estadousuario.SesionUsuario;
import java.net.URL;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.concurrent.ConcurrentHashMap;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
/**
 *
 * @author inici4rsesi0n
 */
@Component
@Scope("prototype")
public class MainController implements Initializable {
    private final GestionPermisos gestionPermisos;
    private final SpringFxmlLoader fxmlLoader;
    private final LoggerPort logger;
    @FXML private Button btnPortalIngreso;
    @FXML private Button btnPortalTramites;
    @FXML private Button btnPortalMatricula;
    @FXML private HBox panelSesion;
    @FXML private Label lblUsuarioSesion;
    @FXML private Button btnCerrarSesion;
    private final Map<String, String> rutasVistas = new ConcurrentHashMap<>();
    public MainController(GestionPermisos gestionPermisos, SpringFxmlLoader fxmlLoader, LoggerPort logger) {
        this.gestionPermisos = gestionPermisos;
        this.fxmlLoader = fxmlLoader;
        this.logger = logger;
    }
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        precargarRutas();
        actualizarPanelSesion();
    }
    private void actualizarPanelSesion() {
        boolean activa = SesionUsuario.isSesionActiva();
        panelSesion.setVisible(activa);
        panelSesion.setManaged(activa);
        if (activa) {
            Usuario u = SesionUsuario.getUsuarioActual();
            lblUsuarioSesion.setText(u.getNombre() + " " + u.getApellido());
        }
    }
    private void precargarRutas() {
        rutasVistas.put("main", "/fxml/main.fxml");
        rutasVistas.put("login", "/fxml/login.fxml");
        rutasVistas.put("dashboard", "/fxml/portaldeingreso/dashboard.fxml");
    }
    @FXML private void handlePortalIngreso() { navegar("INGRESO", btnPortalIngreso); }
    @FXML private void handlePortalTramites() { navegar("TRAMITES", btnPortalTramites); }
    @FXML private void handlePortalMatricula() { navegar("MATRICULA", btnPortalMatricula); }
    @FXML
    private void handleCerrarSesion() {
        SesionUsuario.cerrarSesion();
        logger.setUsuario(null);
        Stage stage = (Stage) btnCerrarSesion.getScene().getWindow();
        navegarAVista("main", stage, null);
    }
    private void navegar(String modo, Button sourceButton) {
        Stage mainStage = (Stage) sourceButton.getScene().getWindow();
        if (SesionUsuario.isSesionActiva()) {
            Usuario usuario = SesionUsuario.getUsuarioActual();
            if (gestionPermisos.puedeAccederPortal(usuario.getRol(), modo)) {
                navegarAVista("dashboard", mainStage, usuario);
            } else {
                Dialogos.info("No tiene permisos para acceder a este portal.");
            }
        } else {
            abrirLogin(modo, mainStage);
        }
    }
    private void abrirLogin(String modo, Stage mainStage) {
        String ruta = rutasVistas.get("login");
        if (ruta == null) return;
        try {
            FXMLLoader loader = fxmlLoader.crearLoader(ruta);
            Parent root = loader.load();
            LoginController controller = loader.getController();
            Stage loginStage = new Stage(StageStyle.DECORATED);
            loginStage.setTitle("Autenticación - WiredAcademy");
            Scene scene = new Scene(root);
            scene.getStylesheets().add(getClass().getResource("/css/app.css").toExternalForm());
            loginStage.setScene(scene);
            loginStage.setOnCloseRequest(event -> Platform.exit());
            controller.limpiarCampos();
            controller.setModo(modo);
            loginStage.setX(mainStage.getX());
            loginStage.setY(mainStage.getY());
            loginStage.setWidth(mainStage.getWidth());
            loginStage.setHeight(mainStage.getHeight());
            loginStage.setMaximized(mainStage.isMaximized());
            mainStage.hide();
            loginStage.showAndWait();
            if (controller.isLoginExitoso()) {
                mainStage.close();
                navegarAVista("dashboard", loginStage, controller.getUsuarioAutenticado());
            } else {
                mainStage.setX(loginStage.getX());
                mainStage.setY(loginStage.getY());
                mainStage.setWidth(loginStage.getWidth());
                mainStage.setHeight(loginStage.getHeight());
                mainStage.setMaximized(loginStage.isMaximized());
                mainStage.show();
            }
        } catch (Exception e) {
            e.printStackTrace();
            Dialogos.info("Error al abrir Login: " + e.getMessage());
            if (!mainStage.isShowing()) mainStage.show();
        }
    }
    private void navegarAVista(String vistaKey, Stage ventanaActual, Usuario usuario) {
        String ruta = rutasVistas.get(vistaKey);
        if (ruta == null) return;
        try {
            FXMLLoader loader = fxmlLoader.crearLoader(ruta);
            Parent root = loader.load();
            Object controller = loader.getController();
            Stage nuevaVentana = new Stage(StageStyle.DECORATED);
            Scene scene = new Scene(root);
            scene.getStylesheets().add(getClass().getResource("/css/app.css").toExternalForm());
            nuevaVentana.setScene(scene);
            nuevaVentana.setX(ventanaActual.getX());
            nuevaVentana.setY(ventanaActual.getY());
            nuevaVentana.setWidth(ventanaActual.getWidth());
            nuevaVentana.setHeight(ventanaActual.getHeight());
            if ("dashboard".equals(vistaKey)) {
                nuevaVentana.setTitle("WiredAcademy · Ecosistema Educativo Digital");
                DashboardController dashboardController = (DashboardController) controller;
                dashboardController.setUsuario(usuario);
                nuevaVentana.setOnCloseRequest(event -> Platform.exit());
                nuevaVentana.setOnHidden(event -> navegarAVista("main", nuevaVentana, null));
            } else if ("main".equals(vistaKey)) {
                nuevaVentana.setTitle("WiredAcademy · Ecosistema Educativo Digital");
            }
            ventanaActual.close();
            nuevaVentana.show();
            if (ventanaActual.isMaximized()) {
                nuevaVentana.setMaximized(true);
            }
        } catch (Exception e) {
            e.printStackTrace();
            Dialogos.info("Error al cargar la vista: " + e.getMessage());
        }
    }
}

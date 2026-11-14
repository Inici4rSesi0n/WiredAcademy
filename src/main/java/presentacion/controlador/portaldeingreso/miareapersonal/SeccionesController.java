/*
 * SPDX-FileCopyrightText: 2026 inici4rsesi0n
 * SPDX-License-Identifier: GPL-3.0-or-later
 */
package presentacion.controlador.portaldeingreso.miareapersonal;
import aplicacion.casosdeuso.portaldeingreso.administraciondelsistema.academico.GestionClases;
import dominio.modelo.Asignatura;
import dominio.modelo.Clase;
import dominio.modelo.Grupo;
import dominio.modelo.Usuario;
import dominio.puerto.externo.LoggerPort;
import infraestructura.configuracion.SpringFxmlLoader;
import presentacion.estadousuario.SesionUsuario;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.layout.*;
import javafx.scene.shape.SVGPath;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import java.util.List;
import java.util.stream.Collectors;
@Component
@Scope("prototype")
public class SeccionesController {
    private final GestionClases gestionClases;
    private final SpringFxmlLoader fxmlLoader;
    private final LoggerPort logger;
    @FXML private Label lblTituloAsignatura;
    @FXML private FlowPane flowPaneSecciones;
    private Asignatura asignaturaActual;
    public SeccionesController(GestionClases gestionClases, SpringFxmlLoader fxmlLoader, LoggerPort logger) {
        this.gestionClases = gestionClases;
        this.fxmlLoader = fxmlLoader;
        this.logger = logger;
    }
    public void setAsignatura(Asignatura asignatura) {
        this.asignaturaActual = asignatura;
        lblTituloAsignatura.setText("Secciones - " + asignatura.getNombre());
        cargarSecciones();
    }
    private void cargarSecciones() {
        flowPaneSecciones.getChildren().clear();
        Usuario usuario = SesionUsuario.getUsuarioActual();
        if (usuario == null) {
            logger.warn("op=READ entity=SECCION | error=usuario_nulo");
            return;
        }
        logger.info("op=READ entity=SECCION | inicio_carga asignatura=" + asignaturaActual.getNombre() + " docente=" + usuario.getCodigo());
        List<Clase> clases = gestionClases.buscarPorDocente(usuario.getCodigo(), "2026-I");
        List<Grupo> secciones = clases.stream()
                .filter(c -> c.getAsignatura().equals(asignaturaActual))
                .map(Clase::getGrupo)
                .distinct()
                .toList();
        logger.info("op=READ entity=SECCION | fin_carga secciones_encontradas=" + secciones.size());
        if (secciones.isEmpty()) {
            flowPaneSecciones.getChildren().add(new Label("No tienes secciones asignadas para esta asignatura."));
            return;
        }
        for (Grupo g : secciones) {
            flowPaneSecciones.getChildren().add(crearTarjetaSeccion(g));
        }
    }
    private VBox crearTarjetaSeccion(Grupo grupo) {
        VBox card = new VBox(10);
        card.setPrefWidth(300);
        card.setStyle("-fx-background-color: rgba(22, 26, 41, 0.8); -fx-background-radius: 12px; -fx-border-color: rgba(0, 210, 255, 0.2); -fx-border-radius: 12px; -fx-border-width: 1px; -fx-padding: 20px; -fx-cursor: hand; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.4), 15, 0, 0, 5);");
        HBox header = new HBox(15);
        header.setAlignment(Pos.CENTER_LEFT);
        SVGPath icon = new SVGPath();
        icon.setContent("M16 11c1.66 0 2.99-1.34 2.99-3S17.66 5 16 5c-1.66 0-3 1.34-3 3s1.34 3 3 3zm-8 0c1.66 0 2.99-1.34 2.99-3S9.66 5 8 5C6.34 5 5 6.34 5 8s1.34 3 3 3zm0 2c-2.33 0-7 1.17-7 3.5V19h14v-2.5c0-2.33-4.67-3.5-7-3.5zm8 0c-.29 0-.62.02-.97.05 1.16.84 1.97 1.97 1.97 3.45V19h6v-2.5c0-2.33-4.67-3.5-7-3.5z");
        icon.setStyle("-fx-fill: #00D2FF; -fx-scale-x: 1.2; -fx-scale-y: 1.2;");
        Label lblNombre = new Label(grupo.getNombre());
        lblNombre.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: #F8FAFC;");
        header.getChildren().addAll(icon, lblNombre);
        Label lblDetalle = new Label("Grado: " + (grupo.getGrado() != null ? grupo.getGrado().getNombre() : "S/G") + "\nModalidad: " + grupo.getModalidad());
        lblDetalle.setStyle("-fx-text-fill: #94A3B8; -fx-font-size: 13px; -fx-line-spacing: 3px;");
        card.getChildren().addAll(header, lblDetalle);
        card.setOnMouseClicked(e -> abrirAulaVirtual(grupo));
        return card;
    }
    private void abrirAulaVirtual(Grupo grupo) {
        try {
            logger.debug("op=NAVIGATE entity=SECCION | destino=aula_virtual seccion=" + grupo.getNombre());
            FXMLLoader loader = fxmlLoader.crearLoader("/fxml/portaldeingreso/miareapersonal/curso_detalle.fxml");
            Parent root = loader.load();
            CursoDetalleController controller = loader.getController();
            controller.setAsignatura(asignaturaActual);
            BorderPane mainRoot = (BorderPane) flowPaneSecciones.getScene().getRoot();
            mainRoot.setCenter(root);
        } catch (Exception e) {
            logger.error("Error al abrir aula virtual: " + e.getMessage(), e);
            e.printStackTrace();
        }
    }
    @FXML
    private void handleVolver() {
        try {
            FXMLLoader loader = fxmlLoader.crearLoader("/fxml/portaldeingreso/miareapersonal/mis_cursos.fxml");
            Parent root = loader.load();
            BorderPane mainRoot = (BorderPane) flowPaneSecciones.getScene().getRoot();
            mainRoot.setCenter(root);
        } catch (Exception e) {
            logger.error("Error al volver a mis cursos: " + e.getMessage(), e);
        }
    }
}

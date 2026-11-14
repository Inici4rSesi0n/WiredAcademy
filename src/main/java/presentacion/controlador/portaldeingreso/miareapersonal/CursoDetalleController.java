/*
 * SPDX-FileCopyrightText: 2026 inici4rsesi0n
 * SPDX-License-Identifier: GPL-3.0-or-later
 */
package presentacion.controlador.portaldeingreso.miareapersonal;
import dominio.modelo.Asignatura;
import dominio.modelo.TipoPublicacion;
import dominio.modelo.Usuario;
import infraestructura.configuracion.SpringFxmlLoader;
import presentacion.estadousuario.SesionUsuario;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.layout.BorderPane;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
@Component
@Scope("prototype")
public class CursoDetalleController {
    private final SpringFxmlLoader fxmlLoader;
    @FXML private Button btnVolver;
    @FXML private Label lblTituloCurso;
    @FXML private TabPane tabPaneCurso;
    @FXML private Tab tabAsistencia;
    @FXML private PublicacionesPanelController includeContenidoController;
    @FXML private PublicacionesPanelController includeTareasController;
    @FXML private PublicacionesPanelController includeEvaluacionesController;
    @FXML private PublicacionesPanelController includeForosController;
    @FXML private PublicacionesPanelController includeAnunciosController;
    @FXML private CalificacionesController includeCalificacionesController;
    private Asignatura asignaturaActual;
    public CursoDetalleController(SpringFxmlLoader fxmlLoader) {
        this.fxmlLoader = fxmlLoader;
    }
    public void setAsignatura(Asignatura asignatura) {
        this.asignaturaActual = asignatura;
        lblTituloCurso.setText("Aula Virtual - " + asignatura.getNombre());
        Usuario usuario = SesionUsuario.getUsuarioActual();
        if (usuario != null && usuario.getRol() == Usuario.Rol.DOCENTE) {
            btnVolver.setText("← Volver a Mis Secciones");
        } else {
            btnVolver.setText("← Volver a Mis Cursos");
        }
        includeContenidoController.configurar(asignatura, TipoPublicacion.MATERIAL);
        includeTareasController.configurar(asignatura, TipoPublicacion.TAREA);
        includeEvaluacionesController.configurar(asignatura, TipoPublicacion.EVALUACION);
        includeForosController.configurar(asignatura, TipoPublicacion.FORO);
        includeAnunciosController.configurar(asignatura, TipoPublicacion.ANUNCIO);
        includeCalificacionesController.setAsignatura(asignatura);
        if (usuario != null && usuario.getRol() != Usuario.Rol.DOCENTE) {
            tabPaneCurso.getTabs().remove(tabAsistencia);
        }
    }
    @FXML
    private void handleVolver() {
        Usuario usuario = SesionUsuario.getUsuarioActual();
        boolean esDocente = usuario != null && usuario.getRol() == Usuario.Rol.DOCENTE;
        String ruta = esDocente ? "/fxml/portaldeingreso/miareapersonal/secciones.fxml" : "/fxml/portaldeingreso/miareapersonal/mis_cursos.fxml";
        try {
            FXMLLoader loader = fxmlLoader.crearLoader(ruta);
            Parent root = loader.load();
            if (esDocente) {
                SeccionesController controller = loader.getController();
                controller.setAsignatura(asignaturaActual);
            }
            BorderPane mainRoot = (BorderPane) tabPaneCurso.getScene().getRoot();
            mainRoot.setCenter(root);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

/*
 * SPDX-FileCopyrightText: 2026 inici4rsesi0n
 * SPDX-License-Identifier: GPL-3.0-or-later
 */
package presentacion.controlador.portaldeingreso.miareapersonal;
import dominio.modelo.Asignatura;
import dominio.modelo.Usuario;
import infraestructura.configuracion.SpringFxmlLoader;
import presentacion.estadousuario.SesionUsuario;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
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
    @FXML private Label lblTituloCurso, lblInfoCurso;
    @FXML private TabPane tabPaneCurso;
    @FXML private Tab tabAsistencia;
    @FXML private CalificacionesController includeCalificacionesController;
    private Asignatura asignaturaActual;
    public CursoDetalleController(SpringFxmlLoader fxmlLoader) {
        this.fxmlLoader = fxmlLoader;
    }
    public void setAsignatura(Asignatura asignatura) {
        this.asignaturaActual = asignatura;
        lblTituloCurso.setText(asignatura.getNombre());
        Usuario usuario = SesionUsuario.getUsuarioActual();
        lblInfoCurso.setText("Curso: " + asignatura.getNombre() + "\nUsuario: " + usuario.getNombre() + " " + usuario.getApellido() + "\nRol: " + usuario.getRol());
        includeCalificacionesController.setAsignatura(asignatura);
        if (usuario.getRol() != Usuario.Rol.DOCENTE) {
            tabPaneCurso.getTabs().remove(tabAsistencia);
        }
    }
    @FXML
    private void handleVolver() {
        try {
            FXMLLoader loader = fxmlLoader.crearLoader("/fxml/portaldeingreso/miareapersonal/mis_cursos.fxml");
            Parent root = loader.load();
            BorderPane mainRoot = (BorderPane) tabPaneCurso.getScene().getRoot();
            mainRoot.setCenter(root);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

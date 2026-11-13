/*
 * SPDX-FileCopyrightText: 2026 inici4rsesi0n
 * SPDX-License-Identifier: GPL-3.0-or-later
 */
package presentacion.controlador.portaldeingreso.miareapersonal;
import aplicacion.casosdeuso.portaldeingreso.miareapersonal.GestionCalificaciones;
import aplicacion.casosdeuso.portaldeingreso.miareapersonal.GestionEvaluaciones;
import dominio.modelo.*;
import infraestructura.configuracion.SpringFxmlLoader;
import presentacion.estadousuario.SesionUsuario;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import java.util.List;
@Component
@Scope("prototype")
public class CalificacionesController {
    private final GestionEvaluaciones gestionEvaluaciones;
    private final GestionCalificaciones gestionCalificaciones;
    private final SpringFxmlLoader fxmlLoader;
    @FXML private Label lblTituloCurso, lblPromedio;
    @FXML private TableView<Object[]> tablaNotas;
    @FXML private TableColumn<Object[], String> colEvaluacion;
    @FXML private TableColumn<Object[], Number> colPeso;
    @FXML private TableColumn<Object[], Number> colNota;
    @FXML private Button btnNuevaEvaluacion;
    private Asignatura asignaturaActual;
    private Usuario usuarioActual;
    public CalificacionesController(GestionEvaluaciones gestionEvaluaciones, GestionCalificaciones gestionCalificaciones, SpringFxmlLoader fxmlLoader) {
        this.gestionEvaluaciones = gestionEvaluaciones;
        this.gestionCalificaciones = gestionCalificaciones;
        this.fxmlLoader = fxmlLoader;
    }
    public void setAsignatura(Asignatura asignatura) {
        this.asignaturaActual = asignatura;
        this.usuarioActual = SesionUsuario.getUsuarioActual();
        lblTituloCurso.setText("Calificaciones - " + asignatura.getNombre());
        configurarTabla();
        cargarDatos();
    }
    private void configurarTabla() {
        colEvaluacion.setCellValueFactory(cell -> new SimpleStringProperty((String) cell.getValue()[0]));
        colPeso.setCellValueFactory(cell -> new SimpleDoubleProperty((Double) cell.getValue()[1]));
        colNota.setCellValueFactory(cell -> {
            Double nota = (Double) cell.getValue()[2];
            return new SimpleDoubleProperty(nota != null ? nota : 0.0);
        });
        if (usuarioActual != null && usuarioActual.getRol() == Usuario.Rol.DOCENTE) {
            btnNuevaEvaluacion.setVisible(true);
            btnNuevaEvaluacion.setManaged(true);
        }
    }
    private void cargarDatos() {
        List<Evaluacion> evaluaciones = gestionEvaluaciones.listarPorAsignatura(asignaturaActual.getNombre());
        tablaNotas.getItems().clear();
        if (usuarioActual.getRol() == Usuario.Rol.ESTUDIANTE) {
            for (Evaluacion ev : evaluaciones) {
                Double nota = gestionCalificaciones.listarPorEstudiante(usuarioActual.getCodigo()).stream()
                        .filter(c -> c.getEvaluacion().getId().equals(ev.getId()))
                        .map(Calificacion::getNota).findFirst().orElse(null);
                tablaNotas.getItems().add(new Object[]{ev.getNombre(), ev.getPeso() * 100, nota});
            }
            double pp = gestionCalificaciones.calcularPromedioPonderado(usuarioActual.getCodigo(), evaluaciones);
            lblPromedio.setText(String.format("%.2f", pp));
        } else {
            for (Evaluacion ev : evaluaciones) {
                tablaNotas.getItems().add(new Object[]{ev.getNombre(), ev.getPeso() * 100, null});
            }
        }
    }
    @FXML private void handleNuevaEvaluacion() {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Nueva Evaluación");
        dialog.setHeaderText("Crear evaluación para " + asignaturaActual.getNombre());
        dialog.setContentText("Nombre (ej. PC1):");
        dialog.showAndWait().ifPresent(nombre -> {
            TextInputDialog pesoDialog = new TextInputDialog("0.2");
            pesoDialog.setTitle("Peso");
            pesoDialog.setHeaderText("Ingrese el peso (0.1 a 1.0):");
            pesoDialog.showAndWait().ifPresent(pesoStr -> {
                try {
                    double peso = Double.parseDouble(pesoStr);
                    gestionEvaluaciones.crearEvaluacion(nombre, peso, asignaturaActual);
                    cargarDatos();
                } catch (Exception e) {
                    Alert alert = new Alert(Alert.AlertType.ERROR, e.getMessage());
                    alert.show();
                }
            });
        });
    }
    @FXML private void handleVolver() {
        try {
            FXMLLoader loader = fxmlLoader.crearLoader("/fxml/portaldeingreso/miareapersonal/mis_cursos.fxml");
            Parent root = loader.load();
            BorderPane mainRoot = (BorderPane) tablaNotas.getScene().getRoot();
            mainRoot.setCenter(root);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

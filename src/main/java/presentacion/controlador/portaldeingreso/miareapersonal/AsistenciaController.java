/*
 * SPDX-FileCopyrightText: 2026 inici4rsesi0n
 * SPDX-License-Identifier: GPL-3.0-or-later
 */
package presentacion.controlador.portaldeingreso.miareapersonal;
import aplicacion.casosdeuso.portaldeingreso.miareapersonal.GestionAsistencias;
import dominio.modelo.*;
import infraestructura.eventos.EventBus;
import dominio.puerto.externo.EventBusListener;
import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import java.net.URL;
import java.time.LocalDate;
import java.util.ResourceBundle;
@Component
@Scope("prototype")
public class AsistenciaController implements Initializable, EventBusListener {
    private final GestionAsistencias gestionAsistencias;
    private final EventBus eventBus;
    @FXML private DatePicker dateAsistencia;
    @FXML private TableView<Estudiante> tablaAsistencia;
    @FXML private TableColumn<Estudiante, String> colNombreAsistencia;
    @FXML private TableColumn<Estudiante, Void> colAccionesAsistencia;
    private Asignatura asignaturaActual;
    private Grupo grupoActual;
    public AsistenciaController(GestionAsistencias gestionAsistencias, EventBus eventBus) {
        this.gestionAsistencias = gestionAsistencias;
        this.eventBus = eventBus;
    }
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        dateAsistencia.setValue(LocalDate.now());
        dateAsistencia.setOnAction(e -> cargarAsistencia());
        configurarTabla();
        eventBus.suscribir(TipoEvento.ASISTENCIAS, this);
    }
    public void onEvent(TipoEvento evento) {
        if (evento == TipoEvento.ASISTENCIAS) Platform.runLater(this::cargarAsistencia);
    }
    public void configurar(Asignatura asignatura, Grupo grupo) {
        this.asignaturaActual = asignatura;
        this.grupoActual = grupo;
        cargarAsistencia();
    }
    private void configurarTabla() {
        colNombreAsistencia.setCellValueFactory(cell -> new SimpleStringProperty(cell.getValue().getNombreCompleto()));
        colAccionesAsistencia.setCellFactory(param -> new TableCell<>() {
            private final ToggleButton btnPresente = new ToggleButton("P");
            private final ToggleButton btnTarde = new ToggleButton("T");
            private final ToggleButton btnAusente = new ToggleButton("A");
            private final HBox box = new HBox(5, btnPresente, btnTarde, btnAusente);
            {
                btnPresente.setStyle("-fx-background-color: rgba(16, 185, 129, 0.2); -fx-text-fill: #10B981; -fx-cursor: hand; -fx-border-color: #10B981; -fx-border-radius: 4px;");
                btnTarde.setStyle("-fx-background-color: rgba(245, 158, 11, 0.2); -fx-text-fill: #F59E0B; -fx-cursor: hand; -fx-border-color: #F59E0B; -fx-border-radius: 4px;");
                btnAusente.setStyle("-fx-background-color: rgba(239, 68, 68, 0.2); -fx-text-fill: #EF4444; -fx-cursor: hand; -fx-border-color: #EF4444; -fx-border-radius: 4px;");
                box.setAlignment(Pos.CENTER_LEFT);
                btnPresente.setOnAction(e -> marcar(getTableView().getItems().get(getIndex()), EstadoAsistencia.PRESENTE));
                btnTarde.setOnAction(e -> marcar(getTableView().getItems().get(getIndex()), EstadoAsistencia.TARDE));
                btnAusente.setOnAction(e -> marcar(getTableView().getItems().get(getIndex()), EstadoAsistencia.AUSENTE));
            }
            @Override protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : box);
            }
        });
    }
    private void marcar(Estudiante est, EstadoAsistencia estado) {
        if (grupoActual == null || asignaturaActual == null) return;
        gestionAsistencias.registrarAsistencia(grupoActual, asignaturaActual, est, dateAsistencia.getValue(), estado);
    }
    private void cargarAsistencia() {
        if (grupoActual == null) return;
        tablaAsistencia.setItems(FXCollections.observableArrayList(grupoActual.getEstudiantes()));
    }
}

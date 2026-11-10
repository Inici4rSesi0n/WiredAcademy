/*
 * SPDX-FileCopyrightText: 2026 inici4rsesi0n
 * SPDX-License-Identifier: GPL-3.0-or-later
 */
package presentacion.controlador.portaldeingreso.administraciondelsistema.gestionacademica.horarios;
import infraestructura.eventos.EventBus;
import dominio.puerto.externo.EventBusListener;

import aplicacion.casosdeuso.portaldeingreso.administraciondelsistema.academico.GestionGrupos;
import aplicacion.casosdeuso.portaldeingreso.miareapersonal.GestionHorario;
import aplicacion.casosdeuso.portaldeingreso.administraciondelsistema.academico.GestionPeriodos;
import dominio.modelo.Clase;
import dominio.modelo.Evento;
import dominio.modelo.Grupo;
import dominio.modelo.PeriodoAcademico;
import presentacion.dialogos.Dialogos;
import dominio.modelo.TipoEvento;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

/**
 *
 * @author inici4rsesi0n
 */
@Component
@Scope("prototype")
public class VisorHorarioController implements Initializable, EventBusListener {

    private final GestionGrupos gestionGrupos;
    private final GestionPeriodos gestionPeriodos;
    private final GestionHorario gestionHorario;
    private final EventBus eventBus;

    @FXML private ComboBox<String> cmbGrupoVisor;
    @FXML private ComboBox<String> cmbPeriodoVisor;
    @FXML private Button btnGenerarHorario;
    @FXML private Button btnCerrarVisor;

    @FXML private TableView<Evento> tablaHorario;
    @FXML private TableColumn<Evento, String> colDia;
    @FXML private TableColumn<Evento, String> colHoraInicio;
    @FXML private TableColumn<Evento, String> colHoraFin;
    @FXML private TableColumn<Evento, String> colTipo;
    @FXML private TableColumn<Evento, String> colAsignaturaHorario;
    @FXML private TableColumn<Evento, String> colDocenteHorario;
    @FXML private TableColumn<Evento, String> colAulaHorario;
    @FXML private TableColumn<Evento, String> colModalidadAula;
    @FXML private TableColumn<Evento, String> colDescripcion;

    private ObservableList<Evento> listaHorario;

    public VisorHorarioController(GestionGrupos gestionGrupos,
                                   GestionPeriodos gestionPeriodos,
                                   GestionHorario gestionHorario, EventBus eventBus) {
        this.gestionGrupos = gestionGrupos;
        this.gestionPeriodos = gestionPeriodos;
        this.gestionHorario = gestionHorario;
        this.eventBus = eventBus;
    }

    
    public void onEvent(TipoEvento evento) {
        switch (evento) {
            case CLASES -> refrescarSiVisible();
            case RECREOS -> refrescarSiVisible();
            default -> {}
        }
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        configurarCombos();
        configurarTabla();

        eventBus.suscribir(TipoEvento.CLASES, this);
        eventBus.suscribir(TipoEvento.RECREOS, this);
    }

    private void configurarCombos() {
        cmbGrupoVisor.getItems().clear();
        cmbGrupoVisor.getItems().add("Seleccione un grupo");
        for (Grupo g : gestionGrupos.listarTodos()) {
            cmbGrupoVisor.getItems().add(g.getNombre());
        }
        cmbGrupoVisor.getSelectionModel().selectFirst();

        cmbPeriodoVisor.getItems().clear();
        cmbPeriodoVisor.getItems().add("Seleccione un periodo");
        for (PeriodoAcademico p : gestionPeriodos.listarTodos()) {
            cmbPeriodoVisor.getItems().add(p.getNombre());
        }
        cmbPeriodoVisor.getSelectionModel().selectFirst();
    }

    private void configurarTabla() {
        colDia.setCellValueFactory(new PropertyValueFactory<>("diaSemana"));
        colHoraInicio.setCellValueFactory(new PropertyValueFactory<>("horaInicio"));
        colHoraFin.setCellValueFactory(new PropertyValueFactory<>("horaFin"));
        colTipo.setCellValueFactory(cell -> {
            Evento evento = cell.getValue();
            String tipo = (evento instanceof Clase) ? "Clase" : "Recreo";
            return new javafx.beans.property.SimpleStringProperty(tipo);
        });
        colAsignaturaHorario.setCellValueFactory(cell -> {
            Evento evento = cell.getValue();
            if (evento instanceof Clase clase) {
                return new javafx.beans.property.SimpleStringProperty(
                        clase.getAsignatura() != null ? clase.getAsignatura().getNombre() : "");
            }
            return new javafx.beans.property.SimpleStringProperty("");
        });
        colDocenteHorario.setCellValueFactory(cell -> {
            Evento evento = cell.getValue();
            if (evento instanceof Clase clase) {
                return new javafx.beans.property.SimpleStringProperty(
                        clase.getDocente() != null ? clase.getDocente().getNombreCompleto() : "");
            }
            return new javafx.beans.property.SimpleStringProperty("");
        });
        colAulaHorario.setCellValueFactory(cell -> {
            Evento evento = cell.getValue();
            if (evento instanceof Clase clase) {
                return new javafx.beans.property.SimpleStringProperty(
                        clase.getAula() != null ? clase.getAula().getNombre() : "");
            }
            return new javafx.beans.property.SimpleStringProperty("");
        });
        colModalidadAula.setCellValueFactory(cell -> {
            Evento evento = cell.getValue();
            if (evento instanceof Clase clase && clase.getAula() != null && clase.getAula().getModalidad() != null) {
                return new javafx.beans.property.SimpleStringProperty(clase.getAula().getModalidad().name());
            }
            return new javafx.beans.property.SimpleStringProperty("");
        });
        colDescripcion.setCellValueFactory(cell -> {
            Evento evento = cell.getValue();
            return new javafx.beans.property.SimpleStringProperty(evento.getDescripcion());
        });
    }

    private void refrescarSiVisible() {
        if (listaHorario != null && !listaHorario.isEmpty()) {
            generarHorarioActual();
        }
    }

    @FXML
    private void handleGenerarHorario() {
        if (!validarSeleccion()) return;
        generarHorarioActual();
    }

    private boolean validarSeleccion() {
        String grupoNombre = cmbGrupoVisor.getValue();
        String periodoNombre = cmbPeriodoVisor.getValue();

        if (grupoNombre == null || grupoNombre.startsWith("Seleccione")
                || periodoNombre == null || periodoNombre.startsWith("Seleccione")) {
            Dialogos.info("Seleccione un grupo y un periodo.");
            return false;
        }
        return true;
    }

    private void generarHorarioActual() {
        String grupoNombre = cmbGrupoVisor.getValue();
        String periodoNombre = cmbPeriodoVisor.getValue();

        Grupo grupo = gestionGrupos.buscarPorNombre(grupoNombre);
        PeriodoAcademico periodo = gestionPeriodos.buscarPorNombre(periodoNombre);

        if (grupo == null || periodo == null) {
            Dialogos.info("Grupo o periodo no encontrados.");
            return;
        }

        List<Evento> eventos = gestionHorario.obtenerHorarioCompleto(grupo, periodo);

        Platform.runLater(() -> {
            listaHorario = FXCollections.observableArrayList(eventos);
            tablaHorario.setItems(listaHorario);
            tablaHorario.refresh();
        });
    }

    @FXML
    private void handleCerrar() {
        Stage stage = (Stage) btnCerrarVisor.getScene().getWindow();
        stage.close();
    }
}

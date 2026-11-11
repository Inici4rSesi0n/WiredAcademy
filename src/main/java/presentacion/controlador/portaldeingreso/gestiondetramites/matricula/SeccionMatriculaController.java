/*
 * SPDX-FileCopyrightText: 2026 inici4rsesi0n
 * SPDX-License-Identifier: GPL-3.0-or-later
 */
package presentacion.controlador.portaldeingreso.gestiondetramites.matricula;
import infraestructura.eventos.EventBus;
import dominio.puerto.externo.EventBusListener;
import aplicacion.casosdeuso.portaldeingreso.administraciondelsistema.usuarios.GestionEstudiantes;
import aplicacion.casosdeuso.portaldeingreso.administraciondelsistema.academico.GestionGrupos;
import aplicacion.casosdeuso.portaldeingreso.gestiondetramites.GestionMatriculas;
import aplicacion.casosdeuso.portaldeingreso.administraciondelsistema.academico.GestionPeriodos;
import dominio.modelo.EstadoMatricula;
import dominio.modelo.Estudiante;
import dominio.modelo.Grupo;
import dominio.modelo.Matricula;
import dominio.modelo.PeriodoAcademico;
import dominio.puerto.externo.LoggerPort;
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
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
/**
 *
 * @author inici4rsesi0n
 */
@Component
@Scope("prototype")
public class SeccionMatriculaController implements Initializable, EventBusListener {
    private final GestionMatriculas gestionMatriculas;
    private final GestionEstudiantes gestionEstudiantes;
    private final GestionPeriodos gestionPeriodos;
    private final GestionGrupos gestionGrupos;
    private final LoggerPort logger;
    private final EventBus eventBus;
    @FXML private TableView<Matricula> tablaMatriculas;
    @FXML private TableColumn<Matricula, Integer> colNumMatricula;
    @FXML private TableColumn<Matricula, String> colEstudiante, colPeriodo, colGrupo, colFecha;
    @FXML private TableColumn<Matricula, EstadoMatricula> colEstado;
    @FXML private TableColumn<Matricula, Void> colAccionesMatricula;
    @FXML private ScrollPane scrollPaneFormulario;
    @FXML private ComboBox<String> cmbEstudianteMatricula, cmbPeriodoMatricula, cmbGrupoMatricula;
    @FXML private ComboBox<String> cmbEstadoMatricula;
    @FXML private TextField txtFechaMatricula;
    @FXML private Button btnNuevaMatricula, btnGuardarMatricula, btnCancelarMatricula;
    private ObservableList<Matricula> listaMatriculas;
    private Matricula matriculaEditando;
    public SeccionMatriculaController(GestionMatriculas gestionMatriculas,
                                 GestionEstudiantes gestionEstudiantes,
                                 GestionPeriodos gestionPeriodos,
                                 GestionGrupos gestionGrupos, LoggerPort logger, EventBus eventBus) {
        this.gestionMatriculas = gestionMatriculas;
        this.gestionEstudiantes = gestionEstudiantes;
        this.gestionPeriodos = gestionPeriodos;
        this.gestionGrupos = gestionGrupos;
        this.logger = logger;
        this.eventBus = eventBus;
    }
    public void onEvent(TipoEvento evento) {
        switch (evento) {
            case MATRICULAS -> cargarMatriculas();
            case GRUPOS -> recargarDesdeEvento();
            case ESTUDIANTES -> recargarDesdeEvento();
            case PERIODOS -> recargarDesdeEvento();
            default -> {}
        }
    }
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        configurarTabla();
        configurarColumnaAcciones();
        configurarComboEstado();
        cargarDatosIniciales();
        eventBus.suscribir(TipoEvento.MATRICULAS, this);
        eventBus.suscribir(TipoEvento.GRUPOS, this);
        eventBus.suscribir(TipoEvento.ESTUDIANTES, this);
        eventBus.suscribir(TipoEvento.PERIODOS, this);
    }
    private void recargarDesdeEvento() {
        if (scrollPaneFormulario.isVisible()) {
            recargarCombos();
        }
        cargarMatriculas();
    }
    private void configurarTabla() {
        colNumMatricula.setCellFactory(col -> new TableCell<>() {
            @Override protected void updateItem(Integer item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty ? null : String.valueOf(getIndex() + 1));
            }
        });
        colEstudiante.setCellValueFactory(cell -> new javafx.beans.property.SimpleStringProperty(
                cell.getValue().getEstudiante() != null ? cell.getValue().getEstudiante().getCodigo() : ""));
        colPeriodo.setCellValueFactory(cell -> new javafx.beans.property.SimpleStringProperty(
                cell.getValue().getPeriodo() != null ? cell.getValue().getPeriodo().getNombre() : ""));
        colGrupo.setCellValueFactory(cell -> new javafx.beans.property.SimpleStringProperty(formatearGrupo(cell.getValue().getGrupo())));
        colFecha.setCellValueFactory(new PropertyValueFactory<>("fecha"));
        colEstado.setCellValueFactory(new PropertyValueFactory<>("estado"));
    }
    private String formatearGrupo(Grupo g) {
        if (g == null) return "";
        String grado = (g.getGrado() != null) ? g.getGrado().getNombre() : "Sin grado";
        return g.getNombre() + " (" + grado + ")";
    }
    private void configurarColumnaAcciones() {
        colAccionesMatricula.setCellFactory(param -> new TableCell<>() {
            private final Button btnEditar = new Button("Editar");
            private final Button btnEliminar = new Button("Eliminar");
            private final HBox hbox = new HBox(10, btnEditar, btnEliminar);
            {
                btnEditar.getStyleClass().add("boton-tabla-editar");
                btnEliminar.getStyleClass().add("boton-tabla-eliminar");
                btnEditar.setOnAction(e -> cargarEnFormulario(getTableView().getItems().get(getIndex())));
                btnEliminar.setOnAction(e -> eliminarMatricula(getTableView().getItems().get(getIndex())));
            }
            @Override protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : hbox);
            }
        });
    }
    private void cargarDatosIniciales() {
        configurarComboEstado();
        cargarMatriculas();
    }
    private void configurarComboEstado() {
        cmbEstadoMatricula.getItems().clear();
        cmbEstadoMatricula.getItems().add("Seleccione un estado");
        for (EstadoMatricula estado : EstadoMatricula.values()) {
            cmbEstadoMatricula.getItems().add(estado.name());
        }
        cmbEstadoMatricula.getSelectionModel().selectFirst();
    }
    private void recargarCombos() {
        cmbEstudianteMatricula.getItems().clear();
        cmbEstudianteMatricula.getItems().add("Seleccione un estudiante");
        for (Estudiante e : gestionEstudiantes.listarTodos()) {
            cmbEstudianteMatricula.getItems().add(e.getCodigo() + " - " + e.getNombreCompleto());
        }
        cmbPeriodoMatricula.getItems().clear();
        cmbPeriodoMatricula.getItems().add("Seleccione un periodo");
        for (PeriodoAcademico p : gestionPeriodos.listarTodos()) {
            cmbPeriodoMatricula.getItems().add(p.getNombre());
        }
        cmbGrupoMatricula.getItems().clear();
        cmbGrupoMatricula.getItems().add("Seleccione un grupo");
        for (Grupo g : gestionGrupos.listarTodos()) {
            cmbGrupoMatricula.getItems().add(formatearGrupo(g));
        }
    }
    private void cargarMatriculas() {
        Platform.runLater(() -> {
            try {
                listaMatriculas = FXCollections.observableArrayList(gestionMatriculas.listarTodas());
                tablaMatriculas.setItems(listaMatriculas);
                tablaMatriculas.refresh();
            } catch (Exception e) {
                logger.error("Error al cargar matrículas: " + e.getMessage());
                Dialogos.info("Error al cargar matrículas.");
            }
        });
    }
    @FXML private void handleNuevaMatricula() {
        matriculaEditando = null;
        recargarCombos();
        limpiarFormulario();
        scrollPaneFormulario.setVisible(true);
        scrollPaneFormulario.setManaged(true);
    }
    @FXML private void handleGuardarMatricula() {
        String estudianteSeleccionado = cmbEstudianteMatricula.getValue();
        String periodoSeleccionado = cmbPeriodoMatricula.getValue();
        String grupoSeleccionado = cmbGrupoMatricula.getValue();
        String fecha = txtFechaMatricula.getText().trim();
        String estadoSeleccionado = cmbEstadoMatricula.getValue();
        if (estudianteSeleccionado == null || estudianteSeleccionado.startsWith("Seleccione")
                || periodoSeleccionado == null || periodoSeleccionado.startsWith("Seleccione")
                || grupoSeleccionado == null || grupoSeleccionado.startsWith("Seleccione")
                || estadoSeleccionado == null || estadoSeleccionado.startsWith("Seleccione")) {
            Dialogos.info("Todos los campos son obligatorios."); return;
        }
        try {
            String codigoEstudiante = estudianteSeleccionado.split(" - ")[0];
            Estudiante estudiante = gestionEstudiantes.buscarPorCodigo(codigoEstudiante);
            PeriodoAcademico periodo = gestionPeriodos.buscarPorNombre(periodoSeleccionado);
            Grupo grupo = parsearGrupo(grupoSeleccionado);
            EstadoMatricula estado = EstadoMatricula.valueOf(estadoSeleccionado);
            if (estudiante == null || periodo == null || grupo == null) {
                Dialogos.info("Los datos seleccionados no son válidos."); return;
            }
            if (matriculaEditando == null) {
                gestionMatriculas.matricularEstudiante(estudiante, periodo, grupo, null, fecha, estado);
            } else {
                matriculaEditando.setEstado(estado);
                matriculaEditando.setFecha(fecha);
                gestionMatriculas.actualizar(matriculaEditando);
            }
            limpiarFormulario();
        } catch (Exception e) {
            logger.error("Error al guardar matrícula: " + e.getMessage());
            Dialogos.info(e.getMessage());
        }
    }
    private Grupo parsearGrupo(String texto) {
        int idx = texto.lastIndexOf(" (");
        if (idx < 0) return null;
        String nombreGrupo = texto.substring(0, idx).trim();
        String nombreGrado = texto.substring(idx + 2, texto.length() - 1).trim();
        List<Grupo> encontrados = gestionGrupos.buscarPorNombreYGrado(nombreGrupo, nombreGrado);
        return encontrados.isEmpty() ? null : encontrados.get(0);
    }
    @FXML private void handleCancelarMatricula() {
        limpiarFormulario();
    }
    private void cargarEnFormulario(Matricula matricula) {
        matriculaEditando = matricula;
        recargarCombos();
        cmbEstudianteMatricula.setValue(matricula.getEstudiante().getCodigo() + " - " + matricula.getEstudiante().getNombreCompleto());
        cmbPeriodoMatricula.setValue(matricula.getPeriodo().getNombre());
        cmbGrupoMatricula.setValue(formatearGrupo(matricula.getGrupo()));
        txtFechaMatricula.setText(matricula.getFecha());
        cmbEstadoMatricula.setValue(matricula.getEstado().name());
        scrollPaneFormulario.setVisible(true);
        scrollPaneFormulario.setManaged(true);
    }
    private void eliminarMatricula(Matricula matricula) {
        if (Dialogos.confirmar("Confirmar eliminación", "¿Está seguro de eliminar la matricula de " + matricula.getEstudiante().getCodigo() + "?")) {
            try {
                gestionMatriculas.eliminarMatricula(matricula);
            } catch (Exception e) {
                logger.error("Error al eliminar matrícula: " + e.getMessage());
                Dialogos.info(e.getMessage());
            }
        }
    }
    private void limpiarFormulario() {
        matriculaEditando = null;
        cmbEstudianteMatricula.getSelectionModel().selectFirst();
        cmbPeriodoMatricula.getSelectionModel().selectFirst();
        cmbGrupoMatricula.getSelectionModel().selectFirst();
        cmbEstadoMatricula.getSelectionModel().selectFirst();
        txtFechaMatricula.clear();
        scrollPaneFormulario.setVisible(false);
        scrollPaneFormulario.setManaged(false);
    }
}

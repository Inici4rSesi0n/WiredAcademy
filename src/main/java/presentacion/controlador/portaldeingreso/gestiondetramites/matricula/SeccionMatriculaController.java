/*
 * SPDX-FileCopyrightText: 2026 inici4rsesi0n
 * SPDX-License-Identifier: GPL-3.0-or-later
 */
package presentacion.controlador.portaldeingreso.gestiondetramites.matricula;
import infraestructura.eventos.EventBus;
import dominio.puerto.externo.EventBusListener;
import aplicacion.casosdeuso.portaldeingreso.administraciondelsistema.usuarios.GestionEstudiantes;
import aplicacion.casosdeuso.portaldeingreso.administraciondelsistema.academico.GestionGrupos;
import aplicacion.casosdeuso.portaldeingreso.administraciondelsistema.academico.GestionAsignaturas;
import aplicacion.casosdeuso.portaldeingreso.gestiondetramites.GestionMatriculas;
import aplicacion.casosdeuso.portaldeingreso.administraciondelsistema.academico.GestionPeriodos;
import dominio.modelo.*;
import dominio.modelo.configuracion.TipoInstitucion;
import dominio.puerto.externo.LoggerPort;
import dominio.puerto.repositorio.RepositorioConfiguracionSistema;
import presentacion.dialogos.Dialogos;
import java.net.URL;
import java.util.ArrayList;
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
import javafx.scene.layout.VBox;
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
    private final GestionAsignaturas gestionAsignaturas;
    private final RepositorioConfiguracionSistema repoConfig;
    private final LoggerPort logger;
    private final EventBus eventBus;
    @FXML private TableView<Matricula> tablaMatriculas;
    @FXML private TableColumn<Matricula, Integer> colNumMatricula;
    @FXML private TableColumn<Matricula, String> colEstudiante, colPeriodo, colGrupo, colFecha;
    @FXML private TableColumn<Matricula, EstadoMatricula> colEstado;
    @FXML private TableColumn<Matricula, Void> colAccionesMatricula;
    @FXML private ScrollPane scrollPaneFormulario;
    @FXML private ComboBox<String> cmbEstudianteMatricula, cmbPeriodoMatricula, cmbGrupoMatricula, cmbEstadoMatricula;
    @FXML private TextField txtFechaMatricula;
    @FXML private VBox panelColegio, panelUniversidad, vboxInscripciones;
    @FXML private Button btnNuevaMatricula, btnGuardarMatricula, btnCancelarMatricula, btnAgregarAsignatura;
    private ObservableList<Matricula> listaMatriculas;
    private Matricula matriculaEditando;
    private boolean esUniversidad = false;
    public SeccionMatriculaController(GestionMatriculas gestionMatriculas,
                                 GestionEstudiantes gestionEstudiantes,
                                 GestionPeriodos gestionPeriodos,
                                 GestionGrupos gestionGrupos,
                                 GestionAsignaturas gestionAsignaturas,
                                 RepositorioConfiguracionSistema repoConfig,
                                 LoggerPort logger, EventBus eventBus) {
        this.gestionMatriculas = gestionMatriculas;
        this.gestionEstudiantes = gestionEstudiantes;
        this.gestionPeriodos = gestionPeriodos;
        this.gestionGrupos = gestionGrupos;
        this.gestionAsignaturas = gestionAsignaturas;
        this.repoConfig = repoConfig;
        this.logger = logger;
        this.eventBus = eventBus;
    }
    public void onEvent(TipoEvento evento) {
        switch (evento) {
            case MATRICULAS -> cargarMatriculas();
            case GRUPOS, ESTUDIANTES, PERIODOS -> recargarDesdeEvento();
            default -> {}
        }
    }
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        configurarTabla();
        configurarColumnaAcciones();
        configurarComboEstado();
        evaluarTipoInstitucion();
        cargarDatosIniciales();
        eventBus.suscribir(TipoEvento.MATRICULAS, this);
        eventBus.suscribir(TipoEvento.GRUPOS, this);
        eventBus.suscribir(TipoEvento.ESTUDIANTES, this);
        eventBus.suscribir(TipoEvento.PERIODOS, this);
    }
    private void evaluarTipoInstitucion() {
        // UNIVERSIDAD e INSTITUTO usan matrícula flexible (por asignatura); COLEGIO usa grupo completo
        esUniversidad = repoConfig.obtener()
                .map(c -> c.getTipoInstitucion() == TipoInstitucion.UNIVERSIDAD || c.getTipoInstitucion() == TipoInstitucion.INSTITUTO)
                .orElse(false);
    }
    private void recargarDesdeEvento() {
        if (scrollPaneFormulario.isVisible()) recargarCombos();
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
        if (g == null) return "Múltiples";
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
    private void cargarDatosIniciales() { configurarComboEstado(); cargarMatriculas(); }
    private void configurarComboEstado() {
        cmbEstadoMatricula.getItems().clear();
        cmbEstadoMatricula.getItems().add("Seleccione un estado");
        for (EstadoMatricula estado : EstadoMatricula.values()) cmbEstadoMatricula.getItems().add(estado.name());
        cmbEstadoMatricula.getSelectionModel().selectFirst();
    }
    private void recargarCombos() {
        cmbEstudianteMatricula.getItems().clear();
        cmbEstudianteMatricula.getItems().add("Seleccione un estudiante");
        for (Estudiante e : gestionEstudiantes.listarTodos()) cmbEstudianteMatricula.getItems().add(e.getCodigo() + " - " + e.getNombreCompleto());
        
        cmbPeriodoMatricula.getItems().clear();
        cmbPeriodoMatricula.getItems().add("Seleccione un periodo");
        for (PeriodoAcademico p : gestionPeriodos.listarTodos()) cmbPeriodoMatricula.getItems().add(p.getNombre());
        
        cmbGrupoMatricula.getItems().clear();
        cmbGrupoMatricula.getItems().add("Seleccione un grupo");
        for (Grupo g : gestionGrupos.listarTodos()) cmbGrupoMatricula.getItems().add(formatearGrupo(g));
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
        panelColegio.setVisible(!esUniversidad);
        panelColegio.setManaged(!esUniversidad);
        panelUniversidad.setVisible(esUniversidad);
        panelUniversidad.setManaged(esUniversidad);
        if (esUniversidad) handleAgregarInscripcion();
        scrollPaneFormulario.setVisible(true);
        scrollPaneFormulario.setManaged(true);
    }
    @FXML private void handleAgregarInscripcion() {
        HBox row = new HBox(10);
        ComboBox<String> cmbAsig = new ComboBox<>();
        cmbAsig.getItems().add("Asignatura...");
        for (Asignatura a : gestionAsignaturas.listarTodos()) cmbAsig.getItems().add(a.getNombre());
        cmbAsig.getSelectionModel().selectFirst();
        cmbAsig.setStyle("-fx-background-color: #161A29; -fx-text-fill: white;");
        
        ComboBox<String> cmbGrup = new ComboBox<>();
        cmbGrup.getItems().add("Grupo...");
        for (Grupo g : gestionGrupos.listarTodos()) cmbGrup.getItems().add(g.getNombre());
        cmbGrup.getSelectionModel().selectFirst();
        cmbGrup.setStyle("-fx-background-color: #161A29; -fx-text-fill: white;");
        
        Button btnDel = new Button("✕");
        btnDel.getStyleClass().add("boton-tabla-eliminar");
        btnDel.setOnAction(e -> vboxInscripciones.getChildren().remove(row));
        
        row.getChildren().addAll(cmbAsig, cmbGrup, btnDel);
        vboxInscripciones.getChildren().add(row);
    }
    @FXML private void handleGuardarMatricula() {
        String estudianteSeleccionado = cmbEstudianteMatricula.getValue();
        String periodoSeleccionado = cmbPeriodoMatricula.getValue();
        String fecha = txtFechaMatricula.getText().trim();
        String estadoSeleccionado = cmbEstadoMatricula.getValue();
        
        if (estudianteSeleccionado == null || estudianteSeleccionado.startsWith("Seleccione")
                || periodoSeleccionado == null || periodoSeleccionado.startsWith("Seleccione")
                || estadoSeleccionado == null || estadoSeleccionado.startsWith("Seleccione")) {
            Dialogos.info("Estudiante, periodo y estado son obligatorios."); return;
        }
        try {
            String codigoEstudiante = estudianteSeleccionado.split(" - ")[0];
            Estudiante estudiante = gestionEstudiantes.buscarPorCodigo(codigoEstudiante);
            PeriodoAcademico periodo = gestionPeriodos.buscarPorNombre(periodoSeleccionado);
            EstadoMatricula estado = EstadoMatricula.valueOf(estadoSeleccionado);
            if (esUniversidad) {
                List<InscripcionAsignatura> inscripciones = new ArrayList<>();
                for (javafx.scene.Node node : vboxInscripciones.getChildren()) {
                    if (node instanceof HBox row) {
                        ComboBox<?> cbA = (ComboBox<?>) row.getChildren().get(0);
                        ComboBox<?> cbG = (ComboBox<?>) row.getChildren().get(1);
                        String a = (String) cbA.getValue();
                        String g = (String) cbG.getValue();
                        if (a != null && !a.startsWith("Asignatura") && g != null && !g.startsWith("Grupo")) {
                            Asignatura asig = gestionAsignaturas.buscarAsignatura(a);
                            Grupo grp = gestionGrupos.buscarPorNombre(g);
                            if (asig != null && grp != null) inscripciones.add(new InscripcionAsignatura(asig, grp));
                        }
                    }
                }
                if (inscripciones.isEmpty()) { Dialogos.info("Debe agregar al menos una asignatura."); return; }
                gestionMatriculas.matricularPorAsignaturas(estudiante, periodo, inscripciones, fecha, estado);
            } else {
                String grupoSeleccionado = cmbGrupoMatricula.getValue();
                if (grupoSeleccionado == null || grupoSeleccionado.startsWith("Seleccione")) {
                    Dialogos.info("Debe seleccionar un grupo."); return;
                }
                Grupo grupo = parsearGrupo(grupoSeleccionado);
                if (estudiante == null || periodo == null || grupo == null) {
                    Dialogos.info("Los datos seleccionados no son válidos."); return;
                }
                gestionMatriculas.matricularEstudiante(estudiante, periodo, grupo, null, fecha, estado);
            }
            limpiarFormulario();
        } catch (Exception e) {
            logger.error("Error al guardar matrícula: " + e.getMessage());
            Dialogos.info(e.getMessage());
        }
    }
    private Grupo parsearGrupo(String texto) {
        int idx = texto.lastIndexOf(" (");
        if (idx < 0) return gestionGrupos.buscarPorNombre(texto);
        String nombreGrupo = texto.substring(0, idx).trim();
        String nombreGrado = texto.substring(idx + 2, texto.length() - 1).trim();
        List<Grupo> encontrados = gestionGrupos.buscarPorNombreYGrado(nombreGrupo, nombreGrado);
        return encontrados.isEmpty() ? null : encontrados.get(0);
    }
    @FXML private void handleCancelarMatricula() { limpiarFormulario(); }
    private void cargarEnFormulario(Matricula matricula) {
        if (esUniversidad) {
            Dialogos.info("La edición avanzada de matrículas universitarias estará disponible en la próxima versión.");
            return;
        }
        matriculaEditando = matricula;
        recargarCombos();
        cmbEstudianteMatricula.setValue(matricula.getEstudiante().getCodigo() + " - " + matricula.getEstudiante().getNombreCompleto());
        cmbPeriodoMatricula.setValue(matricula.getPeriodo().getNombre());
        cmbGrupoMatricula.setValue(formatearGrupo(matricula.getGrupo()));
        txtFechaMatricula.setText(matricula.getFecha());
        cmbEstadoMatricula.setValue(matricula.getEstado().name());
        panelColegio.setVisible(true); panelColegio.setManaged(true);
        panelUniversidad.setVisible(false); panelUniversidad.setManaged(false);
        scrollPaneFormulario.setVisible(true);
        scrollPaneFormulario.setManaged(true);
    }
    private void eliminarMatricula(Matricula matricula) {
        if (Dialogos.confirmar("Confirmar eliminación", "¿Está seguro de eliminar la matricula de " + matricula.getEstudiante().getCodigo() + "?")) {
            try { gestionMatriculas.eliminarMatricula(matricula); } 
            catch (Exception e) { logger.error("Error al eliminar matrícula: " + e.getMessage()); Dialogos.info(e.getMessage()); }
        }
    }
    private void limpiarFormulario() {
        matriculaEditando = null;
        cmbEstudianteMatricula.getSelectionModel().selectFirst();
        cmbPeriodoMatricula.getSelectionModel().selectFirst();
        cmbGrupoMatricula.getSelectionModel().selectFirst();
        cmbEstadoMatricula.getSelectionModel().selectFirst();
        txtFechaMatricula.clear();
        vboxInscripciones.getChildren().clear();
        scrollPaneFormulario.setVisible(false);
        scrollPaneFormulario.setManaged(false);
    }
}

package presentacion.controlador.portaldeingreso.administraciondelsistema.gestionacademica.horarios;

import aplicacion.casosdeuso.portaldeingreso.administraciondelsistema.academico.GestionAsignaturas;
import aplicacion.casosdeuso.portaldeingreso.administraciondelsistema.academico.GestionAulas;
import aplicacion.casosdeuso.portaldeingreso.administraciondelsistema.academico.GestionClases;
import aplicacion.casosdeuso.portaldeingreso.administraciondelsistema.academico.GestionGrupos;
import aplicacion.casosdeuso.portaldeingreso.administraciondelsistema.academico.GestionPeriodos;
import aplicacion.casosdeuso.portaldeingreso.administraciondelsistema.usuarios.GestionDocentes;
import dominio.modelo.Asignatura;
import dominio.modelo.Aula;
import dominio.modelo.Clase;
import dominio.modelo.Docente;
import dominio.modelo.Grupo;
import dominio.modelo.PeriodoAcademico;
import dominio.puerto.externo.LoggerPort;
import infraestructura.configuracion.SpringFxmlLoader;
import presentacion.dialogos.Dialogos;
import presentacion.eventos.SistemaEventBus;
import presentacion.eventos.TipoEvento;

import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

/**
 * Controlador de la pestaña "Horarios" dentro de Gestión Académica.
 *
 * <p>Expone el BorderPane raíz a {@link SeccionGestionAcademicaController}
 * mediante {@link #getRoot()} para permitir el toggle visible/managed
 * de la pestaña desde el selector de categorías.</p>
 *
 * @author inici4rsesi0n
 */
@Component
@Scope("prototype")
public class GestionHorariosController implements Initializable {

    @FXML private BorderPane root;
    public BorderPane getRoot() { return root; }

    private final GestionClases gestionClases;
    private final GestionAsignaturas gestionAsignaturas;
    private final GestionDocentes gestionDocentes;
    private final GestionGrupos gestionGrupos;
    private final GestionAulas gestionAulas;
    private final GestionPeriodos gestionPeriodos;
    private final SpringFxmlLoader fxmlLoader;
    private final LoggerPort logger;

    @FXML private TableView<Clase> tablaClases;
    @FXML private TableColumn<Clase, Integer> colNumClase;
    @FXML private TableColumn<Clase, String> colDiaClase, colInicioClase, colFinClase, colAsignaturaClase,
            colDocenteClase, colGrupoClase, colAulaClase, colPeriodoClase;
    @FXML private TableColumn<Clase, Void> colAccionesClase;
    @FXML private VBox panelFormularioClase;
    @FXML private ScrollPane scrollPaneFormulario;
    @FXML private ComboBox<String> cmbDiaClase, cmbAsignaturaClase, cmbDocenteClase,
            cmbGrupoClase, cmbAulaClase, cmbPeriodoClase;
    @FXML private TextField txtInicioClase, txtFinClase;
    @FXML private Button btnNuevaClase, btnGuardarClase, btnCancelarClase;

    private ObservableList<Clase> listaClases;
    private Clase claseEditando;

    public GestionHorariosController(GestionClases gestionClases,
                                 GestionAsignaturas gestionAsignaturas,
                                 GestionDocentes gestionDocentes,
                                 GestionGrupos gestionGrupos,
                                 GestionAulas gestionAulas,
                                 GestionPeriodos gestionPeriodos,
                                 SpringFxmlLoader fxmlLoader,
                                 LoggerPort logger) {
        this.gestionClases = gestionClases;
        this.gestionAsignaturas = gestionAsignaturas;
        this.gestionDocentes = gestionDocentes;
        this.gestionGrupos = gestionGrupos;
        this.gestionAulas = gestionAulas;
        this.gestionPeriodos = gestionPeriodos;
        this.fxmlLoader = fxmlLoader;
        this.logger = logger;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        configurarTablaClases();
        cargarClases();
        SistemaEventBus.suscribir(TipoEvento.CLASES, this::cargarClases);
        SistemaEventBus.suscribir(TipoEvento.GRUPOS, this::recargarDesdeEvento);
        SistemaEventBus.suscribir(TipoEvento.DOCENTES, this::recargarDesdeEvento);
        SistemaEventBus.suscribir(TipoEvento.AULAS, this::recargarDesdeEvento);
        SistemaEventBus.suscribir(TipoEvento.PERIODOS, this::recargarDesdeEvento);
        SistemaEventBus.suscribir(TipoEvento.ASIGNATURAS, this::recargarDesdeEvento);
    }

    private void recargarDesdeEvento() {
        if (panelFormularioClase.isVisible()) recargarComboClases();
        cargarClases();
    }

    private void configurarTablaClases() {
        colNumClase.setCellFactory(col -> new TableCell<>() {
            @Override protected void updateItem(Integer item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty ? null : String.valueOf(getIndex() + 1));
            }
        });
        colDiaClase.setCellValueFactory(new PropertyValueFactory<>("diaSemana"));
        colInicioClase.setCellValueFactory(new PropertyValueFactory<>("horaInicio"));
        colFinClase.setCellValueFactory(new PropertyValueFactory<>("horaFin"));
        colAsignaturaClase.setCellValueFactory(cell -> new javafx.beans.property.SimpleStringProperty(
                cell.getValue().getAsignatura() != null ? cell.getValue().getAsignatura().getNombre() : ""));
        colDocenteClase.setCellValueFactory(cell -> new javafx.beans.property.SimpleStringProperty(
                cell.getValue().getDocente() != null ? cell.getValue().getDocente().getNombreCompleto() : ""));
        colGrupoClase.setCellValueFactory(cell -> new javafx.beans.property.SimpleStringProperty(
                cell.getValue().getGrupo() != null ? cell.getValue().getGrupo().getNombre() : ""));
        colAulaClase.setCellValueFactory(cell -> new javafx.beans.property.SimpleStringProperty(
                cell.getValue().getAula() != null ? cell.getValue().getAula().getNombre() : ""));
        colPeriodoClase.setCellValueFactory(cell -> new javafx.beans.property.SimpleStringProperty(
                cell.getValue().getPeriodo() != null ? cell.getValue().getPeriodo().getNombre() : ""));
        configurarAcciones();
    }

    private void configurarAcciones() {
        colAccionesClase.setCellFactory(param -> new TableCell<>() {
            private final Button btnEditar = new Button("Editar");
            private final Button btnEliminar = new Button("Eliminar");
            private final HBox hbox = new HBox(10, btnEditar, btnEliminar);
            {
                btnEditar.getStyleClass().add("boton-tabla-editar");
                btnEliminar.getStyleClass().add("boton-tabla-eliminar");
                btnEditar.setOnAction(e -> cargarEnFormulario(getTableView().getItems().get(getIndex())));
                btnEliminar.setOnAction(e -> eliminar(getTableView().getItems().get(getIndex())));
            }
            @Override protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : hbox);
            }
        });
    }

    private void cargarClases() {
        Platform.runLater(() -> {
            try {
                listaClases = FXCollections.observableArrayList(gestionClases.listarClases());
                tablaClases.setItems(listaClases);
                tablaClases.refresh();
            } catch (Exception e) {
                logger.error("Error al cargar clases: " + e.getMessage());
                Dialogos.info("Error al cargar clases.");
            }
        });
    }

    private void recargarComboClases() {
        cmbDiaClase.getItems().clear();
        cmbDiaClase.getItems().add("Seleccione un día");
        cmbDiaClase.getItems().addAll("Lunes", "Martes", "Miércoles", "Jueves", "Viernes", "Sábado", "Domingo");
        cmbDiaClase.getSelectionModel().selectFirst();

        cmbAsignaturaClase.getItems().clear();
        cmbAsignaturaClase.getItems().add("Seleccione una asignatura");
        for (Asignatura a : gestionAsignaturas.listarTodos()) {
            cmbAsignaturaClase.getItems().add(a.getNombre());
        }

        cmbDocenteClase.getItems().clear();
        cmbDocenteClase.getItems().add("Seleccione un docente");
        for (Docente d : gestionDocentes.listarTodos()) {
            cmbDocenteClase.getItems().add(d.getCodigo() + " - " + d.getNombreCompleto());
        }

        cmbGrupoClase.getItems().clear();
        cmbGrupoClase.getItems().add("Seleccione un grupo");
        for (Grupo g : gestionGrupos.listarTodos()) {
            cmbGrupoClase.getItems().add(formatearGrupo(g));
        }

        cmbAulaClase.getItems().clear();
        cmbAulaClase.getItems().add("Seleccione un aula");
        for (Aula a : gestionAulas.listarTodos()) {
            cmbAulaClase.getItems().add(a.getNombre());
        }

        cmbPeriodoClase.getItems().clear();
        cmbPeriodoClase.getItems().add("Seleccione un periodo");
        for (PeriodoAcademico p : gestionPeriodos.listarTodos()) {
            cmbPeriodoClase.getItems().add(p.getNombre());
        }
    }

    private void cargarEnFormulario(Clase c) {
        claseEditando = c;
        recargarComboClases();
        cmbDiaClase.setValue(c.getDiaSemana());
        txtInicioClase.setText(c.getHoraInicio());
        txtFinClase.setText(c.getHoraFin());
        cmbAsignaturaClase.setValue(c.getAsignatura() != null ? c.getAsignatura().getNombre() : null);
        cmbDocenteClase.setValue(c.getDocente() != null
                ? c.getDocente().getCodigo() + " - " + c.getDocente().getNombreCompleto()
                : null);
        cmbGrupoClase.setValue(formatearGrupo(c.getGrupo()));
        cmbAulaClase.setValue(c.getAula() != null ? c.getAula().getNombre() : null);
        cmbPeriodoClase.setValue(c.getPeriodo() != null ? c.getPeriodo().getNombre() : null);
        scrollPaneFormulario.setVisible(true);
        scrollPaneFormulario.setManaged(true);
    }

    private void eliminar(Clase c) {
        if (!Dialogos.confirmar("Confirmar eliminación", "¿Está seguro de eliminar esta clase?")) return;
        try {
            gestionClases.eliminarClase(c);
            SistemaEventBus.notificar(TipoEvento.CLASES);
        } catch (Exception e) {
            logger.error("Error al eliminar clase: " + e.getMessage());
            Dialogos.info(e.getMessage());
        }
    }

    public void handleNuevaClase() {
        claseEditando = null;
        recargarComboClases();
        limpiarFormularioClase();
        scrollPaneFormulario.setVisible(true);
        scrollPaneFormulario.setManaged(true);
    }

    @FXML private void handleGuardarClase() {
        String dia = cmbDiaClase.getValue();
        String inicio = txtInicioClase.getText().trim();
        String fin = txtFinClase.getText().trim();
        String asignaturaNombre = cmbAsignaturaClase.getValue();
        String docenteSel = cmbDocenteClase.getValue();
        String grupoNombre = cmbGrupoClase.getValue();
        String aulaNombre = cmbAulaClase.getValue();
        String periodoNombre = cmbPeriodoClase.getValue();

        if (dia == null || inicio.isBlank() || fin.isBlank()
                || asignaturaNombre == null || asignaturaNombre.startsWith("Seleccione")
                || docenteSel == null || docenteSel.startsWith("Seleccione")
                || grupoNombre == null || grupoNombre.startsWith("Seleccione")
                || aulaNombre == null || aulaNombre.startsWith("Seleccione")
                || periodoNombre == null || periodoNombre.startsWith("Seleccione")) {
            Dialogos.info("Todos los campos son obligatorios.");
            return;
        }
        if (!inicio.matches("([01]\\d|2[0-3]):[0-5]\\d")
                || !fin.matches("([01]\\d|2[0-3]):[0-5]\\d")) {
            Dialogos.info("Formato de hora inválido. Use HH:mm (ej. 08:00, 14:30).");
            return;
        }

        Asignatura asig = gestionAsignaturas.buscarAsignatura(asignaturaNombre);
        Docente doc = gestionDocentes.buscarPorCodigo(docenteSel.split(" - ")[0]);
        Grupo grupo = parsearGrupo(grupoNombre);
        Aula aula = gestionAulas.buscarAula(aulaNombre);
        PeriodoAcademico periodo = gestionPeriodos.buscarPorNombre(periodoNombre);

        if (asig == null || doc == null || grupo == null || aula == null || periodo == null) {
            Dialogos.info("Datos inválidos.");
            return;
        }

        try {
            if (claseEditando == null) {
                gestionClases.crearClase(dia, inicio, fin, asig, doc, grupo, aula, periodo);
            } else {
                gestionClases.actualizarClase(claseEditando, dia, inicio, fin, asig, doc, grupo, aula, periodo);
            }
            SistemaEventBus.notificar(TipoEvento.CLASES);
            scrollPaneFormulario.setVisible(false);
            scrollPaneFormulario.setManaged(false);
        } catch (Exception e) {
            logger.error("Error al guardar clase: " + e.getMessage());
            Dialogos.info(e.getMessage());
        }
    }

    @FXML private void handleCancelarClase() {
        scrollPaneFormulario.setVisible(false);
        scrollPaneFormulario.setManaged(false);
    }

    public void handleVerHorario() {
        try {
            FXMLLoader loader = fxmlLoader.crearLoader("/fxml/portaldeingreso/administraciondelsistema/gestionacademica/horarios/visor_horario.fxml");
            Parent visorRoot = loader.load();

            Stage stage = new Stage();
            stage.initStyle(StageStyle.UNDECORATED);
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.initOwner(root.getScene().getWindow());

            Scene scene = new Scene(visorRoot);
            URL css = getClass().getResource("/css/app.css");
            if (css != null) {
                scene.getStylesheets().add(css.toExternalForm());
            }

            stage.setScene(scene);

            Stage owner = (Stage) root.getScene().getWindow();
            double width = owner.getWidth() * 0.8;
            double height = owner.getHeight() * 0.8;
            stage.setWidth(width);
            stage.setHeight(height);
            stage.setX(owner.getX() + (owner.getWidth() - width) / 2);
            stage.setY(owner.getY() + (owner.getHeight() - height) / 2);

            stage.showAndWait();
        } catch (IOException e) {
            logger.error("Error al abrir visor de horario: " + e.getMessage());
            Dialogos.info("No se pudo abrir el visor de horario.");
        }
    }

    private void limpiarFormularioClase() {
        cmbDiaClase.getSelectionModel().selectFirst();
        txtInicioClase.clear();
        txtFinClase.clear();
        cmbAsignaturaClase.getSelectionModel().selectFirst();
        cmbDocenteClase.getSelectionModel().selectFirst();
        cmbGrupoClase.getSelectionModel().selectFirst();
        cmbAulaClase.getSelectionModel().selectFirst();
        cmbPeriodoClase.getSelectionModel().selectFirst();
    }

    private String formatearGrupo(Grupo g) {
        if (g == null) return "";
        String grado = (g.getGrado() != null) ? g.getGrado().getNombre() : "Sin grado";
        return g.getNombre() + " (" + grado + ")";
    }

    private Grupo parsearGrupo(String texto) {
        if (texto == null) return null;
        int idx = texto.lastIndexOf(" (");
        if (idx < 0) return null;
        String nombreGrupo = texto.substring(0, idx).trim();
        String nombreGrado = texto.substring(idx + 2, texto.length() - 1).trim();
        List<Grupo> encontrados = gestionGrupos.buscarPorNombreYGrado(nombreGrupo, nombreGrado);
        return encontrados.isEmpty() ? null : encontrados.get(0);
    }
}

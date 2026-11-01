package presentacion.controlador.portaldeingreso.administraciondelsistema.gestionacademica.catalogos;
import presentacion.eventos.EventBus;
import presentacion.eventos.EventBusListener;
import aplicacion.casosdeuso.portaldeingreso.administraciondelsistema.academico.GestionPeriodos;
import dominio.modelo.PeriodoAcademico;
import dominio.puerto.externo.LoggerPort;
import presentacion.dialogos.Dialogos;
import presentacion.eventos.TipoEvento;
import java.net.URL;
import java.time.LocalDate;
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
public class PeriodosController implements Initializable, EventBusListener {
    private final GestionPeriodos gestionPeriodos;
    private final LoggerPort logger;
    private final EventBus eventBus;

    @FXML private TableView<PeriodoAcademico> tablaPeriodos;
    @FXML private TableColumn<PeriodoAcademico, Integer> colNumPeriodo;
    @FXML private TableColumn<PeriodoAcademico, String> colNombrePeriodo, colFechaInicio, colFechaFin, colEstadoPeriodo;
    @FXML private TableColumn<PeriodoAcademico, Void> colAccionesPeriodo;
    @FXML private VBox panelFormularioPeriodo;
    @FXML private javafx.scene.control.ScrollPane scrollPaneFormulario;
    @FXML private TextField txtNombrePeriodo;
    @FXML private DatePicker dateFechaInicio, dateFechaFin;
    @FXML private ComboBox<String> cmbEstadoPeriodo;
    @FXML private Button btnNuevoPeriodo, btnGuardarPeriodo, btnCancelarPeriodo;

    private ObservableList<PeriodoAcademico> listaPeriodos;
    private PeriodoAcademico periodoEditando;

    public PeriodosController(GestionPeriodos gestionPeriodos, LoggerPort logger, EventBus eventBus) {
        this.gestionPeriodos = gestionPeriodos;
        this.logger = logger;
        this.eventBus = eventBus;
    }

    
    public void onEvent(TipoEvento evento) {
        switch (evento) {
            case PERIODOS -> cargarPeriodos();
            default -> {}
        }
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        configurarComboEstado();
        configurarTabla();
        cargarPeriodos();
        eventBus.suscribir(TipoEvento.PERIODOS, this);
    }

    private void configurarComboEstado() {
        cmbEstadoPeriodo.getItems().clear();
        cmbEstadoPeriodo.getItems().add("Seleccione un estado");
        cmbEstadoPeriodo.getItems().addAll("Activo", "Inactivo", "Culminado", "Prorrogado");
        cmbEstadoPeriodo.getSelectionModel().selectFirst();
    }

    private void configurarTabla() {
        colNumPeriodo.setCellFactory(col -> new TableCell<>() {
            @Override protected void updateItem(Integer item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty ? null : String.valueOf(getIndex() + 1));
            }
        });
        colNombrePeriodo.setCellValueFactory(new PropertyValueFactory<>("nombre"));
        colFechaInicio.setCellValueFactory(new PropertyValueFactory<>("fechaInicio"));
        colFechaFin.setCellValueFactory(new PropertyValueFactory<>("fechaFin"));
        colEstadoPeriodo.setCellValueFactory(new PropertyValueFactory<>("estado"));
        configurarAcciones();
    }

    private void configurarAcciones() {
        colAccionesPeriodo.setCellFactory(param -> new TableCell<>() {
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

    private void cargarPeriodos() {
        Platform.runLater(() -> {
            try {
                listaPeriodos = FXCollections.observableArrayList(gestionPeriodos.listarTodos());
                tablaPeriodos.setItems(listaPeriodos);
                tablaPeriodos.refresh();
            } catch (Exception e) {
                logger.error("Error al cargar periodos: " + e.getMessage());
                Dialogos.info("Error al cargar periodos.");
            }
        });
    }

    private void cargarEnFormulario(PeriodoAcademico p) {
        periodoEditando = p;
        txtNombrePeriodo.setText(p.getNombre());
        dateFechaInicio.setValue(LocalDate.parse(p.getFechaInicio()));
        dateFechaFin.setValue(LocalDate.parse(p.getFechaFin()));
        cmbEstadoPeriodo.setValue(p.getEstado());
        scrollPaneFormulario.setVisible(true);
        scrollPaneFormulario.setManaged(true);
    }

    private void eliminar(PeriodoAcademico p) {
        if (!Dialogos.confirmar("Confirmar eliminación", "¿Está seguro de eliminar este registro?")) return;
        try {
            gestionPeriodos.eliminarPeriodo(p);
            eventBus.notificar(TipoEvento.PERIODOS);
        } catch (Exception e) {
            logger.error("Error al eliminar periodo: " + e.getMessage());
            Dialogos.info(e.getMessage());
        }
    }

    public void handleNuevoPeriodo() {
        periodoEditando = null;
        txtNombrePeriodo.clear();
        dateFechaInicio.setValue(null); dateFechaFin.setValue(null);
        cmbEstadoPeriodo.getSelectionModel().selectFirst();
        scrollPaneFormulario.setVisible(true);
        scrollPaneFormulario.setManaged(true);
    }

    @FXML private void handleGuardarPeriodo() {
        String nombre = txtNombrePeriodo.getText().trim();
        LocalDate inicio = dateFechaInicio.getValue();
        LocalDate fin = dateFechaFin.getValue();
        String estado = cmbEstadoPeriodo.getValue();
        if (nombre.isBlank() || inicio == null || fin == null || estado == null) {
            Dialogos.info("Todos los campos son obligatorios."); return;
        }
        try {
            if (periodoEditando == null) gestionPeriodos.crearPeriodo(nombre, inicio.toString(), fin.toString(), estado);
            else gestionPeriodos.actualizarPeriodo(periodoEditando, nombre, inicio.toString(), fin.toString(), estado);
            eventBus.notificar(TipoEvento.PERIODOS);
            scrollPaneFormulario.setVisible(false);
            scrollPaneFormulario.setManaged(false);
        } catch (Exception e) {
            logger.error("Error al guardar periodo: " + e.getMessage());
            Dialogos.info(e.getMessage());
        }
    }

    @FXML private void handleCancelarPeriodo() {
        scrollPaneFormulario.setVisible(false);
        scrollPaneFormulario.setManaged(false);
    }
}

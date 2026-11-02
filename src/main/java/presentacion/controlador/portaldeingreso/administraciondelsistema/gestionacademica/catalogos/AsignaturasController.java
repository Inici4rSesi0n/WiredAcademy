package presentacion.controlador.portaldeingreso.administraciondelsistema.gestionacademica.catalogos;
import infraestructura.eventos.EventBus;
import dominio.puerto.externo.EventBusListener;
import aplicacion.casosdeuso.portaldeingreso.administraciondelsistema.academico.GestionAsignaturas;
import dominio.modelo.Asignatura;
import dominio.puerto.externo.LoggerPort;
import presentacion.dialogos.Dialogos;
import dominio.modelo.TipoEvento;
import java.net.URL;
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
public class AsignaturasController implements Initializable, EventBusListener {
    private final GestionAsignaturas gestionAsignaturas;
    private final LoggerPort logger;
    private final EventBus eventBus;

    @FXML private TableView<Asignatura> tablaAsignaturas;
    @FXML private TableColumn<Asignatura, Integer> colNumAsignatura;
    @FXML private TableColumn<Asignatura, String> colNombreAsignatura;
    @FXML private TableColumn<Asignatura, Void> colAccionesAsignatura;
    @FXML private VBox panelFormularioAsignatura;
    @FXML private javafx.scene.control.ScrollPane scrollPaneFormulario;
    @FXML private TextField txtNombreAsignatura;
    @FXML private Button btnNuevaAsignatura, btnGuardarAsignatura, btnCancelarAsignatura;

    private ObservableList<Asignatura> listaAsignaturas;
    private Asignatura asignaturaEditando;

    public AsignaturasController(GestionAsignaturas gestionAsignaturas, LoggerPort logger, EventBus eventBus) {
        this.gestionAsignaturas = gestionAsignaturas;
        this.logger = logger;
        this.eventBus = eventBus;
    }

    
    public void onEvent(TipoEvento evento) {
        switch (evento) {
            case ASIGNATURAS -> cargarAsignaturas();
            default -> {}
        }
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        configurarTabla();
        cargarAsignaturas();
        eventBus.suscribir(TipoEvento.ASIGNATURAS, this);
    }

    private void configurarTabla() {
        colNumAsignatura.setCellFactory(col -> new TableCell<>() {
            @Override protected void updateItem(Integer item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty ? null : String.valueOf(getIndex() + 1));
            }
        });
        colNombreAsignatura.setCellValueFactory(new PropertyValueFactory<>("nombre"));
        configurarAcciones();
    }

    private void configurarAcciones() {
        colAccionesAsignatura.setCellFactory(param -> new TableCell<>() {
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

    private void cargarAsignaturas() {
        Platform.runLater(() -> {
            try {
                listaAsignaturas = FXCollections.observableArrayList(gestionAsignaturas.listarTodos());
                tablaAsignaturas.setItems(listaAsignaturas);
                tablaAsignaturas.refresh();
            } catch (Exception e) {
                logger.error("Error al cargar asignaturas: " + e.getMessage());
                Dialogos.info("Error al cargar asignaturas.");
            }
        });
    }

    private void cargarEnFormulario(Asignatura a) {
        asignaturaEditando = a;
        txtNombreAsignatura.setText(a.getNombre());
        scrollPaneFormulario.setVisible(true);
        scrollPaneFormulario.setManaged(true);
    }

    private void eliminar(Asignatura a) {
        if (!Dialogos.confirmar("Confirmar eliminación", "¿Está seguro de eliminar este registro?")) return;
        try {
            gestionAsignaturas.eliminarAsignatura(a);
        } catch (Exception e) {
            logger.error("Error al eliminar asignatura: " + e.getMessage());
            Dialogos.info(e.getMessage());
        }
    }

    public void handleNuevaAsignatura() {
        asignaturaEditando = null;
        txtNombreAsignatura.clear();
        scrollPaneFormulario.setVisible(true);
        scrollPaneFormulario.setManaged(true);
    }

    @FXML private void handleGuardarAsignatura() {
        String nombre = txtNombreAsignatura.getText().trim();
        if (nombre.isBlank()) {
            Dialogos.info("El nombre no puede estar vacío."); return;
        }
        try {
            if (asignaturaEditando == null) gestionAsignaturas.crearAsignatura(nombre);
            else gestionAsignaturas.actualizarAsignatura(asignaturaEditando, nombre);
            scrollPaneFormulario.setVisible(false);
            scrollPaneFormulario.setManaged(false);
        } catch (Exception e) {
            logger.error("Error al guardar asignatura: " + e.getMessage());
            Dialogos.info(e.getMessage());
        }
    }

    @FXML private void handleCancelarAsignatura() {
        scrollPaneFormulario.setVisible(false);
        scrollPaneFormulario.setManaged(false);
    }
}

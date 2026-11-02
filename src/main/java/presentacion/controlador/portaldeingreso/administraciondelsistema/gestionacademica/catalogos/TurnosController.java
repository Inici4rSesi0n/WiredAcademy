package presentacion.controlador.portaldeingreso.administraciondelsistema.gestionacademica.catalogos;
import infraestructura.eventos.EventBus;
import dominio.puerto.externo.EventBusListener;
import aplicacion.casosdeuso.portaldeingreso.administraciondelsistema.academico.GestionTurnos;
import dominio.modelo.Turno;
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
public class TurnosController implements Initializable, EventBusListener {
    private final GestionTurnos gestionTurnos;
    private final LoggerPort logger;
    private final EventBus eventBus;

    @FXML private TableView<Turno> tablaTurnos;
    @FXML private TableColumn<Turno, Integer> colNumTurno;
    @FXML private TableColumn<Turno, String> colNombreTurno;
    @FXML private TableColumn<Turno, Void> colAccionesTurno;
    @FXML private VBox panelFormularioTurno;
    @FXML private javafx.scene.control.ScrollPane scrollPaneFormulario;
    @FXML private TextField txtNombreTurno;
    @FXML private Button btnNuevoTurno, btnGuardarTurno, btnCancelarTurno;

    private ObservableList<Turno> listaTurnos;
    private Turno turnoEditando;

    public TurnosController(GestionTurnos gestionTurnos, LoggerPort logger, EventBus eventBus) {
        this.gestionTurnos = gestionTurnos;
        this.logger = logger;
        this.eventBus = eventBus;
    }

    
    public void onEvent(TipoEvento evento) {
        switch (evento) {
            case TURNOS -> cargarTurnos();
            default -> {}
        }
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        configurarTabla();
        cargarTurnos();
        eventBus.suscribir(TipoEvento.TURNOS, this);
    }

    private void configurarTabla() {
        colNumTurno.setCellFactory(col -> new TableCell<>() {
            @Override protected void updateItem(Integer item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty ? null : String.valueOf(getIndex() + 1));
            }
        });
        colNombreTurno.setCellValueFactory(new PropertyValueFactory<>("nombre"));
        configurarAcciones();
    }

    private void configurarAcciones() {
        colAccionesTurno.setCellFactory(param -> new TableCell<>() {
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

    private void cargarTurnos() {
        Platform.runLater(() -> {
            try {
                listaTurnos = FXCollections.observableArrayList(gestionTurnos.listarTodos());
                tablaTurnos.setItems(listaTurnos);
                tablaTurnos.refresh();
            } catch (Exception e) {
                logger.error("Error al cargar turnos: " + e.getMessage());
                Dialogos.info("Error al cargar turnos.");
            }
        });
    }

    private void cargarEnFormulario(Turno t) {
        turnoEditando = t;
        txtNombreTurno.setText(t.getNombre());
        scrollPaneFormulario.setVisible(true);
        scrollPaneFormulario.setManaged(true);
    }

    private void eliminar(Turno t) {
        if (!Dialogos.confirmar("Confirmar eliminación", "¿Está seguro de eliminar este registro?")) return;
        try {
            gestionTurnos.eliminarTurno(t);
        } catch (Exception e) {
            logger.error("Error al eliminar turno: " + e.getMessage());
            Dialogos.info(e.getMessage());
        }
    }

    public void handleNuevoTurno() {
        turnoEditando = null;
        txtNombreTurno.clear();
        scrollPaneFormulario.setVisible(true);
        scrollPaneFormulario.setManaged(true);
    }

    @FXML private void handleGuardarTurno() {
        String nombre = txtNombreTurno.getText().trim();
        if (nombre.isBlank()) {
            Dialogos.info("El nombre no puede estar vacío."); return;
        }
        try {
            if (turnoEditando == null) gestionTurnos.crearTurno(nombre);
            else gestionTurnos.actualizarTurno(turnoEditando, nombre);
            scrollPaneFormulario.setVisible(false);
            scrollPaneFormulario.setManaged(false);
        } catch (Exception e) {
            logger.error("Error al guardar turno: " + e.getMessage());
            Dialogos.info(e.getMessage());
        }
    }

    @FXML private void handleCancelarTurno() {
        scrollPaneFormulario.setVisible(false);
        scrollPaneFormulario.setManaged(false);
    }
}

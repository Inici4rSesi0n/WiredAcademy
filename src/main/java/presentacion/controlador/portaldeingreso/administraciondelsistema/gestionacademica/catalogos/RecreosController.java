/*
 * SPDX-FileCopyrightText: 2026 inici4rsesi0n
 * SPDX-License-Identifier: GPL-3.0-or-later
 */
package presentacion.controlador.portaldeingreso.administraciondelsistema.gestionacademica.catalogos;
import infraestructura.eventos.EventBus;
import dominio.puerto.externo.EventBusListener;
import aplicacion.casosdeuso.portaldeingreso.administraciondelsistema.academico.GestionPeriodos;
import aplicacion.casosdeuso.portaldeingreso.administraciondelsistema.academico.GestionRecreos;
import dominio.modelo.FranjaHoraria;
import dominio.modelo.PeriodoAcademico;
import dominio.modelo.Recreo;
import dominio.puerto.externo.LoggerPort;
import presentacion.dialogos.Dialogos;
import dominio.modelo.TipoEvento;
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
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
/**
 *
 * @author inici4rsesi0n
 */
@Component
@Scope("prototype")
public class RecreosController implements Initializable, EventBusListener {
    private final GestionRecreos gestionRecreos;
    private final GestionPeriodos gestionPeriodos;
    private final LoggerPort logger;
    private final EventBus eventBus;
    @FXML private TableView<Recreo> tablaRecreos;
    @FXML private TableColumn<Recreo, Integer> colNumRecreo;
    @FXML private TableColumn<Recreo, String> colDiaRecreo, colInicioRecreo, colFinRecreo, colDescripcionRecreo;
    @FXML private TableColumn<Recreo, Void> colAccionesRecreo;
    @FXML private VBox panelFormularioRecreo;
    @FXML private javafx.scene.control.ScrollPane scrollPaneFormulario;
    @FXML private ComboBox<String> cmbDiaRecreo, cmbPeriodoRecreo;
    @FXML private ListView<PeriodoAcademico> listViewPeriodosRecreo;
    @FXML private TextField txtInicioRecreo, txtFinRecreo, txtDescripcionRecreo;
    @FXML private Button btnNuevoRecreo, btnGuardarRecreo, btnCancelarRecreo;
    private ObservableList<Recreo> listaRecreos;
    private ObservableList<PeriodoAcademico> periodosRecreoTemporal;
    private Recreo recreoEditando;
    public RecreosController(GestionRecreos gestionRecreos, GestionPeriodos gestionPeriodos, LoggerPort logger, EventBus eventBus) {
        this.gestionRecreos = gestionRecreos;
        this.gestionPeriodos = gestionPeriodos;
        this.logger = logger;
        this.eventBus = eventBus;
    }
    public void onEvent(TipoEvento evento) {
        switch (evento) {
            case RECREOS -> cargarRecreos();
            case PERIODOS -> recargarComboPeriodosRecreo();
            default -> {}
        }
    }
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        configurarComboDia();
        configurarTabla();
        configurarListaPeriodos();
        cargarRecreos();
        recargarComboPeriodosRecreo();
        eventBus.suscribir(TipoEvento.RECREOS, this);
        eventBus.suscribir(TipoEvento.PERIODOS, this);
    }
    private void configurarComboDia() {
        cmbDiaRecreo.getItems().clear();
        cmbDiaRecreo.getItems().add("Seleccione un día");
        cmbDiaRecreo.getItems().addAll("Lunes", "Martes", "Miércoles", "Jueves", "Viernes", "Sábado", "Domingo");
        cmbDiaRecreo.getSelectionModel().selectFirst();
    }
    private void configurarTabla() {
        colNumRecreo.setCellFactory(col -> new TableCell<>() {
            @Override protected void updateItem(Integer item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty ? null : String.valueOf(getIndex() + 1));
            }
        });
        colDiaRecreo.setCellValueFactory(new PropertyValueFactory<>("diaSemana"));
        colInicioRecreo.setCellValueFactory(new PropertyValueFactory<>("horaInicio"));
        colFinRecreo.setCellValueFactory(new PropertyValueFactory<>("horaFin"));
        colDescripcionRecreo.setCellValueFactory(cell -> new javafx.beans.property.SimpleStringProperty(cell.getValue().getDescripcion()));
        configurarAcciones();
    }
    private void configurarAcciones() {
        colAccionesRecreo.setCellFactory(param -> new TableCell<>() {
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
    private void configurarListaPeriodos() {
        periodosRecreoTemporal = FXCollections.observableArrayList();
        listViewPeriodosRecreo.setItems(periodosRecreoTemporal);
        // Placeholder bonito para cuando no hay periodos
        Label placeholder = new Label("Sin periodos asociados. Use '+ Añadir'.");
        placeholder.setStyle("-fx-text-fill: #64748B; -fx-font-style: italic; -fx-font-size: 12px;");
        listViewPeriodosRecreo.setPlaceholder(placeholder);
        listViewPeriodosRecreo.setCellFactory(lv -> new ListCell<>() {
            @Override
            protected void updateItem(PeriodoAcademico item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                    setGraphic(null);
                } else {
                    Label lbl = new Label(item.getNombre());
                    lbl.setStyle("-fx-font-size: 13px; -fx-text-fill: #F8FAFC;");
                    Button btnQuitar = new Button("✕");
                    btnQuitar.getStyleClass().add("boton-tabla-eliminar");
                    btnQuitar.setStyle("-fx-font-size: 10px; -fx-padding: 2px 8px; -fx-cursor: hand;");
                    btnQuitar.setOnAction(e -> periodosRecreoTemporal.remove(item));
                    HBox hbox = new HBox(10);
                    hbox.setAlignment(javafx.geometry.Pos.CENTER_LEFT);
                    hbox.setMaxWidth(Double.MAX_VALUE);
                    Region spacer = new Region();
                    HBox.setHgrow(spacer, Priority.ALWAYS);
                    hbox.getChildren().addAll(lbl, spacer, btnQuitar);
                    hbox.prefWidthProperty().bind(listViewPeriodosRecreo.widthProperty().subtract(35));
                    setGraphic(hbox);
                    setStyle("-fx-padding: 4px 8px;");
                }
            }
        });
    }
    private void recargarComboPeriodosRecreo() {
        cmbPeriodoRecreo.getItems().clear();
        cmbPeriodoRecreo.getItems().add("Seleccione un periodo");
        for (PeriodoAcademico p : gestionPeriodos.listarTodos()) {
            cmbPeriodoRecreo.getItems().add(p.getNombre());
        }
        cmbPeriodoRecreo.getSelectionModel().selectFirst();
    }
    private void cargarRecreos() {
        Platform.runLater(() -> {
            try {
                listaRecreos = FXCollections.observableArrayList(gestionRecreos.listarTodos());
                tablaRecreos.setItems(listaRecreos);
                tablaRecreos.refresh();
            } catch (Exception e) {
                logger.error("Error al cargar recreos: " + e.getMessage());
                Dialogos.info("Error al cargar recreos.");
            }
        });
    }
    private void cargarEnFormulario(Recreo r) {
        recreoEditando = r;
        recargarComboPeriodosRecreo();
        periodosRecreoTemporal.setAll(r.getPeriodos());
        cmbDiaRecreo.setValue(r.getDiaSemana());
        txtInicioRecreo.setText(r.getHoraInicio());
        txtFinRecreo.setText(r.getHoraFin());
        txtDescripcionRecreo.setText(r.getDescripcion());
        scrollPaneFormulario.setVisible(true);
        scrollPaneFormulario.setManaged(true);
    }
    private void eliminar(Recreo r) {
        if (!Dialogos.confirmar("Confirmar eliminación", "¿Está seguro de eliminar este registro?")) return;
        try {
            gestionRecreos.eliminarRecreo(r);
        } catch (Exception e) {
            logger.error("Error al eliminar recreo: " + e.getMessage());
            Dialogos.info(e.getMessage());
        }
    }
    public void handleNuevoRecreo() {
        recreoEditando = null;
        periodosRecreoTemporal.clear();
        recargarComboPeriodosRecreo();
        cmbDiaRecreo.getSelectionModel().selectFirst();
        txtInicioRecreo.clear();
        txtFinRecreo.clear();
        txtDescripcionRecreo.clear();
        scrollPaneFormulario.setVisible(true);
        scrollPaneFormulario.setManaged(true);
    }
    @FXML private void handleGuardarRecreo() {
        String dia = cmbDiaRecreo.getValue();
        String inicio = txtInicioRecreo.getText().trim();
        String fin = txtFinRecreo.getText().trim();
        String desc = txtDescripcionRecreo.getText().trim();
        if (dia == null || inicio.isBlank() || fin.isBlank()) {
            Dialogos.info("Todos los campos son obligatorios."); return;
        }
        if (periodosRecreoTemporal.isEmpty()) {
            Dialogos.info("Debe asociar al menos un periodo al recreo."); return;
        }
        if (!inicio.matches("([01]\\d|2[0-3]):[0-5]\\d") || !fin.matches("([01]\\d|2[0-3]):[0-5]\\d")) {
            Dialogos.info("Formato de hora inválido. Use HH:mm (ej. 08:00, 14:30)."); return;
        }
        FranjaHoraria franja = new FranjaHoraria(dia, inicio, fin);
        List<PeriodoAcademico> periodos = new ArrayList<>(periodosRecreoTemporal);
        try {
            if (recreoEditando == null) {
                gestionRecreos.crearRecreo(franja, desc.isBlank() ? "Recreo" : desc, periodos);
            } else {
                gestionRecreos.actualizarRecreo(recreoEditando, franja, desc.isBlank() ? "Recreo" : desc, periodos);
            }
            scrollPaneFormulario.setVisible(false);
            scrollPaneFormulario.setManaged(false);
        } catch (Exception e) {
            logger.error("Error al guardar recreo: " + e.getMessage());
            Dialogos.info(e.getMessage());
        }
    }
    @FXML private void handleCancelarRecreo() {
        scrollPaneFormulario.setVisible(false);
        scrollPaneFormulario.setManaged(false);
    }
    @FXML private void handleAgregarPeriodoRecreo() {
        String nombrePeriodo = cmbPeriodoRecreo.getValue();
        if (nombrePeriodo == null || nombrePeriodo.startsWith("Seleccione")) return;
        PeriodoAcademico periodo = gestionPeriodos.buscarPorNombre(nombrePeriodo);
        if (periodo == null) {
            Dialogos.info("Periodo no encontrado.");
            return;
        }
        if (periodosRecreoTemporal.contains(periodo)) {
            Dialogos.info("El periodo ya está asociado.");
            return;
        }
        periodosRecreoTemporal.add(periodo);
    }
}

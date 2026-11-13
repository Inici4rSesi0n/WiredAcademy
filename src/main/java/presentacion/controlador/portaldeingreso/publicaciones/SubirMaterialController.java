/*
 * SPDX-FileCopyrightText: 2026 inici4rsesi0n
 * SPDX-License-Identifier: GPL-3.0-or-later
 */
package presentacion.controlador.portaldeingreso.publicaciones;
import aplicacion.casosdeuso.portaldeingreso.administraciondelsistema.academico.GestionAsignaturas;
import aplicacion.casosdeuso.portaldeingreso.publicaciones.GestionPublicaciones;
import dominio.modelo.*;
import dominio.puerto.externo.EventBusListener;
import dominio.puerto.externo.LoggerPort;
import infraestructura.eventos.EventBus;
import presentacion.dialogos.Dialogos;
import presentacion.estadousuario.SesionUsuario;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import java.io.File;
import java.net.URL;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ResourceBundle;
@Component
@Scope("prototype")
public class SubirMaterialController implements Initializable, EventBusListener {
    private final GestionPublicaciones gestionPublicaciones;
    private final GestionAsignaturas gestionAsignaturas;
    private final LoggerPort logger;
    private final EventBus eventBus;
    @FXML private TableView<Publicacion> tablaPublicaciones;
    @FXML private TableColumn<Publicacion, String> colTitulo, colTipo, colAsignatura;
    @FXML private TableColumn<Publicacion, Integer> colSemana;
    @FXML private TableColumn<Publicacion, Void> colAcciones;
    @FXML private ScrollPane scrollPaneFormulario;
    @FXML private ComboBox<TipoPublicacion> cmbTipo;
    @FXML private TextField txtTitulo, txtSemana;
    @FXML private TextArea txtDescripcion;
    @FXML private ComboBox<String> cmbAsignatura;
    @FXML private DatePicker dateFechaLimite;
    @FXML private VBox vboxAsignatura, vboxSemana, vboxFechaLimite;
    @FXML private Button btnNuevaPublicacion, btnGuardar, btnCancelar, btnAdjuntar;
    @FXML private Label lblArchivoSeleccionado;
    private File archivoSeleccionado;
    private ObservableList<Publicacion> listaPublicaciones;
    public SubirMaterialController(GestionPublicaciones gestionPublicaciones, GestionAsignaturas gestionAsignaturas, LoggerPort logger, EventBus eventBus) {
        this.gestionPublicaciones = gestionPublicaciones;
        this.gestionAsignaturas = gestionAsignaturas;
        this.logger = logger;
        this.eventBus = eventBus;
    }
    public void onEvent(TipoEvento evento) {
        if (evento == TipoEvento.PUBLICACIONES) cargarPublicaciones();
    }
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        configurarTabla();
        configurarCombos();
        cargarPublicaciones();
        eventBus.suscribir(TipoEvento.PUBLICACIONES, this);
        handleTipoCambiado();
    }
    private void configurarCombos() {
        cmbTipo.getItems().addAll(TipoPublicacion.values());
        cmbTipo.getSelectionModel().selectFirst();
        cmbAsignatura.getItems().clear();
        for (Asignatura a : gestionAsignaturas.listarTodos()) cmbAsignatura.getItems().add(a.getNombre());
        cmbAsignatura.getSelectionModel().selectFirst();
    }
    private void configurarTabla() {
        colTitulo.setCellValueFactory(new PropertyValueFactory<>("titulo"));
        colTipo.setCellValueFactory(cell -> new javafx.beans.property.SimpleStringProperty(cell.getValue().getTipo().name()));
        colAsignatura.setCellValueFactory(cell -> new javafx.beans.property.SimpleStringProperty(
                cell.getValue().getAsignatura() != null ? cell.getValue().getAsignatura().getNombre() : "Global"));
        colSemana.setCellValueFactory(new PropertyValueFactory<>("numeroSemana"));
        colAcciones.setCellFactory(param -> new TableCell<>() {
            private final Button btnEliminar = new Button("Eliminar");
            {
                btnEliminar.getStyleClass().add("boton-tabla-eliminar");
                btnEliminar.setOnAction(e -> {
                    Publicacion p = getTableView().getItems().get(getIndex());
                    if (Dialogos.confirmar("Eliminar", "¿Borrar publicación?")) gestionPublicaciones.eliminarPublicacion(p);
                });
            }
            @Override protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : new HBox(5, btnEliminar));
            }
        });
    }
    @FXML private void handleTipoCambiado() {
        TipoPublicacion tipo = cmbTipo.getValue();
        if (tipo == null) return;
        boolean esGlobal = tipo == TipoPublicacion.ANUNCIO;
        vboxAsignatura.setVisible(!esGlobal);
        vboxAsignatura.setManaged(!esGlobal);
        boolean pideSemana = tipo == TipoPublicacion.MATERIAL;
        vboxSemana.setVisible(pideSemana);
        vboxSemana.setManaged(pideSemana);
        boolean pideFecha = tipo == TipoPublicacion.TAREA || tipo == TipoPublicacion.EVALUACION;
        vboxFechaLimite.setVisible(pideFecha);
        vboxFechaLimite.setManaged(pideFecha);
    }
    private void cargarPublicaciones() {
        Platform.runLater(() -> {
            listaPublicaciones = FXCollections.observableArrayList(gestionPublicaciones.listarTodos());
            tablaPublicaciones.setItems(listaPublicaciones);
            tablaPublicaciones.refresh();
        });
    }
    @FXML private void handleNuevaPublicacion() {
        scrollPaneFormulario.setVisible(true);
        scrollPaneFormulario.setManaged(true);
        limpiarFormulario();
    }
    @FXML private void handleAdjuntarArchivo() {
        FileChooser fc = new FileChooser();
        archivoSeleccionado = fc.showOpenDialog(tablaPublicaciones.getScene().getWindow());
        if (archivoSeleccionado != null) lblArchivoSeleccionado.setText(archivoSeleccionado.getName());
    }
    @FXML private void handleGuardar() {
        TipoPublicacion tipo = cmbTipo.getValue();
        String titulo = txtTitulo.getText().trim();
        String desc = txtDescripcion.getText().trim();
        if (titulo.isBlank() || tipo == null) { Dialogos.info("Título y tipo son obligatorios."); return; }
        Asignatura asig = tipo == TipoPublicacion.ANUNCIO ? null : gestionAsignaturas.buscarAsignatura(cmbAsignatura.getValue());
        Integer semana = txtSemana.getText().isBlank() ? null : Integer.parseInt(txtSemana.getText());
        LocalDateTime fechaLimite = dateFechaLimite.getValue() != null ? dateFechaLimite.getValue().atTime(LocalTime.MAX) : null;
        String url = archivoSeleccionado != null ? archivoSeleccionado.getAbsolutePath() : null;
        Usuario autor = SesionUsuario.getUsuarioActual();
        try {
            gestionPublicaciones.crearPublicacion(titulo, desc, tipo, semana, fechaLimite, url, asig, autor);
            scrollPaneFormulario.setVisible(false);
            scrollPaneFormulario.setManaged(false);
        } catch (Exception e) {
            logger.error("Error al publicar: " + e.getMessage());
            Dialogos.info(e.getMessage());
        }
    }
    @FXML private void handleCancelar() {
        scrollPaneFormulario.setVisible(false);
        scrollPaneFormulario.setManaged(false);
    }
    private void limpiarFormulario() {
        txtTitulo.clear();
        txtDescripcion.clear();
        txtSemana.clear();
        dateFechaLimite.setValue(null);
        archivoSeleccionado = null;
        lblArchivoSeleccionado.setText("Ningún archivo seleccionado");
        cmbTipo.getSelectionModel().selectFirst();
        cmbAsignatura.getSelectionModel().selectFirst();
        handleTipoCambiado();
    }
}

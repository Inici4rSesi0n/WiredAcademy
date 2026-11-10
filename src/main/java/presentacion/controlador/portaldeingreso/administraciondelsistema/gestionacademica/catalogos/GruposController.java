/*
 * SPDX-FileCopyrightText: 2026 inici4rsesi0n
 * SPDX-License-Identifier: GPL-3.0-or-later
 */
package presentacion.controlador.portaldeingreso.administraciondelsistema.gestionacademica.catalogos;
import infraestructura.eventos.EventBus;
import dominio.puerto.externo.EventBusListener;
import aplicacion.casosdeuso.portaldeingreso.administraciondelsistema.academico.GestionGrados;
import aplicacion.casosdeuso.portaldeingreso.administraciondelsistema.academico.GestionGrupos;
import dominio.modelo.Grado;
import dominio.modelo.Grupo;
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
public class GruposController implements Initializable, EventBusListener {
    private final GestionGrupos gestionGrupos;
    private final GestionGrados gestionGrados;
    private final LoggerPort logger;
    private final EventBus eventBus;
    @FXML private TableView<Grupo> tablaGrupos;
    @FXML private TableColumn<Grupo, Integer> colNumGrupo;
    @FXML private TableColumn<Grupo, String> colNombreGrupo, colGradoGrupo;
    @FXML private TableColumn<Grupo, Void> colAccionesGrupo;
    @FXML private VBox panelFormularioGrupo;
    @FXML private javafx.scene.control.ScrollPane scrollPaneFormulario;
    @FXML private TextField txtNombreGrupo;
    @FXML private ComboBox<String> cmbGradoGrupo;
    @FXML private Button btnNuevoGrupo, btnGuardarGrupo, btnCancelarGrupo;
    private ObservableList<Grupo> listaGrupos;
    private Grupo grupoEditando;
    public GruposController(GestionGrupos gestionGrupos, GestionGrados gestionGrados, LoggerPort logger, EventBus eventBus) {
        this.gestionGrupos = gestionGrupos;
        this.gestionGrados = gestionGrados;
        this.logger = logger;
        this.eventBus = eventBus;
    }
    public void onEvent(TipoEvento evento) {
        switch (evento) {
            case GRUPOS -> cargarGrupos();
            case GRADOS -> recargarComboGrados();
            default -> {}
        }
    }
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        configurarTabla();
        recargarComboGrados();
        cargarGrupos();
        eventBus.suscribir(TipoEvento.GRUPOS, this);
        eventBus.suscribir(TipoEvento.GRADOS, this);
    }
    private void configurarTabla() {
        colNumGrupo.setCellFactory(col -> new TableCell<>() {
            @Override protected void updateItem(Integer item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty ? null : String.valueOf(getIndex() + 1));
            }
        });
        colNombreGrupo.setCellValueFactory(new PropertyValueFactory<>("nombre"));
        colGradoGrupo.setCellValueFactory(cell -> new javafx.beans.property.SimpleStringProperty(
                cell.getValue().getGrado() != null ? cell.getValue().getGrado().getNombre() : ""));
        configurarAcciones();
    }
    private void configurarAcciones() {
        colAccionesGrupo.setCellFactory(param -> new TableCell<>() {
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
    private void cargarGrupos() {
        Platform.runLater(() -> {
            try {
                listaGrupos = FXCollections.observableArrayList(gestionGrupos.listarTodos());
                tablaGrupos.setItems(listaGrupos);
                tablaGrupos.refresh();
            } catch (Exception e) {
                logger.error("Error al cargar grupos: " + e.getMessage());
                Dialogos.info("Error al cargar grupos.");
            }
        });
    }
    private void recargarComboGrados() {
        cmbGradoGrupo.getItems().clear();
        cmbGradoGrupo.getItems().add("Seleccione un grado");
        for (Grado g : gestionGrados.listarTodos()) {
            cmbGradoGrupo.getItems().add(formatearGrado(g));
        }
        cmbGradoGrupo.getSelectionModel().selectFirst();
    }
    private void cargarEnFormulario(Grupo g) {
        grupoEditando = g;
        recargarComboGrados();
        txtNombreGrupo.setText(g.getNombre());
        cmbGradoGrupo.setValue(g.getGrado() != null ? formatearGrado(g.getGrado()) : null);
        scrollPaneFormulario.setVisible(true);
        scrollPaneFormulario.setManaged(true);
    }
    private void eliminar(Grupo g) {
        if (!Dialogos.confirmar("Confirmar eliminación", "¿Está seguro de eliminar este registro?")) return;
        try {
            gestionGrupos.eliminarGrupo(g);
        } catch (Exception e) {
            logger.error("Error al eliminar grupo: " + e.getMessage());
            Dialogos.info(e.getMessage());
        }
    }
    public void handleNuevoGrupo() {
        grupoEditando = null;
        recargarComboGrados();
        txtNombreGrupo.clear();
        scrollPaneFormulario.setVisible(true);
        scrollPaneFormulario.setManaged(true);
    }
    @FXML private void handleGuardarGrupo() {
        String nombre = txtNombreGrupo.getText().trim();
        String gradoNombre = cmbGradoGrupo.getValue();
        if (nombre.isBlank() || gradoNombre == null || gradoNombre.startsWith("Seleccione")) {
            Dialogos.info("Todos los campos son obligatorios."); return;
        }
        Grado grado = parsearGrado(gradoNombre);
        if (grado == null) {
            Dialogos.info("El grado seleccionado no existe."); return;
        }
        try {
            if (grupoEditando == null) gestionGrupos.crearGrupo(nombre, grado);
            else gestionGrupos.actualizarGrupo(grupoEditando, nombre, grado);
            scrollPaneFormulario.setVisible(false);
            scrollPaneFormulario.setManaged(false);
        } catch (Exception e) {
            logger.error("Error al guardar grupo: " + e.getMessage());
            Dialogos.info(e.getMessage());
        }
    }
    @FXML private void handleCancelarGrupo() {
        scrollPaneFormulario.setVisible(false);
        scrollPaneFormulario.setManaged(false);
    }
    private String formatearGrado(Grado g) {
        if (g == null) return "";
        String nivel = (g.getNivel() != null) ? g.getNivel() : "Sin nivel";
        return g.getNombre() + " (" + nivel + ")";
    }
    private Grado parsearGrado(String texto) {
        if (texto == null) return null;
        int idx = texto.lastIndexOf(" (");
        if (idx < 0) return gestionGrados.buscarPorNombre(texto);
        String nombre = texto.substring(0, idx).trim();
        String nivel = texto.substring(idx + 2, texto.length() - 1).trim();
        return gestionGrados.buscarPorNombreYNivel(nombre, nivel);
    }
}

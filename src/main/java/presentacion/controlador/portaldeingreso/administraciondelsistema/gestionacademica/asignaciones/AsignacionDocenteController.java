package presentacion.controlador.portaldeingreso.administraciondelsistema.gestionacademica.asignaciones;
import presentacion.eventos.EventBus;
import presentacion.eventos.EventBusListener;
import presentacion.dto.AsignacionItem;

import aplicacion.casosdeuso.portaldeingreso.administraciondelsistema.academico.GestionAsignaturas;
import aplicacion.casosdeuso.portaldeingreso.administraciondelsistema.usuarios.GestionDocentes;
import dominio.modelo.Asignatura;
import dominio.modelo.Docente;
import dominio.modelo.Grupo;
import dominio.puerto.externo.LoggerPort;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import presentacion.dialogos.Dialogos;
import presentacion.eventos.TipoEvento;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Component
@Scope("prototype")
public class AsignacionDocenteController implements Initializable, EventBusListener {
    private final GestionDocentes gestionDocentes;
    private final GestionAsignaturas gestionAsignaturas;
    private final LoggerPort logger;
    private final EventBus eventBus;

    @FXML private BorderPane root;
    public BorderPane getRoot() { return root; }

    @FXML private TableView<AsignacionItem> tablaAsignaturasDocente;
    @FXML private TableColumn<AsignacionItem, String> colCodigoDocenteAsignatura, colNombreDocenteAsignatura, colAsignaturaDocente;
    @FXML private TableColumn<AsignacionItem, Void> colAccionesAsignaturaDocente;
    @FXML private ComboBox<String> cmbDocenteAsignatura, cmbAsignaturaDocente;

    private ObservableList<AsignacionItem> listaAsignacionesDocente;

    public AsignacionDocenteController(GestionDocentes gestionDocentes, GestionAsignaturas gestionAsignaturas, LoggerPort logger, EventBus eventBus) {
        this.gestionDocentes = gestionDocentes;
        this.gestionAsignaturas = gestionAsignaturas;
        this.logger = logger;
        this.eventBus = eventBus;
    }

    
    public void onEvent(TipoEvento evento) {
        switch (evento) {
            case DOCENTES -> recargarDesdeEvento();
            case ASIGNATURAS -> recargarDesdeEvento();
            default -> {}
        }
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        configurarTablaAsignacionesDocente();
        recargarCombosAsignaciones();
        cargarAsignacionesDocente();
        eventBus.suscribir(TipoEvento.DOCENTES, this);
        eventBus.suscribir(TipoEvento.ASIGNATURAS, this);
    }

    private void recargarDesdeEvento() {
        recargarCombosAsignaciones();
        cargarAsignacionesDocente();
    }

    private void recargarCombosAsignaciones() {
        cmbDocenteAsignatura.getItems().clear(); cmbDocenteAsignatura.getItems().add("Seleccione un docente");
        for (Docente d : gestionDocentes.listarTodos()) cmbDocenteAsignatura.getItems().add(d.getCodigo() + " - " + d.getNombreCompleto());
        cmbDocenteAsignatura.getSelectionModel().selectFirst();

        cmbAsignaturaDocente.getItems().clear(); cmbAsignaturaDocente.getItems().add("Seleccione una asignatura");
        for (Asignatura a : gestionAsignaturas.listarTodos()) cmbAsignaturaDocente.getItems().add(a.getNombre());
        cmbAsignaturaDocente.getSelectionModel().selectFirst();
    }

    private void configurarTablaAsignacionesDocente() {
        colCodigoDocenteAsignatura.setCellValueFactory(new PropertyValueFactory<>("codigoDocente"));
        colNombreDocenteAsignatura.setCellValueFactory(new PropertyValueFactory<>("nombreDocente"));
        colAsignaturaDocente.setCellValueFactory(new PropertyValueFactory<>("nombreAsignatura"));
        configurarAccionesAsignaturaDocente();
    }

    private void configurarAccionesAsignaturaDocente() {
        colAccionesAsignaturaDocente.setCellFactory(param -> new TableCell<>() {
            private final Button btnQuitar = new Button("Quitar");
            {
                btnQuitar.getStyleClass().add("boton-tabla-eliminar");
                btnQuitar.setOnAction(e -> {
                    AsignacionItem item = getTableView().getItems().get(getIndex());
                    Docente docente = gestionDocentes.buscarPorCodigo(item.getCodigoDocente());
                    if (docente != null) {
                        try {
                            gestionDocentes.removerAsignatura(docente, item.getAsignatura());
                            eventBus.notificar(TipoEvento.DOCENTES);
                        } catch (Exception ex) {
                            logger.error("Error al remover asignatura: " + ex.getMessage());
                            Dialogos.info(ex.getMessage());
                        }
                    }
                });
            }
            @Override protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : new HBox(10, btnQuitar));
            }
        });
    }

    private void cargarAsignacionesDocente() {
        try {
            List<AsignacionItem> items = new ArrayList<>();
            for (Docente d : gestionDocentes.listarTodos()) {
                for (Asignatura a : d.getAsignaturas()) items.add(new AsignacionItem(d.getCodigo(), d.getNombreCompleto(), a.getNombre(), a));
            }
            listaAsignacionesDocente = FXCollections.observableArrayList(items);
            tablaAsignaturasDocente.setItems(listaAsignacionesDocente);
            tablaAsignaturasDocente.refresh();
        } catch (Exception e) {
            logger.error("Error al cargar asignaciones docente: " + e.getMessage());
        }
    }

    @FXML 
    private void handleAgregarAsignaturaDocente() {
        String docenteSel = cmbDocenteAsignatura.getValue();
        String asigSel = cmbAsignaturaDocente.getValue();
        if (docenteSel == null || docenteSel.startsWith("Seleccione") || asigSel == null || asigSel.startsWith("Seleccione")) {
            Dialogos.info("Seleccione un docente y una asignatura."); return;
        }
        String cod = docenteSel.split(" - ")[0];
        Docente docente = gestionDocentes.buscarPorCodigo(cod);
        if (docente == null) { Dialogos.info("Docente no encontrado."); return; }
        Asignatura a = gestionAsignaturas.buscarAsignatura(asigSel);
        if (a == null) { Dialogos.info("Asignatura no encontrada."); return; }
        try {
            gestionDocentes.agregarAsignatura(docente, a);
            eventBus.notificar(TipoEvento.DOCENTES);
        } catch (Exception e) {
            logger.error("Error al agregar asignatura a docente: " + e.getMessage());
            Dialogos.info(e.getMessage());
        }
    }
}

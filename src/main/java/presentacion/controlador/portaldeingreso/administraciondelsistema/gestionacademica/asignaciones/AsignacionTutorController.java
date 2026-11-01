package presentacion.controlador.portaldeingreso.administraciondelsistema.gestionacademica.asignaciones;
import presentacion.eventos.EventBus;
import presentacion.eventos.EventBusListener;
import presentacion.dto.TutorItem;

import aplicacion.casosdeuso.portaldeingreso.administraciondelsistema.academico.GestionGrupos;
import aplicacion.casosdeuso.portaldeingreso.administraciondelsistema.usuarios.GestionDocentes;
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
public class AsignacionTutorController implements Initializable, EventBusListener {
    private final GestionDocentes gestionDocentes;
    private final GestionGrupos gestionGrupos;
    private final LoggerPort logger;
    private final EventBus eventBus;

    @FXML private BorderPane root;
    public BorderPane getRoot() { return root; }

    @FXML private ComboBox<String> cmbDocenteTutor, cmbGrupoTutor;
    @FXML private TableView<TutorItem> tablaTutores;
    @FXML private TableColumn<TutorItem, String> colGrupoTutor, colDocenteTutor;
    @FXML private TableColumn<TutorItem, Void> colAccionesTutor;

    public AsignacionTutorController(GestionDocentes gestionDocentes, GestionGrupos gestionGrupos, LoggerPort logger, EventBus eventBus) {
        this.gestionDocentes = gestionDocentes;
        this.gestionGrupos = gestionGrupos;
        this.logger = logger;
        this.eventBus = eventBus;
    }

    
    public void onEvent(TipoEvento evento) {
        switch (evento) {
            case GRUPOS -> recargarDesdeEvento();
            case DOCENTES -> recargarDesdeEvento();
            default -> {}
        }
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        configurarTabla();
        recargarCombos();
        cargarTutores();
        eventBus.suscribir(TipoEvento.GRUPOS, this);
        eventBus.suscribir(TipoEvento.DOCENTES, this);
    }

    private void recargarDesdeEvento() {
        recargarCombos();
        cargarTutores();
    }

    private void recargarCombos() {
        cmbDocenteTutor.getItems().clear(); cmbDocenteTutor.getItems().add("Seleccione un docente");
        for (Docente d : gestionDocentes.listarTodos()) cmbDocenteTutor.getItems().add(d.getCodigo() + " - " + d.getNombreCompleto());
        cmbDocenteTutor.getSelectionModel().selectFirst();

        cmbGrupoTutor.getItems().clear(); cmbGrupoTutor.getItems().add("Seleccione un grupo");
        for (Grupo g : gestionGrupos.listarTodos()) cmbGrupoTutor.getItems().add(g.getNombre() + " (" + g.getGrado().getNombre() + ")");
        cmbGrupoTutor.getSelectionModel().selectFirst();
    }

    private void configurarTabla() {
        colGrupoTutor.setCellValueFactory(new PropertyValueFactory<>("nombreGrupo"));
        colDocenteTutor.setCellValueFactory(new PropertyValueFactory<>("nombreTutor"));
        colAccionesTutor.setCellFactory(param -> new TableCell<>() {
            private final Button btnQuitar = new Button("Quitar");
            {
                btnQuitar.getStyleClass().add("boton-tabla-eliminar");
                btnQuitar.setOnAction(e -> {
                    TutorItem item = getTableView().getItems().get(getIndex());
                    try {
                        gestionGrupos.removerTutor(item.getGrupo());
                        eventBus.notificar(TipoEvento.GRUPOS, TipoEvento.DOCENTES);
                    } catch (Exception ex) {
                        logger.error("Error al remover tutor: " + ex.getMessage());
                        Dialogos.info(ex.getMessage());
                    }
                });
            }
            @Override protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : new HBox(10, btnQuitar));
            }
        });
    }

    private void cargarTutores() {
        try {
            List<TutorItem> items = new ArrayList<>();
            for (Grupo g : gestionGrupos.listarTodos()) {
                if (g.getTutor() != null) items.add(new TutorItem(g));
            }
            ObservableList<TutorItem> lista = FXCollections.observableArrayList(items);
            tablaTutores.setItems(lista);
            tablaTutores.refresh();
        } catch (Exception e) {
            logger.error("Error al cargar tutores: " + e.getMessage());
        }
    }

    @FXML
    private void handleAsignarTutor() {
        String docenteSel = cmbDocenteTutor.getValue();
        String grupoSel = cmbGrupoTutor.getValue();
        if (docenteSel == null || docenteSel.startsWith("Seleccione") || grupoSel == null || grupoSel.startsWith("Seleccione")) {
            Dialogos.info("Seleccione un docente y un grupo."); return;
        }
        String cod = docenteSel.split(" - ")[0];
        Docente docente = gestionDocentes.buscarPorCodigo(cod);
        if (docente == null) { Dialogos.info("Docente no encontrado."); return; }
        String nombreGrupo = grupoSel.substring(0, grupoSel.lastIndexOf(" (")).trim();
        Grupo grupo = gestionGrupos.buscarPorNombre(nombreGrupo);
        if (grupo == null) { Dialogos.info("Grupo no encontrado."); return; }
        try {
            gestionGrupos.asignarTutor(grupo, docente);
            eventBus.notificar(TipoEvento.GRUPOS, TipoEvento.DOCENTES);
        } catch (Exception e) {
            logger.error("Error al asignar tutor: " + e.getMessage());
            Dialogos.info(e.getMessage());
        }
    }
}

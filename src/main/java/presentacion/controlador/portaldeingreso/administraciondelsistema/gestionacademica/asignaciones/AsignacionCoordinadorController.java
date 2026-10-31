package presentacion.controlador.portaldeingreso.administraciondelsistema.gestionacademica.asignaciones;
import presentacion.dto.CoordinacionItem;

import aplicacion.casosdeuso.portaldeingreso.administraciondelsistema.academico.GestionGrados;
import aplicacion.casosdeuso.portaldeingreso.administraciondelsistema.usuarios.GestionUsuarios;
import dominio.modelo.CoordinadorAcademico;
import dominio.modelo.Grado;
import dominio.modelo.Usuario;
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
import presentacion.eventos.SistemaEventBus;
import presentacion.eventos.TipoEvento;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Component
@Scope("prototype")
public class AsignacionCoordinadorController implements Initializable {
    private final GestionUsuarios gestionUsuarios;
    private final GestionGrados gestionGrados;
    private final LoggerPort logger;

    @FXML private BorderPane root;
    public BorderPane getRoot() { return root; }

    @FXML private ComboBox<String> cmbCoordinador, cmbGradoCoordinacion;
    @FXML private TableView<CoordinacionItem> tablaCoordinaciones;
    @FXML private TableColumn<CoordinacionItem, String> colCoordinadorNombre, colGradoCoordinado;
    @FXML private TableColumn<CoordinacionItem, Void> colAccionesCoordinacion;

    public AsignacionCoordinadorController(GestionUsuarios gestionUsuarios, GestionGrados gestionGrados, LoggerPort logger) {
        this.gestionUsuarios = gestionUsuarios;
        this.gestionGrados = gestionGrados;
        this.logger = logger;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        configurarTabla();
        recargarCombos();
        cargarCoordinaciones();
        SistemaEventBus.suscribir(TipoEvento.USUARIOS, this::recargarDesdeEvento);
        SistemaEventBus.suscribir(TipoEvento.GRADOS, this::recargarDesdeEvento);
    }

    private void recargarDesdeEvento() {
        recargarCombos();
        cargarCoordinaciones();
    }

    private void recargarCombos() {
        cmbCoordinador.getItems().clear(); cmbCoordinador.getItems().add("Seleccione un coordinador");
        for (Usuario u : gestionUsuarios.listarTodos()) {
            if (u instanceof CoordinadorAcademico) cmbCoordinador.getItems().add(u.getCodigo() + " - " + u.getNombre() + " " + u.getApellido());
        }
        cmbCoordinador.getSelectionModel().selectFirst();

        cmbGradoCoordinacion.getItems().clear(); cmbGradoCoordinacion.getItems().add("Seleccione un grado");
        for (Grado g : gestionGrados.listarTodos()) cmbGradoCoordinacion.getItems().add(g.getNombre() + " (" + g.getNivel() + ")");
        cmbGradoCoordinacion.getSelectionModel().selectFirst();
    }

    private void configurarTabla() {
        colCoordinadorNombre.setCellValueFactory(new PropertyValueFactory<>("nombreCoordinador"));
        colGradoCoordinado.setCellValueFactory(new PropertyValueFactory<>("nombreGrado"));
        colAccionesCoordinacion.setCellFactory(param -> new TableCell<>() {
            private final Button btnQuitar = new Button("Quitar");
            {
                btnQuitar.getStyleClass().add("boton-tabla-eliminar");
                btnQuitar.setOnAction(e -> {
                    CoordinacionItem item = getTableView().getItems().get(getIndex());
                    try {
                        item.getCoordinador().removerGrado(item.getGrado());
                        gestionUsuarios.actualizarUsuario(item.getCoordinador());
                        SistemaEventBus.notificar(TipoEvento.USUARIOS, TipoEvento.GRADOS);
                    } catch (Exception ex) {
                        logger.error("Error al remover coordinación: " + ex.getMessage());
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

    private void cargarCoordinaciones() {
        try {
            List<CoordinacionItem> items = new ArrayList<>();
            for (Usuario u : gestionUsuarios.listarTodos()) {
                if (u instanceof CoordinadorAcademico coord) {
                    for (Grado g : coord.getGradosSupervisados()) items.add(new CoordinacionItem(coord, g));
                }
            }
            ObservableList<CoordinacionItem> lista = FXCollections.observableArrayList(items);
            tablaCoordinaciones.setItems(lista);
            tablaCoordinaciones.refresh();
        } catch (Exception e) {
            logger.error("Error al cargar coordinaciones: " + e.getMessage());
        }
    }

    @FXML
    private void handleAsignarCoordinacion() {
        String coordSel = cmbCoordinador.getValue();
        String gradoSel = cmbGradoCoordinacion.getValue();
        if (coordSel == null || coordSel.startsWith("Seleccione") || gradoSel == null || gradoSel.startsWith("Seleccione")) {
            Dialogos.info("Seleccione un coordinador y un grado."); return;
        }
        String cod = coordSel.split(" - ")[0];
        Usuario u = gestionUsuarios.buscarPorCodigo(cod);
        if (!(u instanceof CoordinadorAcademico coord)) { Dialogos.info("El usuario no es un coordinador."); return; }
        Grado g = parsearGrado(gradoSel);
        if (g == null) { Dialogos.info("Grado no encontrado."); return; }
        try {
            coord.agregarGrado(g);
            gestionUsuarios.actualizarUsuario(coord);
            SistemaEventBus.notificar(TipoEvento.USUARIOS, TipoEvento.GRADOS);
        } catch (Exception e) {
            logger.error("Error al asignar coordinación: " + e.getMessage());
            Dialogos.info(e.getMessage());
        }
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

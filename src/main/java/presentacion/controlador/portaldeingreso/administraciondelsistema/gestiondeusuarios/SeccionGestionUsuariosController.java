package presentacion.controlador.portaldeingreso.administraciondelsistema.gestiondeusuarios;
import presentacion.eventos.EventBus;
import presentacion.eventos.EventBusListener;
import aplicacion.casosdeuso.portaldeingreso.administraciondelsistema.usuarios.GestionUsuarios;
import aplicacion.dto.CrearUsuarioCommand;
import dominio.modelo.Usuario;
import dominio.puerto.externo.HashProvider;
import dominio.puerto.externo.LoggerPort;
import presentacion.dialogos.Dialogos;
import presentacion.eventos.TipoEvento;
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
public class SeccionGestionUsuariosController implements Initializable, EventBusListener {
    private final GestionUsuarios gestionUsuarios;
    private final HashProvider hashProvider;
    private final LoggerPort logger;
    private final EventBus eventBus;
    @FXML private TableView<Usuario> tablaUsuarios;
    @FXML private TableColumn<Usuario, String> colCodigo, colNombre, colApellido, colDni, colRol;
    @FXML private TableColumn<Usuario, Void> colAcciones;
    @FXML private VBox panelFormulario;
    @FXML private ComboBox<String> cmbRol;
    @FXML private TextField txtCodigo, txtNombre, txtApellido, txtDni, txtEdad, txtContrasena;
    @FXML private Button btnNuevoUsuario, btnGuardar, btnCancelar;
    private ObservableList<Usuario> listaUsuarios;
    private Usuario usuarioEditando;
    public SeccionGestionUsuariosController(GestionUsuarios gestionUsuarios, HashProvider hashProvider, LoggerPort logger, EventBus eventBus) {
        this.gestionUsuarios = gestionUsuarios;
        this.hashProvider = hashProvider;
        this.logger = logger;
        this.eventBus = eventBus;
    }
    
    public void onEvent(TipoEvento evento) {
        switch (evento) {
            case USUARIOS -> cargarUsuarios();
            default -> {}
        }
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        configurarTabla();
        configurarColumnaAcciones();
        configurarComboRoles();
        eventBus.suscribir(TipoEvento.USUARIOS, this);
        cargarUsuarios();
    }
    private void configurarTabla() {
        colCodigo.setCellValueFactory(new PropertyValueFactory<>("codigo"));
        colNombre.setCellValueFactory(new PropertyValueFactory<>("nombre"));
        colApellido.setCellValueFactory(new PropertyValueFactory<>("apellido"));
        colDni.setCellValueFactory(new PropertyValueFactory<>("dni"));
        colRol.setCellValueFactory(new PropertyValueFactory<>("rol"));
    }
    private void configurarColumnaAcciones() {
        colAcciones.setCellFactory(param -> new TableCell<>() {
            private final Button btnEditar = new Button("Editar");
            private final Button btnEliminar = new Button("Eliminar");
            private final HBox hbox = new HBox(10, btnEditar, btnEliminar);
            {
                btnEditar.getStyleClass().add("boton-tabla-editar");
                btnEliminar.getStyleClass().add("boton-tabla-eliminar");
                btnEditar.setOnAction(event -> cargarUsuarioEnFormulario(getTableView().getItems().get(getIndex())));
                btnEliminar.setOnAction(event -> eliminarUsuario(getTableView().getItems().get(getIndex())));
            }
            @Override protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : hbox);
            }
        });
    }
    private void configurarComboRoles() {
        cmbRol.getItems().clear();
        cmbRol.getItems().add("Seleccione un rol");
        for (Usuario.Rol rol : Usuario.Rol.values()) {
            cmbRol.getItems().add(rol.name());
        }
        cmbRol.getSelectionModel().selectFirst();
    }
    private void cargarUsuarios() {
        Platform.runLater(() -> {
            try {
                listaUsuarios = FXCollections.observableArrayList(gestionUsuarios.listarTodos());
                tablaUsuarios.setItems(listaUsuarios);
                tablaUsuarios.refresh();
            } catch (Exception e) {
                logger.error("Error al cargar usuarios: " + e.getMessage());
                Dialogos.info("Error al cargar usuarios.");
            }
        });
    }
    @FXML private void handleNuevoUsuario() {
        usuarioEditando = null;
        limpiarFormulario();
        txtCodigo.setText(generarCodigoUnico());
        txtCodigo.setEditable(true);
        panelFormulario.setManaged(true);
        panelFormulario.setVisible(true);
    }
    @FXML private void handleCancelar() {
        limpiarFormulario();
        panelFormulario.setManaged(false);
        panelFormulario.setVisible(false);
    }
    @FXML private void handleGuardar() {
        String rolSeleccionado = cmbRol.getValue();
        String codigo = txtCodigo.getText().trim();
        String nombre = txtNombre.getText().trim();
        String apellido = txtApellido.getText().trim();
        String dni = txtDni.getText().trim();
        String edadTexto = txtEdad.getText().trim();
        String contrasenaTexto = txtContrasena.getText();
        if (rolSeleccionado == null || rolSeleccionado.startsWith("Seleccione") || codigo.isBlank() || nombre.isBlank() || apellido.isBlank() || dni.isBlank() || edadTexto.isBlank()) {
            Dialogos.info("Todos los campos obligatorios deben estar completos."); return;
        }
        int edad;
        try { edad = Integer.parseInt(edadTexto); }
        catch (NumberFormatException e) { Dialogos.info("La edad debe ser un número válido."); return; }
        if (usuarioEditando == null && contrasenaTexto.isBlank()) {
            Dialogos.info("Debe asignar una contraseña al nuevo usuario."); return;
        }
        try {
            Usuario.Rol rol = Usuario.Rol.valueOf(rolSeleccionado);
            if (usuarioEditando == null) {
                char[] contrasena = contrasenaTexto.toCharArray();
                String hash = hashProvider.generarHash(contrasena);
                CrearUsuarioCommand cmd = new CrearUsuarioCommand(codigo, hash, dni, nombre, apellido, edad, rol, null);
                gestionUsuarios.agregarUsuario(cmd);
            } else {
                if (!codigo.equals(usuarioEditando.getCodigo())) {
                    Dialogos.info("No se puede modificar el código de un usuario existente."); return;
                }
                if (!dni.equals(usuarioEditando.getDni()) && gestionUsuarios.existeDni(dni, usuarioEditando)) {
                    Dialogos.info("Ya existe otro usuario con ese DNI."); return;
                }
                usuarioEditando.setNombre(nombre);
                usuarioEditando.setApellido(apellido);
                usuarioEditando.setDni(dni);
                usuarioEditando.setEdad(edad);
                if (!contrasenaTexto.isBlank()) {
                    char[] nuevaContrasena = contrasenaTexto.toCharArray();
                    usuarioEditando.setHashContrasena(hashProvider.generarHash(nuevaContrasena));
                }
                gestionUsuarios.actualizarUsuario(usuarioEditando);
            }
            limpiarFormulario();
            panelFormulario.setManaged(false);
            panelFormulario.setVisible(false);
            eventBus.notificar(TipoEvento.USUARIOS);
        } catch (Exception e) {
            logger.error("Error al guardar usuario: " + e.getMessage());
            Dialogos.info(e.getMessage());
        }
    }
    private void cargarUsuarioEnFormulario(Usuario usuario) {
        usuarioEditando = usuario;
        panelFormulario.setManaged(true);
        panelFormulario.setVisible(true);
        cmbRol.setValue(usuario.getRol().name());
        txtCodigo.setText(usuario.getCodigo());
        txtCodigo.setEditable(false);
        txtNombre.setText(usuario.getNombre());
        txtApellido.setText(usuario.getApellido());
        txtDni.setText(usuario.getDni());
        txtEdad.setText(String.valueOf(usuario.getEdad()));
        txtContrasena.clear();
    }
    private void eliminarUsuario(Usuario usuario) {
        if (Dialogos.confirmar("Confirmar eliminación", "¿Está seguro de eliminar al usuario " + usuario.getCodigo() + "?")) {
            try {
                gestionUsuarios.eliminarUsuario(usuario);
                eventBus.notificar(TipoEvento.USUARIOS);
            } catch (Exception e) {
                logger.error("Error al eliminar usuario: " + e.getMessage());
                Dialogos.info(e.getMessage());
            }
        }
    }
    private void limpiarFormulario() {
        usuarioEditando = null;
        cmbRol.getSelectionModel().selectFirst();
        txtCodigo.clear(); txtCodigo.setEditable(true);
        txtNombre.clear(); txtApellido.clear(); txtDni.clear(); txtEdad.clear(); txtContrasena.clear();
    }
    private String generarCodigoUnico() {
        String codigo;
        do {
            int numero = (int) (Math.random() * 90_000_000) + 10_000_000;
            codigo = "U" + numero;
        } while (gestionUsuarios.buscarPorCodigo(codigo) != null);
        return codigo;
    }
}

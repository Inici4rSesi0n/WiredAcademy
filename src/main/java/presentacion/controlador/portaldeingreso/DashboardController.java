/*
 * SPDX-FileCopyrightText: 2026 inici4rsesi0n
 * SPDX-License-Identifier: GPL-3.0-or-later
 */
package presentacion.controlador.portaldeingreso;
import infraestructura.eventos.EventBus;
import dominio.puerto.externo.EventBusListener;
import aplicacion.casosdeuso.portaldeingreso.administraciondelsistema.academico.GestionAsignaturas;
import aplicacion.casosdeuso.portaldeingreso.administraciondelsistema.academico.GestionGrupos;
import aplicacion.casosdeuso.portaldeingreso.administraciondelsistema.usuarios.GestionDocentes;
import aplicacion.casosdeuso.portaldeingreso.administraciondelsistema.usuarios.GestionEstudiantes;
import aplicacion.casosdeuso.portaldeingreso.sistema.GestionPermisos;
import aplicacion.casosdeuso.portaldeingreso.sistema.GestionPermisos.SeccionMenu;
import dominio.modelo.Usuario;
import infraestructura.configuracion.SpringFxmlLoader;
import presentacion.dialogos.Dialogos;
import dominio.modelo.TipoEvento;
import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
/**
 *
 * @author inici4rsesi0n
 */
@Component
@Scope("prototype")
public class DashboardController implements Initializable, EventBusListener {
    private final GestionEstudiantes gestionEstudiantes;
    private final GestionDocentes gestionDocentes;
    private final GestionGrupos gestionGrupos;
    private final GestionAsignaturas gestionAsignaturas;
    private final GestionPermisos gestionPermisos;
    private final SpringFxmlLoader fxmlLoader;
    private final EventBus eventBus;
    @FXML private Label lblUsuarioHeader;
    @FXML private Label lblNumEstudiantes;
    @FXML private Label lblNumDocentes;
    @FXML private Label lblNumGrupos;
    @FXML private Label lblNumAsignaturas;
    @FXML private StackPane contenedorCentral;
    @FXML private Button btnVolverInicio;
    @FXML private Button btnMisCursos, btnMiHorario, btnCalificaciones, btnSubirMaterial, btnRegistrarAsistencia;
    @FXML private Button btnTramites, btnMatricula, btnGestionUsuarios, btnGestionAcademica, btnReportes, btnConfiguracion;
    private Usuario usuario;
    private Node vistaInicial;
    private final Map<String, Pane> vistasCargadas = new HashMap<>();
    private final Map<String, String> rutasVistas = new HashMap<>();
    public DashboardController(GestionEstudiantes gestionEstudiantes, GestionDocentes gestionDocentes,
                               GestionGrupos gestionGrupos, GestionAsignaturas gestionAsignaturas,
                               GestionPermisos gestionPermisos, SpringFxmlLoader fxmlLoader, EventBus eventBus) {
        this.gestionEstudiantes = gestionEstudiantes;
        this.gestionDocentes = gestionDocentes;
        this.gestionGrupos = gestionGrupos;
        this.gestionAsignaturas = gestionAsignaturas;
        this.gestionPermisos = gestionPermisos;
        this.fxmlLoader = fxmlLoader;
        this.eventBus = eventBus;
    }
    public void onEvent(TipoEvento evento) {
        switch (evento) {
            case ESTUDIANTES, DOCENTES, GRUPOS, ASIGNATURAS -> actualizarTarjetas();
            default -> {}
        }
    }
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        precargarRutas();
        if (!contenedorCentral.getChildren().isEmpty()) {
            vistaInicial = contenedorCentral.getChildren().get(0);
        }
        eventBus.suscribir(TipoEvento.ESTUDIANTES, this);
        eventBus.suscribir(TipoEvento.DOCENTES, this);
        eventBus.suscribir(TipoEvento.GRUPOS, this);
        eventBus.suscribir(TipoEvento.ASIGNATURAS, this);
    }
    private void precargarRutas() {
        rutasVistas.put("seccion_gestion_usuarios", "/fxml/portaldeingreso/administraciondelsistema/gestiondeusuarios/seccion_gestion_usuarios.fxml");
        rutasVistas.put("seccion_gestion_academica", "/fxml/portaldeingreso/administraciondelsistema/gestionacademica/seccion_gestion_academica.fxml");
        rutasVistas.put("seccion_matricula", "/fxml/portaldeingreso/gestiondetramites/matricula/seccion_matricula.fxml");
        rutasVistas.put("seccion_mis_cursos", "/fxml/portaldeingreso/miareapersonal/mis_cursos.fxml");
    }
    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
        if (lblUsuarioHeader != null) {
            lblUsuarioHeader.setText(usuario.getNombre() + " " + usuario.getApellido());
        }
        aplicarPermisosUI();
        cargarDatosIniciales();
    }
    private void aplicarPermisosUI() {
        if (usuario == null) return;
        Usuario.Rol rol = usuario.getRol();
        btnMisCursos.setVisible(gestionPermisos.esSeccionVisible(rol, SeccionMenu.MIS_CURSOS));
        btnMisCursos.setManaged(btnMisCursos.isVisible());
        btnMiHorario.setVisible(gestionPermisos.esSeccionVisible(rol, SeccionMenu.MI_HORARIO));
        btnMiHorario.setManaged(btnMiHorario.isVisible());
        btnCalificaciones.setVisible(gestionPermisos.esSeccionVisible(rol, SeccionMenu.CALIFICACIONES));
        btnCalificaciones.setManaged(btnCalificaciones.isVisible());
        btnSubirMaterial.setVisible(gestionPermisos.esSeccionVisible(rol, SeccionMenu.SUBIR_MATERIAL));
        btnSubirMaterial.setManaged(btnSubirMaterial.isVisible());
        btnRegistrarAsistencia.setVisible(gestionPermisos.esSeccionVisible(rol, SeccionMenu.REGISTRAR_ASISTENCIA));
        btnRegistrarAsistencia.setManaged(btnRegistrarAsistencia.isVisible());
        btnTramites.setVisible(gestionPermisos.esSeccionVisible(rol, SeccionMenu.TRAMITES));
        btnTramites.setManaged(btnTramites.isVisible());
        btnMatricula.setVisible(gestionPermisos.esSeccionVisible(rol, SeccionMenu.MATRICULA));
        btnMatricula.setManaged(btnMatricula.isVisible());
        btnGestionUsuarios.setVisible(gestionPermisos.esSeccionVisible(rol, SeccionMenu.GESTION_USUARIOS));
        btnGestionUsuarios.setManaged(btnGestionUsuarios.isVisible());
        btnGestionAcademica.setVisible(gestionPermisos.esSeccionVisible(rol, SeccionMenu.GESTION_ACADEMICA));
        btnGestionAcademica.setManaged(btnGestionAcademica.isVisible());
        btnReportes.setVisible(gestionPermisos.esSeccionVisible(rol, SeccionMenu.REPORTES));
        btnReportes.setManaged(btnReportes.isVisible());
        boolean verConfig = gestionPermisos.esSeccionVisible(rol, SeccionMenu.CONFIGURACION_SISTEMA) || 
                            gestionPermisos.esSeccionVisible(rol, SeccionMenu.CONFIGURACION_PERFIL);
        btnConfiguracion.setVisible(verConfig);
        btnConfiguracion.setManaged(verConfig);
    }
    private void cargarDatosIniciales() {
        if (usuario == null) return;
        actualizarTarjetas();
    }
    private void actualizarTarjetas() {
        if (usuario == null) return;
        Platform.runLater(() -> {
            try {
                if (lblNumEstudiantes != null) lblNumEstudiantes.setText(String.valueOf(gestionEstudiantes.listarTodos().size()));
                if (lblNumDocentes != null) lblNumDocentes.setText(String.valueOf(gestionDocentes.listarTodos().size()));
                if (lblNumGrupos != null) lblNumGrupos.setText(String.valueOf(gestionGrupos.listarTodos().size()));
                if (lblNumAsignaturas != null) lblNumAsignaturas.setText(String.valueOf(gestionAsignaturas.listarTodos().size()));
            } catch (Exception e) {
                System.err.println("Error actualizando tarjetas KPI: " + e.getMessage());
            }
        });
    }
    @FXML
    private void cargarSeccion(javafx.event.ActionEvent event) {
        Button boton = (Button) event.getSource();
        String clave = (String) boton.getUserData();
        if (clave == null) return;
        Pane vista = vistasCargadas.get(clave);
        if (vista == null) {
            String ruta = rutasVistas.get(clave);
            if (ruta == null) {
                Dialogos.info("Sección no disponible: " + clave);
                return;
            }
            try {
                FXMLLoader loader = fxmlLoader.crearLoader(ruta);
                vista = loader.load();
                vistasCargadas.put(clave, vista);
            } catch (IOException e) {
                e.printStackTrace();
                Dialogos.info("No se pudo cargar la sección: " + clave);
                return;
            }
        }
        contenedorCentral.getChildren().setAll(vista);
    }
    @FXML
    private void handleIrInicio() {
        if (vistaInicial != null) {
            contenedorCentral.getChildren().setAll(vistaInicial);
        }
    }
    @FXML
    private void handleVolverInicio() {
        if (btnVolverInicio != null && btnVolverInicio.getScene() != null) {
            Stage stage = (Stage) btnVolverInicio.getScene().getWindow();
            if (stage != null) stage.close();
        }
    }
}

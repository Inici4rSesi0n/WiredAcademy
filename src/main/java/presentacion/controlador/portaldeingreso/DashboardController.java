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
    @FXML private Label lblSeccionAreaPersonal, lblSeccionTramites, lblSeccionAdmin;
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
        if (lblUsuarioHeader != null) lblUsuarioHeader.setText(usuario.getNombre() + " " + usuario.getApellido());
        aplicarPermisosUI();
        cargarDatosIniciales();
    }
    private void aplicarPermisosUI() {
        if (usuario == null) return;
        Usuario.Rol rol = usuario.getRol();
        
        // Sección: Mi Área Personal
        boolean verMisCursos = gestionPermisos.esSeccionVisible(rol, SeccionMenu.MIS_CURSOS);
        boolean verMiHorario = gestionPermisos.esSeccionVisible(rol, SeccionMenu.MI_HORARIO);
        boolean verCalificaciones = gestionPermisos.esSeccionVisible(rol, SeccionMenu.CALIFICACIONES);
        boolean verSubirMaterial = gestionPermisos.esSeccionVisible(rol, SeccionMenu.SUBIR_MATERIAL);
        boolean verRegistrarAsistencia = gestionPermisos.esSeccionVisible(rol, SeccionMenu.REGISTRAR_ASISTENCIA);
        
        btnMisCursos.setVisible(verMisCursos); btnMisCursos.setManaged(verMisCursos);
        btnMiHorario.setVisible(verMiHorario); btnMiHorario.setManaged(verMiHorario);
        btnCalificaciones.setVisible(verCalificaciones); btnCalificaciones.setManaged(verCalificaciones);
        btnSubirMaterial.setVisible(verSubirMaterial); btnSubirMaterial.setManaged(verSubirMaterial);
        btnRegistrarAsistencia.setVisible(verRegistrarAsistencia); btnRegistrarAsistencia.setManaged(verRegistrarAsistencia);
        
        boolean areaPersonalVacia = !verMisCursos && !verMiHorario && !verCalificaciones && !verSubirMaterial && !verRegistrarAsistencia;
        if (lblSeccionAreaPersonal != null) { lblSeccionAreaPersonal.setVisible(!areaPersonalVacia); lblSeccionAreaPersonal.setManaged(!areaPersonalVacia); }

        // Sección: Gestión de Trámites
        boolean verTramites = gestionPermisos.esSeccionVisible(rol, SeccionMenu.TRAMITES);
        boolean verMatricula = gestionPermisos.esSeccionVisible(rol, SeccionMenu.MATRICULA);
        btnTramites.setVisible(verTramites); btnTramites.setManaged(verTramites);
        btnMatricula.setVisible(verMatricula); btnMatricula.setManaged(verMatricula);
        
        boolean tramitesVacio = !verTramites && !verMatricula;
        if (lblSeccionTramites != null) { lblSeccionTramites.setVisible(!tramitesVacio); lblSeccionTramites.setManaged(!tramitesVacio); }

        // Sección: Administración del Sistema
        boolean verGestionUsuarios = gestionPermisos.esSeccionVisible(rol, SeccionMenu.GESTION_USUARIOS);
        boolean verGestionAcademica = gestionPermisos.esSeccionVisible(rol, SeccionMenu.GESTION_ACADEMICA);
        boolean verReportes = gestionPermisos.esSeccionVisible(rol, SeccionMenu.REPORTES);
        boolean verConfig = gestionPermisos.esSeccionVisible(rol, SeccionMenu.CONFIGURACION_SISTEMA) || gestionPermisos.esSeccionVisible(rol, SeccionMenu.CONFIGURACION_PERFIL);
        
        btnGestionUsuarios.setVisible(verGestionUsuarios); btnGestionUsuarios.setManaged(verGestionUsuarios);
        btnGestionAcademica.setVisible(verGestionAcademica); btnGestionAcademica.setManaged(verGestionAcademica);
        btnReportes.setVisible(verReportes); btnReportes.setManaged(verReportes);
        btnConfiguracion.setVisible(verConfig); btnConfiguracion.setManaged(verConfig);
        
        boolean adminVacio = !verGestionUsuarios && !verGestionAcademica && !verReportes && !verConfig;
        if (lblSeccionAdmin != null) { lblSeccionAdmin.setVisible(!adminVacio); lblSeccionAdmin.setManaged(!adminVacio); }
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
    @FXML private void handleIrInicio() { if (vistaInicial != null) contenedorCentral.getChildren().setAll(vistaInicial); }
    @FXML private void handleVolverInicio() { if (btnVolverInicio != null && btnVolverInicio.getScene() != null) { Stage stage = (Stage) btnVolverInicio.getScene().getWindow(); if (stage != null) stage.close(); } }
}

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
    private final SpringFxmlLoader fxmlLoader;
    private final EventBus eventBus;

    @FXML private Label lblUsuarioHeader;
    @FXML private Label lblNumEstudiantes;
    @FXML private Label lblNumDocentes;
    @FXML private Label lblNumGrupos;
    @FXML private Label lblNumAsignaturas;
    @FXML private StackPane contenedorCentral;
    @FXML private Button btnVolverInicio;

    private Usuario usuario;
    private final Map<String, Pane> vistasCargadas = new HashMap<>();
    private final Map<String, String> rutasVistas = new HashMap<>();

    public DashboardController(GestionEstudiantes gestionEstudiantes,
                               GestionDocentes gestionDocentes,
                               GestionGrupos gestionGrupos,
                               GestionAsignaturas gestionAsignaturas,
                               SpringFxmlLoader fxmlLoader, EventBus eventBus) {
        this.gestionEstudiantes = gestionEstudiantes;
        this.gestionDocentes = gestionDocentes;
        this.gestionGrupos = gestionGrupos;
        this.gestionAsignaturas = gestionAsignaturas;
        this.fxmlLoader = fxmlLoader;
        this.eventBus = eventBus;
    }

    
    public void onEvent(TipoEvento evento) {
        switch (evento) {
            case ESTUDIANTES -> actualizarTarjetas();
            case DOCENTES -> actualizarTarjetas();
            case GRUPOS -> actualizarTarjetas();
            case ASIGNATURAS -> actualizarTarjetas();
            default -> {}
        }
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        precargarRutas();
        eventBus.suscribir(TipoEvento.ESTUDIANTES, this);
        eventBus.suscribir(TipoEvento.DOCENTES, this);
        eventBus.suscribir(TipoEvento.GRUPOS, this);
        eventBus.suscribir(TipoEvento.ASIGNATURAS, this);
    }

    private void precargarRutas() {
        rutasVistas.put("seccion_gestion_usuarios", "/fxml/portaldeingreso/administraciondelsistema/gestiondeusuarios/seccion_gestion_usuarios.fxml");
        rutasVistas.put("seccion_gestion_academica", "/fxml/portaldeingreso/administraciondelsistema/gestionacademica/seccion_gestion_academica.fxml");
        rutasVistas.put("seccion_matricula", "/fxml/portaldeingreso/gestiondetramites/matricula/seccion_matricula.fxml");
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
        if (lblUsuarioHeader != null) {
            lblUsuarioHeader.setText(usuario.getNombre() + " " + usuario.getApellido());
        }
        cargarDatosIniciales();
    }

    private void cargarDatosIniciales() {
        if (usuario == null) return;
        actualizarTarjetas();
    }

    private void actualizarTarjetas() {
        if (usuario == null) return;
        Platform.runLater(() -> {
            try {
                if (lblNumEstudiantes != null)
                    lblNumEstudiantes.setText(String.valueOf(gestionEstudiantes.listarTodos().size()));
                if (lblNumDocentes != null)
                    lblNumDocentes.setText(String.valueOf(gestionDocentes.listarTodos().size()));
                if (lblNumGrupos != null)
                    lblNumGrupos.setText(String.valueOf(gestionGrupos.listarTodos().size()));
                if (lblNumAsignaturas != null)
                    lblNumAsignaturas.setText(String.valueOf(gestionAsignaturas.listarTodos().size()));
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
    private void handleVolverInicio() {
        if (btnVolverInicio != null && btnVolverInicio.getScene() != null) {
            Stage stage = (Stage) btnVolverInicio.getScene().getWindow();
            if (stage != null) stage.close();
        }
    }
}

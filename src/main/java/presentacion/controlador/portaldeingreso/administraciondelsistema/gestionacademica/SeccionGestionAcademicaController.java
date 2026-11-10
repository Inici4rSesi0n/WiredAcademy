/*
 * SPDX-FileCopyrightText: 2026 inici4rsesi0n
 * SPDX-License-Identifier: GPL-3.0-or-later
 */
package presentacion.controlador.portaldeingreso.administraciondelsistema.gestionacademica;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.layout.StackPane;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import presentacion.controlador.portaldeingreso.administraciondelsistema.gestionacademica.catalogos.ContenedorCatalogosController;
import presentacion.controlador.portaldeingreso.administraciondelsistema.gestionacademica.asignaciones.ContenedorAsignacionesController;
import presentacion.controlador.portaldeingreso.administraciondelsistema.gestionacademica.horarios.GestionHorariosController;
@Component
@Scope("prototype")
public class SeccionGestionAcademicaController implements Initializable {
    @FXML private ComboBox<String> cmbCategoria;
    @FXML private StackPane contenedorCategorias;
    @FXML private Button btnNuevoGlobal;
    @FXML private Button btnVerHorarioGlobal;
    @FXML private ContenedorCatalogosController tabPaneCatalogosController;
    @FXML private ContenedorAsignacionesController tabPaneAsignacionesGroupController;
    @FXML private GestionHorariosController tabPaneHorariosController;
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        configurarCategoria();
    }
    private void configurarCategoria() {
        cmbCategoria.getItems().addAll("Catálogos", "Asignaciones", "Horarios");
        cmbCategoria.getSelectionModel().selectFirst();
        cmbCategoria.valueProperty().addListener((obs, oldVal, newVal) -> {
            if (tabPaneCatalogosController == null) return;
            boolean cat = "Catálogos".equals(newVal);
            boolean asig = "Asignaciones".equals(newVal);
            boolean hor = "Horarios".equals(newVal);
            tabPaneCatalogosController.getTabPane().setVisible(cat);
            tabPaneCatalogosController.getTabPane().setManaged(cat);
            tabPaneAsignacionesGroupController.getTabPane().setVisible(asig);
            tabPaneAsignacionesGroupController.getTabPane().setManaged(asig);
            tabPaneHorariosController.getRoot().setVisible(hor);
            tabPaneHorariosController.getRoot().setManaged(hor);
            btnNuevoGlobal.setVisible(cat || hor);
            btnNuevoGlobal.setManaged(cat || hor);
            btnVerHorarioGlobal.setVisible(hor);
            btnVerHorarioGlobal.setManaged(hor);
        });
        tabPaneAsignacionesGroupController.getTabPane().setVisible(false);
        tabPaneAsignacionesGroupController.getTabPane().setManaged(false);
        tabPaneHorariosController.getRoot().setVisible(false);
        tabPaneHorariosController.getRoot().setManaged(false);
        btnVerHorarioGlobal.setVisible(false);
        btnVerHorarioGlobal.setManaged(false);
    }
    @FXML
    private void handleNuevoGlobal() {
        String cat = cmbCategoria.getValue();
        if ("Catálogos".equals(cat)) {
            javafx.scene.control.Tab selected = tabPaneCatalogosController.getTabPane().getSelectionModel().getSelectedItem();
            if (selected == null) return;
            switch (selected.getId()) {
                case "tabAsignaturas": tabPaneCatalogosController.getIncludeAsignaturasController().handleNuevaAsignatura(); break;
                case "tabPeriodos": tabPaneCatalogosController.getIncludePeriodosController().handleNuevoPeriodo(); break;
                case "tabAulas": tabPaneCatalogosController.getIncludeAulasController().handleNuevaAula(); break;
                case "tabGrupos": tabPaneCatalogosController.getIncludeGruposController().handleNuevoGrupo(); break;
                case "tabRecreos": tabPaneCatalogosController.getIncludeRecreosController().handleNuevoRecreo(); break;
                case "tabGrados": tabPaneCatalogosController.getIncludeGradosController().handleNuevoGrado(); break;
                case "tabTurnos": tabPaneCatalogosController.getIncludeTurnosController().handleNuevoTurno(); break;
            }
        } else if ("Horarios".equals(cat)) {
            tabPaneHorariosController.handleNuevaClase();
        }
    }
    @FXML
    private void handleVerHorarioGlobal() {
        tabPaneHorariosController.handleVerHorario();
    }
}

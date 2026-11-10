/*
 * SPDX-FileCopyrightText: 2026 inici4rsesi0n
 * SPDX-License-Identifier: GPL-3.0-or-later
 */
package presentacion.controlador.portaldeingreso.administraciondelsistema.gestionacademica.asignaciones;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TabPane;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
@Component
@Scope("prototype")
public class ContenedorAsignacionesController implements Initializable {
    @FXML private TabPane tabPaneAsignacionesGroup;
    @FXML private AsignacionDocenteController includeAsigDocenteController;
    @FXML private AsignacionTutorController includeAsigTutorController;
    @FXML private AsignacionCoordinadorController includeAsigCoordinadorController;
    @Override
    public void initialize(URL location, ResourceBundle resources) {
    }
    public TabPane getTabPane() { return tabPaneAsignacionesGroup; }
    public AsignacionDocenteController getIncludeAsigDocenteController() { return includeAsigDocenteController; }
    public AsignacionTutorController getIncludeAsigTutorController() { return includeAsigTutorController; }
    public AsignacionCoordinadorController getIncludeAsigCoordinadorController() { return includeAsigCoordinadorController; }
}

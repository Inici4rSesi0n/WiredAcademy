/*
 * SPDX-FileCopyrightText: 2026 inici4rsesi0n
 * SPDX-License-Identifier: GPL-3.0-or-later
 */
package presentacion.controlador.portaldeingreso.administraciondelsistema.gestionacademica.catalogos;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Component
@Scope("prototype")
public class ContenedorCatalogosController implements Initializable {
    @FXML private TabPane tabPaneCatalogos;
    @FXML private Tab tabAsignaturas, tabPeriodos, tabAulas, tabGrupos, tabRecreos, tabGrados, tabTurnos;
    
    @FXML private AsignaturasController includeAsignaturasController;
    @FXML private PeriodosController includePeriodosController;
    @FXML private AulasController includeAulasController;
    @FXML private GruposController includeGruposController;
    @FXML private RecreosController includeRecreosController;
    @FXML private GradosController includeGradosController;
    @FXML private TurnosController includeTurnosController;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
    }

    public TabPane getTabPane() {
        return tabPaneCatalogos;
    }

    public AsignaturasController getIncludeAsignaturasController() { return includeAsignaturasController; }
    public PeriodosController getIncludePeriodosController() { return includePeriodosController; }
    public AulasController getIncludeAulasController() { return includeAulasController; }
    public GruposController getIncludeGruposController() { return includeGruposController; }
    public RecreosController getIncludeRecreosController() { return includeRecreosController; }
    public GradosController getIncludeGradosController() { return includeGradosController; }
    public TurnosController getIncludeTurnosController() { return includeTurnosController; }
}

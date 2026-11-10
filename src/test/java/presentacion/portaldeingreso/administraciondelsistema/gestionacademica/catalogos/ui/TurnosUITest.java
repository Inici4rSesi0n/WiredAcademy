/*
 * SPDX-FileCopyrightText: 2026 inici4rsesi0n
 * SPDX-License-Identifier: GPL-3.0-or-later
 */
package presentacion.portaldeingreso.administraciondelsistema.gestionacademica.catalogos.ui;
import aplicacion.casosdeuso.portaldeingreso.administraciondelsistema.academico.GestionTurnos;
import dominio.modelo.Turno;
import javafx.scene.control.TableView;
import javafx.stage.Stage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.testfx.util.WaitForAsyncUtils;
import presentacion.BaseUITest;
import presentacion.portaldeingreso.administraciondelsistema.gestionacademica.catalogos.pages.TurnosPage;
import java.util.concurrent.TimeUnit;
/**
 *
 * @author inici4rsesi0n
 */
public class TurnosUITest extends BaseUITest {
    @Override
    public void start(Stage stage) throws Exception {
        loadAndShow(stage, "/fxml/portaldeingreso/administraciondelsistema/gestionacademica/seccion_gestion_academica.fxml");
    }
    @BeforeEach
    public void cleanUp() {
        GestionTurnos gt = springContext.getBean(GestionTurnos.class);
        gt.listarTodos().stream()
                .filter(t -> t.getNombre().startsWith("UITest-"))
                .forEach(gt::eliminarTurno);
    }
    @Test
    public void testCrearTurno() throws Exception {
        TurnosPage page = new TurnosPage(this);
        page.clickTabTurnos()
            .clickNuevoTurno()
            .escribirNombre("UITest-Turno-1")
            .clickGuardar();
        WaitForAsyncUtils.waitFor(10, TimeUnit.SECONDS, () -> {
            TableView<?> tabla = lookup("#tablaTurnos").queryTableView();
            return tabla.getItems().stream()
                .anyMatch(o -> ((Turno) o).getNombre().equals("UITest-Turno-1"));
        });
    }
}

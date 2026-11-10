/*
 * SPDX-FileCopyrightText: 2026 inici4rsesi0n
 * SPDX-License-Identifier: GPL-3.0-or-later
 */
package infraestructura.configuracion;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;
import java.io.IOException;
import java.net.URL;
/**
 *
 * @author inici4rsesi0n
 */
@Component
public class SpringFxmlLoader {
    private final ApplicationContext contexto;
    public SpringFxmlLoader(ApplicationContext contexto) {
        this.contexto = contexto;
    }
    public FXMLLoader crearLoader(String rutaFxml) {
        URL url = getClass().getResource(rutaFxml);
        if (url == null) {
            throw new IllegalArgumentException("FXML no encontrado: " + rutaFxml);
        }
        FXMLLoader loader = new FXMLLoader(url);
        loader.setControllerFactory(this::crearController);
        return loader;
    }
    public Parent cargar(String rutaFxml) {
        try {
            return crearLoader(rutaFxml).load();
        } catch (IOException e) {
            throw new RuntimeException("Error al cargar FXML: " + rutaFxml, e);
        }
    }
    private Object crearController(Class<?> tipoController) {
        try {
            return contexto.getBean(tipoController);
        } catch (Exception e) {
            try {
                return tipoController.getDeclaredConstructor().newInstance();
            } catch (Exception ex) {
                throw new RuntimeException(
                        "No se pudo instanciar el controller: " + tipoController.getName(), ex);
            }
        }
    }
}

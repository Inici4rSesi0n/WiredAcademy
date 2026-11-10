/*
 * SPDX-FileCopyrightText: 2026 inici4rsesi0n
 * SPDX-License-Identifier: GPL-3.0-or-later
 */
package presentacion.dialogos;

import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.DialogPane;
import javafx.scene.control.TextInputDialog;

import java.util.Optional;

/**
 *
 * @author inici4rsesi0n
 */
public class Dialogos {
    
    private static void aplicarEstilo(DialogPane dialogPane) {
        try {
            String css = Dialogos.class.getResource("/css/app.css").toExternalForm();
            dialogPane.getStylesheets().add(css);
            dialogPane.getStyleClass().add("dialog-pane");
            
            // Forzar estilo oscuro al contenedor
            dialogPane.setStyle("-fx-background-color: #0F172A; -fx-border-color: #00D2FF; -fx-border-width: 1px;");
            
            // Forzar estilo a los botones (evita que el SO imponga su color)
            for (ButtonType type : dialogPane.getButtonTypes()) {
                Node button = dialogPane.lookupButton(type);
                if (button != null) {
                    button.setStyle("-fx-background-color: linear-gradient(to right, #00D2FF, #3B82F6); " +
                                    "-fx-text-fill: white; " +
                                    "-fx-font-weight: bold; " +
                                    "-fx-background-radius: 6px; " +
                                    "-fx-cursor: hand; " +
                                    "-fx-padding: 8px 24px;");
                }
            }
        } catch (Exception e) {
            System.err.println("No se pudo cargar el CSS en el diálogo: " + e.getMessage());
        }
    }

    public static void info(String mensaje) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Información");
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        aplicarEstilo(alert.getDialogPane());
        alert.showAndWait();
    }

    public static String pedirTexto(String mensaje) {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Entrada");
        dialog.setHeaderText(null);
        dialog.setContentText(mensaje);
        aplicarEstilo(dialog.getDialogPane());
        Optional<String> result = dialog.showAndWait();
        return result.orElse(null);
    }

    public static boolean confirmar(String titulo, String mensaje) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        aplicarEstilo(alert.getDialogPane());
        Optional<ButtonType> result = alert.showAndWait();
        return result.isPresent() && result.get() == ButtonType.OK;
    }
}

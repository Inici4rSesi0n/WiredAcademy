package presentacion.utilidades;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import java.util.function.Predicate;
/**
 *
 * @author inici4rsesi0n
 */
public class ValidadorUI {
    public static void aplicar(TextField campo, Predicate<String> regla, Label feedback) {
        campo.textProperty().addListener((obs, oldVal, newVal) -> {
            if (feedback == null) return;
            campo.getStyleClass().removeAll("input-error", "input-success");
            if (newVal == null || newVal.trim().isEmpty()) {
                feedback.setText("");
            } else if (regla.test(newVal)) {
                feedback.setText("✅");
                feedback.setStyle("-fx-text-fill: #2ecc71;");
                campo.getStyleClass().add("input-success");
            } else {
                feedback.setText("❌");
                feedback.setStyle("-fx-text-fill: #e74c3c;");
                campo.getStyleClass().add("input-error");
            }
        });
    }
}

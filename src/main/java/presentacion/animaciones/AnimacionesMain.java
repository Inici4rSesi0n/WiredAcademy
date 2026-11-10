/*
 * SPDX-FileCopyrightText: 2026 inici4rsesi0n
 * SPDX-License-Identifier: GPL-3.0-or-later
 */
package presentacion.animaciones;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.scene.control.Label;
import javafx.util.Duration;
/**
 *
 * @author inici4rsesi0n
 */
public class AnimacionesMain {

    public static Timeline crearTimelinePulso(Label label) {
        Timeline timeline = new Timeline(
            new KeyFrame(Duration.ZERO,
                new KeyValue(label.scaleXProperty(), 1.0),
                new KeyValue(label.scaleYProperty(), 1.0)
            ),
            new KeyFrame(Duration.millis(200),
                new KeyValue(label.scaleXProperty(), 1.1),
                new KeyValue(label.scaleYProperty(), 1.1)
            ),
            new KeyFrame(Duration.millis(3200),
                new KeyValue(label.scaleXProperty(), 1.1),
                new KeyValue(label.scaleYProperty(), 1.1)
            ),
            new KeyFrame(Duration.millis(3400),
                new KeyValue(label.scaleXProperty(), 1.0),
                new KeyValue(label.scaleYProperty(), 1.0)
            )
        );
        timeline.setCycleCount(1);
        return timeline;
    }
}
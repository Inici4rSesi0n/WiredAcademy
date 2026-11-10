/*
 * SPDX-FileCopyrightText: 2026 inici4rsesi0n
 * SPDX-License-Identifier: GPL-3.0-or-later
 */
package integration.eventos;
import boot.WiredAcademyApp;
import dominio.modelo.TipoEvento;
import infraestructura.eventos.EventBus;
import javafx.application.Platform;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
/**
 *
 * @author inici4rsesi0n
 */
@SpringBootTest(classes = WiredAcademyApp.class, webEnvironment = SpringBootTest.WebEnvironment.NONE)
@ActiveProfiles("test")
@Transactional
public abstract class BaseContractTest {
    static {
        System.setProperty("testfx.robot", "glass");
        System.setProperty("testfx.headless", "true");
        System.setProperty("glass.platform", "Monocle");
        System.setProperty("monocle.platform", "Headless");
        System.setProperty("prism.order", "sw");
        System.setProperty("prism.text", "t2k");
        System.setProperty("monocle.screen.width", "1280");
        System.setProperty("monocle.screen.height", "800");
        try {
            Platform.startup(() -> {});
        } catch (IllegalStateException alreadyInitialized) {
        }
    }
    @Autowired
    protected EventBus eventBus;
    protected TestEventRecorder recorder;
    @BeforeEach
    void setUpContract() {
        recorder = new TestEventRecorder();
        for (TipoEvento evento : TipoEvento.values()) {
            eventBus.suscribir(evento, recorder);
        }
        recorder.reset();
    }
    @AfterEach
    void tearDownContract() {
        if (recorder != null) {
            for (TipoEvento evento : TipoEvento.values()) {
                eventBus.desuscribir(evento, recorder);
            }
        }
    }
}

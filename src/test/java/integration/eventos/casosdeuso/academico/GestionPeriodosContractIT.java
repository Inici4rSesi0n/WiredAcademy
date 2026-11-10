/*
 * SPDX-FileCopyrightText: 2026 inici4rsesi0n
 * SPDX-License-Identifier: GPL-3.0-or-later
 */
package integration.eventos.casosdeuso.academico;
import aplicacion.casosdeuso.portaldeingreso.administraciondelsistema.academico.GestionPeriodos;
import dominio.modelo.PeriodoAcademico;
import dominio.modelo.TipoEvento;
import integration.eventos.BaseContractTest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import java.util.concurrent.TimeUnit;
import static org.awaitility.Awaitility.await;
import static org.junit.jupiter.api.Assertions.*;
/**
 *
 * @author inici4rsesi0n
 */
class GestionPeriodosContractIT extends BaseContractTest {
    @Autowired
    private GestionPeriodos gestionPeriodos;
    @Test
    void crearPeriodo_emitePERIODOS() {
        gestionPeriodos.crearPeriodo("CT-Per-001", "2026-01-01", "2026-12-31", "Activo");
        await().atMost(2, TimeUnit.SECONDS).until(() -> recorder.contiene(TipoEvento.PERIODOS));
        assertTrue(recorder.contiene(TipoEvento.PERIODOS));
    }
    @Test
    void actualizarPeriodo_emitePERIODOS() {
        gestionPeriodos.crearPeriodo("CT-Per-002", "2026-01-01", "2026-06-30", "Activo");
        PeriodoAcademico original = gestionPeriodos.buscarPorNombre("CT-Per-002");
        recorder.reset();
        gestionPeriodos.actualizarPeriodo(original, "CT-Per-002-UPD", "2026-07-01", "2026-12-31", "Culminado");
        await().atMost(2, TimeUnit.SECONDS).until(() -> recorder.contiene(TipoEvento.PERIODOS));
        assertTrue(recorder.contiene(TipoEvento.PERIODOS));
    }
    @Test
    void eliminarPeriodo_emitePERIODOS() {
        gestionPeriodos.crearPeriodo("CT-Per-003", "2026-01-01", "2026-12-31", "Activo");
        PeriodoAcademico periodo = gestionPeriodos.buscarPorNombre("CT-Per-003");
        recorder.reset();
        gestionPeriodos.eliminarPeriodo(periodo);
        await().atMost(2, TimeUnit.SECONDS).until(() -> recorder.contiene(TipoEvento.PERIODOS));
        assertTrue(recorder.contiene(TipoEvento.PERIODOS));
    }
}

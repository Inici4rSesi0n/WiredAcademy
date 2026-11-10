/*
 * SPDX-FileCopyrightText: 2026 inici4rsesi0n
 * SPDX-License-Identifier: GPL-3.0-or-later
 */
package infraestructura.log;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.slf4j.MDC;
import static org.junit.jupiter.api.Assertions.*;
/**
 *
 * @author inici4rsesi0n
 */
class Slf4jLoggerAdapterTest {
    @AfterEach
    void limpiar() {
        MDC.clear();
    }
    @Test
    void setUsuario_conCodigoValido_debeGuardarEnMdc() {
        Slf4jLoggerAdapter logger = new Slf4jLoggerAdapter();
        logger.setUsuario("USER1");
        assertEquals("USER1", MDC.get("usuario"));
    }
    @Test
    void setUsuario_conNull_debeLimpiarMdc() {
        Slf4jLoggerAdapter logger = new Slf4jLoggerAdapter();
        logger.setUsuario("USER1");
        logger.setUsuario(null);
        assertNull(MDC.get("usuario"), "MDC debe limpiarse al recibir null (bug B2)");
    }
    @Test
    void setUsuario_conVacio_debeLimpiarMdc() {
        Slf4jLoggerAdapter logger = new Slf4jLoggerAdapter();
        logger.setUsuario("USER1");
        logger.setUsuario("");
        assertNull(MDC.get("usuario"));
    }
    @Test
    void setUsuario_conBlanco_debeLimpiarMdc() {
        Slf4jLoggerAdapter logger = new Slf4jLoggerAdapter();
        logger.setUsuario("USER1");
        logger.setUsuario("   ");
        assertNull(MDC.get("usuario"));
    }
}

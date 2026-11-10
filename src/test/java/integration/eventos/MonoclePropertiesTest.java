/*
 * SPDX-FileCopyrightText: 2026 inici4rsesi0n
 * SPDX-License-Identifier: GPL-3.0-or-later
 */
package integration.eventos;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
/**
 * Verifica que la configuracion headless de JavaFX esta correctamente
 * configurada a nivel de JVM (via pom.xml surefire <systemPropertyVariables>).
 *
 * Si estos tests fallan, significa que:
 * 1. Alguien borro <glass.platform> o <monocle.platform> del pom.xml, o
 * 2. Se esta ejecutando el test fuera de surefire (IDE) sin la config.
 *
 * En ambos casos, los 51 contract tests fallaran en cascada con
 * 'Unable to open DISPLAY' -> ExceptionInInitializerError -> NoClassDefFoundError.
 *
 * Ver docs/TESTING.md seccion 6 (Troubleshooting) y seccion 9 (L-001).
 *
 * @author inici4rsesi0n
 */
class MonoclePropertiesTest {
    @Test
    void glassPlatform_debeSerMonocle() {
        assertEquals("Monocle", System.getProperty("glass.platform"),
                "glass.platform debe ser 'Monocle'. " +
                "Sin esto, JavaFX arranca Glass/GTK que requiere X11 (DISPLAY) y los " +
                "contract tests fallan en CI headless. " +
                "Configurar en pom.xml <systemPropertyVariables> o heredar de BaseContractTest. " +
                "Ver docs/TESTING.md seccion 6.");
    }
    @Test
    void monoclePlatform_debeSerHeadless() {
        assertEquals("Headless", System.getProperty("monocle.platform"),
                "monocle.platform debe ser 'Headless'. " +
                "Es el backend que permite a Monocle (el Glass platform) correr sin X11. " +
                "Sin esto, la config de glass.platform=Monocle es insuficiente. " +
                "Configurar en pom.xml <systemPropertyVariables> o heredar de BaseContractTest. " +
                "Ver docs/TESTING.md seccion 6.");
    }
    @Test
    void testfxHeadless_debeSerTrue() {
        assertEquals("true", System.getProperty("testfx.headless"),
                "testfx.headless debe ser 'true'. " +
                "Es la convencion de TestFX que indica a los tests de UI que corran sin display. " +
                "Configurar en pom.xml <systemPropertyVariables>. " +
                "Ver docs/TESTING.md seccion 5 (Configuracion headless).");
    }
}

/*
 * SPDX-FileCopyrightText: 2026 inici4rsesi0n
 * SPDX-License-Identifier: GPL-3.0-or-later
 */
package dominio.modelo;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 *
 * @author inici4rsesi0n
 */
class EventoTest {

    private FranjaHoraria franja;
    private Evento evento;

    @BeforeEach
    void setUp() {
        franja = new FranjaHoraria("Lunes", "08:00", "10:00");
        evento = new EventoConcreto(franja, "Clase de prueba");
    }

    @Test
    void constructor_debeCrearEventoConDatosCorrectos() {
        assertEquals("Lunes", evento.getDiaSemana());
        assertEquals("08:00", evento.getHoraInicio());
        assertEquals("10:00", evento.getHoraFin());
        assertEquals(franja, evento.getFranja());
    }

    @Test
    void constructor_debeLanzarExcepcionSiFranjaNula() {
        assertThrows(IllegalArgumentException.class,
                () -> new EventoConcreto(null, "desc"));
    }

    @Test
    void setFranja_debeActualizarFranja() {
        FranjaHoraria nuevaFranja = new FranjaHoraria("Martes", "09:00", "11:00");
        evento.setFranja(nuevaFranja);
        assertEquals(nuevaFranja, evento.getFranja());
        assertEquals("Martes", evento.getDiaSemana());
    }

    @Test
    void toString_debeIncluirDescripcionYFranja() {
        String texto = evento.toString();
        assertTrue(texto.contains("Clase de prueba"));
        assertTrue(texto.contains("Lunes 08:00-10:00"));
    }

    @Test
    void toString_conDescripcionNulaDebeMostrarSinDescripcion() {
        Evento eventoSinDesc = new EventoConcreto(franja, null);
        String texto = eventoSinDesc.toString();
        assertTrue(texto.contains("Sin descripción"));
    }

    private static class EventoConcreto extends Evento {
        private String descripcion;

        public EventoConcreto(FranjaHoraria franja, String descripcion) {
            super(franja);
            this.descripcion = descripcion;
        }

        @Override
        public String getDescripcion() {
            return descripcion;
        }
    }
}

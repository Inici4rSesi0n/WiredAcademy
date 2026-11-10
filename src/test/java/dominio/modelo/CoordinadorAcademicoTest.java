/*
 * SPDX-FileCopyrightText: 2026 inici4rsesi0n
 * SPDX-License-Identifier: GPL-3.0-or-later
 */
package dominio.modelo;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 *
 * @author inici4rsesi0n
 */
class CoordinadorAcademicoTest {

    private CoordinadorAcademico coordinador;

    @BeforeEach
    void setUp() {
        coordinador = new CoordinadorAcademico("C001", "hash", "111", "Coord", "Uno", 30);
    }

    @Test
    void constructor_debeCrearCoordinadorConRolCorrecto() {
        assertEquals("C001", coordinador.getCodigo());
        assertEquals("Coord", coordinador.getNombre());
        assertEquals("Uno", coordinador.getApellido());
        assertEquals(Usuario.Rol.COORDINADOR, coordinador.getRol());
    }

    @Test
    void gradosSupervisados_debeIniciarVacio() {
        assertTrue(coordinador.getGradosSupervisados().isEmpty());
    }

    @Test
    void agregarGrado_debeAgregarGradoALista() {
        Grado grado = new Grado("1er Grado", "Primaria");
        coordinador.agregarGrado(grado);

        assertEquals(1, coordinador.getGradosSupervisados().size());
        assertTrue(coordinador.getGradosSupervisados().contains(grado));
    }

    @Test
    void agregarGrado_noDebeDuplicar() {
        Grado grado = new Grado("1er Grado", "Primaria");
        coordinador.agregarGrado(grado);
        coordinador.agregarGrado(grado);

        assertEquals(1, coordinador.getGradosSupervisados().size());
    }

    @Test
    void removerGrado_debeEliminarGradoDeLista() {
        Grado grado1 = new Grado("1er Grado", "Primaria");
        Grado grado2 = new Grado("2do Grado", "Primaria");
        coordinador.agregarGrado(grado1);
        coordinador.agregarGrado(grado2);

        coordinador.removerGrado(grado1);

        assertEquals(1, coordinador.getGradosSupervisados().size());
        assertFalse(coordinador.getGradosSupervisados().contains(grado1));
        assertTrue(coordinador.getGradosSupervisados().contains(grado2));
    }

    @Test
    void setGradosSupervisados_debeReemplazarLista() {
        List<Grado> grados = new ArrayList<>();
        grados.add(new Grado("3er Grado", "Primaria"));
        grados.add(new Grado("4to Grado", "Primaria"));

        coordinador.setGradosSupervisados(grados);

        assertEquals(2, coordinador.getGradosSupervisados().size());
    }

    @Test
    void getNombreCompleto_debeRetornarNombreYApellido() {
        assertEquals("Coord Uno", coordinador.getNombreCompleto());
    }
}
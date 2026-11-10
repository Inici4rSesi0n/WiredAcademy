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
class PadreTest {

    private Padre padre;
    private Estudiante hijo1;
    private Estudiante hijo2;

    @BeforeEach
    void setUp() {
        List<Estudiante> hijosIniciales = new ArrayList<>();
        padre = new Padre("P001", "hash", "111", "Padre", "Ejemplo", 40, hijosIniciales);
        hijo1 = new Estudiante("E001", "hash1", "111", "Carlos", "Gomez", 15);
        hijo2 = new Estudiante("E002", "hash2", "222", "Maria", "Lopez", 16);
    }

    @Test
    void constructor_debeCrearPadreConDatosCorrectos() {
        assertEquals("P001", padre.getCodigo());
        assertEquals("hash", padre.getHashContrasena());
        assertEquals("111", padre.getDni());
        assertEquals("Padre", padre.getNombre());
        assertEquals("Ejemplo", padre.getApellido());
        assertEquals(40, padre.getEdad());
        assertEquals(Usuario.Rol.PADRE, padre.getRol());
        assertTrue(padre.getEstudiantes().isEmpty());
    }

    @Test
    void constructor_debeLanzarExcepcionSiCodigoNulo() {
        assertThrows(IllegalArgumentException.class,
                () -> new Padre(null, "hash", "111", "Padre", "Ejemplo", 40, new ArrayList<>()));
    }

    @Test
    void constructor_debeAceptarListaNulaYConvertirAVacia() {
        Padre padreNulo = new Padre("P002", "hash", "222", "Test", "Null", 35, null);
        assertNotNull(padreNulo.getEstudiantes());
        assertTrue(padreNulo.getEstudiantes().isEmpty());
    }

    @Test
    void agregarHijo_debeAgregarALista() {
        padre.agregarHijo(hijo1);
        assertEquals(1, padre.getEstudiantes().size());
        assertTrue(padre.getEstudiantes().contains(hijo1));
    }

    @Test
    void agregarHijo_noDebeDuplicar() {
        padre.agregarHijo(hijo1);
        padre.agregarHijo(hijo1);
        assertEquals(1, padre.getEstudiantes().size());
    }

    @Test
    void agregarHijo_debeLanzarExcepcionSiEstudianteNulo() {
        assertThrows(IllegalArgumentException.class,
                () -> padre.agregarHijo(null));
    }

    @Test
    void removerHijo_debeEliminarDeLista() {
        padre.agregarHijo(hijo1);
        padre.agregarHijo(hijo2);
        padre.removerHijo(hijo1);
        assertEquals(1, padre.getEstudiantes().size());
        assertFalse(padre.getEstudiantes().contains(hijo1));
        assertTrue(padre.getEstudiantes().contains(hijo2));
    }

    @Test
    void removerHijo_noDebeFallarSiNoExiste() {
        padre.agregarHijo(hijo1);
        padre.removerHijo(hijo2); // No está
        assertEquals(1, padre.getEstudiantes().size());
    }

    @Test
    void getEstudiantes_debeRetornarListaNoModificable() {
        padre.agregarHijo(hijo1);
        List<Estudiante> lista = padre.getEstudiantes();
        assertThrows(UnsupportedOperationException.class,
                () -> lista.add(new Estudiante("E003", "hash3", "333", "Nuevo", "Intento", 17)));
    }
}
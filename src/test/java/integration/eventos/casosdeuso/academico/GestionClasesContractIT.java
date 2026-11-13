/*
 * SPDX-FileCopyrightText: 2026 inici4rsesi0n
 * SPDX-License-Identifier: GPL-3.0-or-later
 */
package integration.eventos.casosdeuso.academico;
import aplicacion.casosdeuso.portaldeingreso.administraciondelsistema.academico.GestionAsignaturas;
import aplicacion.casosdeuso.portaldeingreso.administraciondelsistema.academico.GestionAulas;
import aplicacion.casosdeuso.portaldeingreso.administraciondelsistema.academico.GestionClases;
import aplicacion.casosdeuso.portaldeingreso.administraciondelsistema.academico.GestionGrados;
import aplicacion.casosdeuso.portaldeingreso.administraciondelsistema.academico.GestionGrupos;
import aplicacion.casosdeuso.portaldeingreso.administraciondelsistema.academico.GestionPeriodos;
import aplicacion.casosdeuso.portaldeingreso.administraciondelsistema.usuarios.GestionDocentes;
import dominio.modelo.*;
import integration.eventos.BaseContractTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;
import static org.awaitility.Awaitility.await;
import static org.junit.jupiter.api.Assertions.*;
/**
 *
 * @author inici4rsesi0n
 */
class GestionClasesContractIT extends BaseContractTest {
    @Autowired private GestionClases gestionClases;
    @Autowired private GestionAsignaturas gestionAsignaturas;
    @Autowired private GestionAulas gestionAulas;
    @Autowired private GestionDocentes gestionDocentes;
    @Autowired private GestionGrados gestionGrados;
    @Autowired private GestionGrupos gestionGrupos;
    @Autowired private GestionPeriodos gestionPeriodos;
    private Asignatura asignatura;
    private Docente docente;
    private Grupo grupo;
    private Aula aula;
    private PeriodoAcademico periodo;
    @BeforeEach
    void setUpDatos() {
        gestionAsignaturas.crearAsignatura("CT-Clase-Asig");
        asignatura = gestionAsignaturas.buscarAsignatura("CT-Clase-Asig");
        gestionGrados.crearGrado("CT-Clase-Grado", "Primaria");
        Grado grado = gestionGrados.buscarPorNombreYNivel("CT-Clase-Grado", "Primaria");
        gestionGrupos.crearGrupo("CT-Clase-Grupo", grado);
        grupo = gestionGrupos.buscarPorNombre("CT-Clase-Grupo");
        gestionAulas.crearAula("CT-Clase-Aula", 30, "Piso CT", "Teoría");
        aula = gestionAulas.buscarAula("CT-Clase-Aula");
        gestionPeriodos.crearPeriodo("CT-Clase-Per", "2026-01-01", "2026-12-31", "Activo");
        periodo = gestionPeriodos.buscarPorNombre("CT-Clase-Per");
        gestionDocentes.agregar(new Docente("CT-Clase-DOC", "hash", "91000001", "Doc", "CT", 30, "Mat", new ArrayList<>()));
        docente = gestionDocentes.buscarPorCodigo("CT-Clase-DOC");
        recorder.reset();
    }
    @Test
    void crearClase_emiteCLASES() {
        gestionClases.crearClase("Lunes", "08:00", "10:00", asignatura, docente, grupo, aula, periodo);
        await().atMost(2, TimeUnit.SECONDS).until(() -> recorder.contiene(TipoEvento.CLASES));
        assertTrue(recorder.contiene(TipoEvento.CLASES));
    }
    @Test
    void actualizarClase_emiteCLASES() {
        gestionClases.crearClase("Lunes", "08:00", "10:00", asignatura, docente, grupo, aula, periodo);
        Clase original = gestionClases.buscarClase("CT-Clase-Grado", "CT-Clase-Grupo", "Lunes", "08:00", "10:00");
        recorder.reset();
        gestionClases.actualizarClase(original, "Martes", "09:00", "11:00", asignatura, docente, grupo, aula, periodo);
        await().atMost(2, TimeUnit.SECONDS).until(() -> recorder.contiene(TipoEvento.CLASES));
        assertTrue(recorder.contiene(TipoEvento.CLASES));
    }
    @Test
    void eliminarClase_emiteCLASES() {
        gestionClases.crearClase("Lunes", "08:00", "10:00", asignatura, docente, grupo, aula, periodo);
        Clase clase = gestionClases.buscarClase("CT-Clase-Grado", "CT-Clase-Grupo", "Lunes", "08:00", "10:00");
        recorder.reset();
        gestionClases.eliminarClase(clase);
        await().atMost(2, TimeUnit.SECONDS).until(() -> recorder.contiene(TipoEvento.CLASES));
        assertTrue(recorder.contiene(TipoEvento.CLASES));
    }
}

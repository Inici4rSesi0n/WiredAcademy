/*
 * SPDX-FileCopyrightText: 2026 inici4rsesi0n
 * SPDX-License-Identifier: GPL-3.0-or-later
 */
package integration.eventos.casosdeuso.tramites;
import aplicacion.casosdeuso.portaldeingreso.administraciondelsistema.academico.GestionGrados;
import aplicacion.casosdeuso.portaldeingreso.administraciondelsistema.academico.GestionGrupos;
import aplicacion.casosdeuso.portaldeingreso.administraciondelsistema.academico.GestionPeriodos;
import aplicacion.casosdeuso.portaldeingreso.administraciondelsistema.usuarios.GestionEstudiantes;
import aplicacion.casosdeuso.portaldeingreso.gestiondetramites.GestionMatriculas;
import dominio.modelo.*;
import integration.eventos.BaseContractTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import java.util.concurrent.TimeUnit;
import static org.awaitility.Awaitility.await;
import static org.junit.jupiter.api.Assertions.*;
/**
 *
 * @author inici4rsesi0n
 */
class GestionMatriculasContractIT extends BaseContractTest {
    @Autowired private GestionMatriculas gestionMatriculas;
    @Autowired private GestionEstudiantes gestionEstudiantes;
    @Autowired private GestionGrados gestionGrados;
    @Autowired private GestionGrupos gestionGrupos;
    @Autowired private GestionPeriodos gestionPeriodos;
    private Estudiante estudiante;
    private PeriodoAcademico periodo;
    private Grupo grupo;
    @BeforeEach
    void setUpDatos() {
        gestionEstudiantes.agregar(new Estudiante("CT-Mat-EST1", "hash", "96000001", "Est", "CT", 15));
        estudiante = gestionEstudiantes.buscarPorCodigo("CT-Mat-EST1");
        gestionPeriodos.crearPeriodo("CT-Mat-Per", "2026-01-01", "2026-12-31", "Activo");
        periodo = gestionPeriodos.buscarPorNombre("CT-Mat-Per");
        gestionGrados.crearGrado("CT-Mat-Grado", "Primaria");
        Grado grado = gestionGrados.buscarPorNombreYNivel("CT-Mat-Grado", "Primaria");
        gestionGrupos.crearGrupo("CT-Mat-Grupo", grado);
        grupo = gestionGrupos.buscarPorNombre("CT-Mat-Grupo");
        recorder.reset();
    }
    @Test
    void matricularEstudiante_emiteMATRICULAS() {
        gestionMatriculas.matricularEstudiante(estudiante, periodo, grupo, null, "2026-01-10", EstadoMatricula.ACTIVA);
        await().atMost(2, TimeUnit.SECONDS).until(() -> recorder.contiene(TipoEvento.MATRICULAS));
        assertTrue(recorder.contiene(TipoEvento.MATRICULAS));
    }
    @Test
    void actualizar_emiteMATRICULAS() {
        gestionMatriculas.matricularEstudiante(estudiante, periodo, grupo, null, "2026-01-10", EstadoMatricula.ACTIVA);
        Matricula matricula = gestionMatriculas.buscarMatricula("CT-Mat-EST1", "CT-Mat-Per");
        matricula.setFecha("2026-01-15");
        recorder.reset();
        gestionMatriculas.actualizar(matricula);
        await().atMost(2, TimeUnit.SECONDS).until(() -> recorder.contiene(TipoEvento.MATRICULAS));
        assertTrue(recorder.contiene(TipoEvento.MATRICULAS));
    }
    @Test
    void actualizarEstado_emiteMATRICULAS() {
        gestionMatriculas.matricularEstudiante(estudiante, periodo, grupo, null, "2026-01-10", EstadoMatricula.ACTIVA);
        Matricula matricula = gestionMatriculas.buscarMatricula("CT-Mat-EST1", "CT-Mat-Per");
        recorder.reset();
        gestionMatriculas.actualizarEstado(matricula, EstadoMatricula.RETIRADA);
        await().atMost(2, TimeUnit.SECONDS).until(() -> recorder.contiene(TipoEvento.MATRICULAS));
        assertTrue(recorder.contiene(TipoEvento.MATRICULAS));
    }
    @Test
    void eliminarMatricula_emiteMATRICULAS() {
        gestionMatriculas.matricularEstudiante(estudiante, periodo, grupo, null, "2026-01-10", EstadoMatricula.ACTIVA);
        Matricula matricula = gestionMatriculas.buscarMatricula("CT-Mat-EST1", "CT-Mat-Per");
        recorder.reset();
        gestionMatriculas.eliminarMatricula(matricula);
        await().atMost(2, TimeUnit.SECONDS).until(() -> recorder.contiene(TipoEvento.MATRICULAS));
        assertTrue(recorder.contiene(TipoEvento.MATRICULAS));
    }
}

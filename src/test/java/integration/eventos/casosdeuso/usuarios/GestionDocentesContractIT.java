/*
 * SPDX-FileCopyrightText: 2026 inici4rsesi0n
 * SPDX-License-Identifier: GPL-3.0-or-later
 */
package integration.eventos.casosdeuso.usuarios;
import aplicacion.casosdeuso.portaldeingreso.administraciondelsistema.academico.GestionAsignaturas;
import aplicacion.casosdeuso.portaldeingreso.administraciondelsistema.academico.GestionGrados;
import aplicacion.casosdeuso.portaldeingreso.administraciondelsistema.academico.GestionGrupos;
import aplicacion.casosdeuso.portaldeingreso.administraciondelsistema.usuarios.GestionDocentes;
import dominio.modelo.Asignatura;
import dominio.modelo.Docente;
import dominio.modelo.Grado;
import dominio.modelo.Grupo;
import dominio.modelo.TipoEvento;
import integration.eventos.BaseContractTest;
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
class GestionDocentesContractIT extends BaseContractTest {
    @Autowired private GestionDocentes gestionDocentes;
    @Autowired private GestionAsignaturas gestionAsignaturas;
    @Autowired private GestionGrupos gestionGrupos;
    @Autowired private GestionGrados gestionGrados;
    private Docente crearDocente(String codigo, String dni) {
        Docente d = new Docente(codigo, "hash", dni, "Doc", "CT", 30, "Mat", new ArrayList<>());
        gestionDocentes.agregar(d);
        return gestionDocentes.buscarPorCodigo(codigo);
    }
    private Asignatura crearAsignatura(String nombre) {
        gestionAsignaturas.crearAsignatura(nombre);
        return gestionAsignaturas.buscarAsignatura(nombre);
    }
    private Grupo crearGrupo(String nombre, String grado) {
        gestionGrados.crearGrado(grado, "Primaria");
        Grado g = gestionGrados.buscarPorNombreYNivel(grado, "Primaria");
        gestionGrupos.crearGrupo(nombre, g);
        return gestionGrupos.buscarPorNombre(nombre);
    }
    @Test
    void agregar_emiteDOCENTES() {
        crearDocente("CT-Doc-001", "94100001");
        await().atMost(2, TimeUnit.SECONDS).until(() -> recorder.contiene(TipoEvento.DOCENTES));
        assertTrue(recorder.contiene(TipoEvento.DOCENTES));
    }
    @Test
    void actualizar_emiteDOCENTES() {
        crearDocente("CT-Doc-002", "94100002");
        Docente actualizado = new Docente("CT-Doc-002", "hash", "94100002", "Doc", "Updated", 30, "Mat", new ArrayList<>());
        recorder.reset();
        gestionDocentes.actualizar(actualizado, actualizado);
        await().atMost(2, TimeUnit.SECONDS).until(() -> recorder.contiene(TipoEvento.DOCENTES));
        assertTrue(recorder.contiene(TipoEvento.DOCENTES));
    }
    @Test
    void eliminar_emiteDOCENTES() {
        Docente docente = crearDocente("CT-Doc-003", "94100003");
        recorder.reset();
        gestionDocentes.eliminar(docente);
        await().atMost(2, TimeUnit.SECONDS).until(() -> recorder.contiene(TipoEvento.DOCENTES));
        assertTrue(recorder.contiene(TipoEvento.DOCENTES));
    }
    @Test
    void agregarAsignatura_emiteDOCENTES() {
        Docente docente = crearDocente("CT-Doc-004", "94100004");
        Asignatura asignatura = crearAsignatura("CT-Doc-Asig1");
        recorder.reset();
        gestionDocentes.agregarAsignatura(docente, asignatura);
        await().atMost(2, TimeUnit.SECONDS).until(() -> recorder.contiene(TipoEvento.DOCENTES));
        assertTrue(recorder.contiene(TipoEvento.DOCENTES));
    }
    @Test
    void removerAsignatura_emiteDOCENTES() {
        Docente docente = crearDocente("CT-Doc-005", "94100005");
        Asignatura asignatura = crearAsignatura("CT-Doc-Asig2");
        gestionDocentes.agregarAsignatura(docente, asignatura);
        recorder.reset();
        gestionDocentes.removerAsignatura(docente, asignatura);
        await().atMost(2, TimeUnit.SECONDS).until(() -> recorder.contiene(TipoEvento.DOCENTES));
        assertTrue(recorder.contiene(TipoEvento.DOCENTES));
    }
    @Test
    void asignarTutoria_emiteGRUPOS() {
        Docente docente = crearDocente("CT-Doc-006", "94100006");
        Grupo grupo = crearGrupo("CT-Doc-Grp1", "CT-Doc-Grado1");
        recorder.reset();
        gestionDocentes.asignarTutoria(docente, grupo);
        await().atMost(2, TimeUnit.SECONDS).until(() -> recorder.contiene(TipoEvento.GRUPOS));
        assertTrue(recorder.contiene(TipoEvento.GRUPOS));
    }
    @Test
    void asignarTutoria_emiteDOCENTES() {
        Docente docente = crearDocente("CT-Doc-007", "94100007");
        Grupo grupo = crearGrupo("CT-Doc-Grp2", "CT-Doc-Grado2");
        recorder.reset();
        gestionDocentes.asignarTutoria(docente, grupo);
        await().atMost(2, TimeUnit.SECONDS).until(() -> recorder.contiene(TipoEvento.DOCENTES));
        assertTrue(recorder.contiene(TipoEvento.DOCENTES));
    }
}

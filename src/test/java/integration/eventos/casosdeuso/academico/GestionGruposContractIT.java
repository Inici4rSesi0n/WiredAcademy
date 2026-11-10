/*
 * SPDX-FileCopyrightText: 2026 inici4rsesi0n
 * SPDX-License-Identifier: GPL-3.0-or-later
 */
package integration.eventos.casosdeuso.academico;
import aplicacion.casosdeuso.portaldeingreso.administraciondelsistema.academico.GestionGrados;
import aplicacion.casosdeuso.portaldeingreso.administraciondelsistema.academico.GestionGrupos;
import aplicacion.casosdeuso.portaldeingreso.administraciondelsistema.usuarios.GestionDocentes;
import dominio.modelo.*;
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
class GestionGruposContractIT extends BaseContractTest {
    @Autowired private GestionGrupos gestionGrupos;
    @Autowired private GestionGrados gestionGrados;
    @Autowired private GestionDocentes gestionDocentes;
    private Grado crearGrado(String nombre) {
        gestionGrados.crearGrado(nombre, "Primaria");
        return gestionGrados.buscarPorNombreYNivel(nombre, "Primaria");
    }
    private Docente crearDocente(String codigo, String dni) {
        gestionDocentes.agregar(new Docente(codigo, "hash", dni, "Doc", "CT", 30, "Mat", new ArrayList<>()));
        return gestionDocentes.buscarPorCodigo(codigo);
    }
    @Test
    void crearGrupo_emiteGRUPOS() {
        Grado grado = crearGrado("CT-Grp-G1");
        recorder.reset();
        gestionGrupos.crearGrupo("CT-Grp-A", grado);
        await().atMost(2, TimeUnit.SECONDS).until(() -> recorder.contiene(TipoEvento.GRUPOS));
        assertTrue(recorder.contiene(TipoEvento.GRUPOS));
    }
    @Test
    void actualizarGrupo_emiteGRUPOS() {
        Grado grado = crearGrado("CT-Grp-G2");
        gestionGrupos.crearGrupo("CT-Grp-B", grado);
        Grupo original = gestionGrupos.buscarPorNombre("CT-Grp-B");
        recorder.reset();
        gestionGrupos.actualizarGrupo(original, "CT-Grp-B-UPD", grado);
        await().atMost(2, TimeUnit.SECONDS).until(() -> recorder.contiene(TipoEvento.GRUPOS));
        assertTrue(recorder.contiene(TipoEvento.GRUPOS));
    }
    @Test
    void eliminarGrupo_emiteGRUPOS() {
        Grado grado = crearGrado("CT-Grp-G3");
        gestionGrupos.crearGrupo("CT-Grp-C", grado);
        Grupo grupo = gestionGrupos.buscarPorNombre("CT-Grp-C");
        recorder.reset();
        gestionGrupos.eliminarGrupo(grupo);
        await().atMost(2, TimeUnit.SECONDS).until(() -> recorder.contiene(TipoEvento.GRUPOS));
        assertTrue(recorder.contiene(TipoEvento.GRUPOS));
    }
    @Test
    void asignarTutor_emiteGRUPOS() {
        Grado grado = crearGrado("CT-Grp-G4");
        gestionGrupos.crearGrupo("CT-Grp-D", grado);
        Grupo grupo = gestionGrupos.buscarPorNombre("CT-Grp-D");
        Docente tutor = crearDocente("CT-Grp-DOC1", "92000001");
        recorder.reset();
        gestionGrupos.asignarTutor(grupo, tutor);
        await().atMost(2, TimeUnit.SECONDS).until(() -> recorder.contiene(TipoEvento.GRUPOS));
        assertTrue(recorder.contiene(TipoEvento.GRUPOS));
    }
    @Test
    void asignarTutor_emiteDOCENTES() {
        Grado grado = crearGrado("CT-Grp-G5");
        gestionGrupos.crearGrupo("CT-Grp-E", grado);
        Grupo grupo = gestionGrupos.buscarPorNombre("CT-Grp-E");
        Docente tutor = crearDocente("CT-Grp-DOC2", "92000002");
        recorder.reset();
        gestionGrupos.asignarTutor(grupo, tutor);
        await().atMost(2, TimeUnit.SECONDS).until(() -> recorder.contiene(TipoEvento.DOCENTES));
        assertTrue(recorder.contiene(TipoEvento.DOCENTES));
    }
    @Test
    void removerTutor_emiteGRUPOS() {
        Grado grado = crearGrado("CT-Grp-G6");
        gestionGrupos.crearGrupo("CT-Grp-F", grado);
        Grupo grupo = gestionGrupos.buscarPorNombre("CT-Grp-F");
        Docente tutor = crearDocente("CT-Grp-DOC3", "92000003");
        gestionGrupos.asignarTutor(grupo, tutor);
        recorder.reset();
        gestionGrupos.removerTutor(grupo);
        await().atMost(2, TimeUnit.SECONDS).until(() -> recorder.contiene(TipoEvento.GRUPOS));
        assertTrue(recorder.contiene(TipoEvento.GRUPOS));
    }
    @Test
    void removerTutor_emiteDOCENTES() {
        Grado grado = crearGrado("CT-Grp-G7");
        gestionGrupos.crearGrupo("CT-Grp-H", grado);
        Grupo grupo = gestionGrupos.buscarPorNombre("CT-Grp-H");
        Docente tutor = crearDocente("CT-Grp-DOC4", "92000004");
        gestionGrupos.asignarTutor(grupo, tutor);
        recorder.reset();
        gestionGrupos.removerTutor(grupo);
        await().atMost(2, TimeUnit.SECONDS).until(() -> recorder.contiene(TipoEvento.DOCENTES));
        assertTrue(recorder.contiene(TipoEvento.DOCENTES));
    }
}

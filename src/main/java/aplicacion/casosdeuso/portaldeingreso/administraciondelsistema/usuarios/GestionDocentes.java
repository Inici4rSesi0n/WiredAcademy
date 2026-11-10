/*
 * SPDX-FileCopyrightText: 2026 inici4rsesi0n
 * SPDX-License-Identifier: GPL-3.0-or-later
 */
package aplicacion.casosdeuso.portaldeingreso.administraciondelsistema.usuarios;

import dominio.modelo.Asignatura;
import dominio.modelo.Docente;
import dominio.modelo.Grupo;
import dominio.puerto.externo.LoggerPort;
import dominio.puerto.externo.EventPublisher;
import dominio.modelo.TipoEvento;
import dominio.puerto.repositorio.RepositorioDocentes;
import dominio.puerto.repositorio.RepositorioGrupos;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 *
 * @author inici4rsesi0n
 */
@Service
public class GestionDocentes {

    private final RepositorioDocentes repo;
    private final RepositorioGrupos repoGrupos;
    private final LoggerPort logger;
    private final EventPublisher publisher;

    public GestionDocentes(RepositorioDocentes repo, RepositorioGrupos repoGrupos, LoggerPort logger, EventPublisher publisher) {
        this.repo = repo;
        this.repoGrupos = repoGrupos;
        this.logger = logger;
        this.publisher = publisher;
    }

    public void agregar(Docente docente) {
        if (docente == null) throw new IllegalArgumentException("El docente no puede ser nulo");
        if (repo.buscarPorCodigo(docente.getCodigo()).isPresent())
            throw new IllegalArgumentException("Ya existe un docente con ese código");
        if (repo.buscarPorDni(docente.getDni()).isPresent())
            throw new IllegalArgumentException("Ya existe un docente con ese DNI");
        repo.agregar(docente);
        logger.audit(String.format(
                "op=CREATE entity=DOCENTE id=%s | nombre=%s %s dni=%s",
                docente.getCodigo(), docente.getNombre(),
                docente.getApellido(), docente.getDni()));
        publisher.notificar(TipoEvento.DOCENTES);
    }

    public Docente buscarPorCodigo(String codigo) { return repo.buscarPorCodigo(codigo).orElse(null); }
    public Docente buscarPorDni(String dni) { return repo.buscarPorDni(dni).orElse(null); }
    public List<Docente> listarTodos() { return repo.listarTodos(); }

    public void actualizar(Docente original, Docente actualizado) {
        if (original == null || actualizado == null) {
            throw new IllegalArgumentException("Los docentes no pueden ser nulos");
        }
        String antes = original.toString();
        repo.actualizar(original, actualizado);
        logger.audit(String.format(
                "op=UPDATE entity=DOCENTE id=%s | before=%s after=%s",
                original.getCodigo(), antes, actualizado));
        publisher.notificar(TipoEvento.DOCENTES);
    }

    public void eliminar(Docente docente) {
        if (docente == null) throw new IllegalArgumentException("El docente no puede ser nulo");
        String snapshot = docente.toString();
        repo.eliminar(docente);
        logger.audit(String.format(
                "op=DELETE entity=DOCENTE id=%s | snapshot=%s",
                docente.getCodigo(), snapshot));
        publisher.notificar(TipoEvento.DOCENTES);
    }

    public void agregarAsignatura(Docente docente, Asignatura asignatura) {
        if (asignatura == null) throw new IllegalArgumentException("La asignatura no puede ser nula");
        docente.agregarAsignatura(asignatura);
        repo.actualizar(docente, docente);
        logger.audit(String.format(
                "op=LINK entity=DOCENTE id=%s -> ASIGNATURA id=%s",
                docente.getCodigo(), asignatura.getNombre()));
        publisher.notificar(TipoEvento.DOCENTES);
    }

    public void removerAsignatura(Docente docente, Asignatura asignatura) {
        if (asignatura == null) throw new IllegalArgumentException("La asignatura no puede ser nula");
        docente.removerAsignatura(asignatura);
        repo.actualizar(docente, docente);
        logger.audit(String.format(
                "op=UNLINK entity=DOCENTE id=%s -> ASIGNATURA id=%s",
                docente.getCodigo(), asignatura.getNombre()));
        publisher.notificar(TipoEvento.DOCENTES);
    }

    /**
     * Asigna una tutoría a un docente sobre un grupo.
     * Persiste tanto el grupo (con su nuevo tutor) como el docente (con su nueva tutoría).
     */
    @Transactional
    public void asignarTutoria(Docente docente, Grupo grupo) {
        if (docente == null || grupo == null) throw new IllegalArgumentException("Docente y grupo son obligatorios");

        Docente tutorAnterior = grupo.getTutor();
        grupo.asignarTutor(docente);

        if (tutorAnterior != null && !tutorAnterior.equals(docente)) {
            repo.actualizar(tutorAnterior, tutorAnterior);
        }
        repoGrupos.actualizar(grupo, grupo);
        repo.actualizar(docente, docente);

        String detalleAnterior = (tutorAnterior != null && !tutorAnterior.equals(docente))
                ? String.format(" previous=%s", tutorAnterior.getCodigo())
                : "";
        logger.audit(String.format(
                "op=LINK entity=DOCENTE id=%s -> GRUPO id=%s | role=TUTOR%s",
                docente.getCodigo(), grupo.getNombre(), detalleAnterior));
        publisher.notificar(TipoEvento.GRUPOS);
        publisher.notificar(TipoEvento.DOCENTES);
    }
}

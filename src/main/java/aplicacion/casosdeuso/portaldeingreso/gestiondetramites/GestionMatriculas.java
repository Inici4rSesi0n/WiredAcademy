/*
 * SPDX-FileCopyrightText: 2026 inici4rsesi0n
 * SPDX-License-Identifier: GPL-3.0-or-later
 */
package aplicacion.casosdeuso.portaldeingreso.gestiondetramites;
import dominio.modelo.*;
import dominio.puerto.externo.EventPublisher;
import dominio.puerto.externo.LoggerPort;
import dominio.puerto.repositorio.RepositorioMatriculas;
import org.springframework.stereotype.Service;
import java.util.List;
/**
 *
 * @author inici4rsesi0n
 */
@Service
public class GestionMatriculas {
    private final RepositorioMatriculas repo;
    private final LoggerPort logger;
    private final EventPublisher publisher;
    public GestionMatriculas(RepositorioMatriculas repo, LoggerPort logger, EventPublisher publisher) {
        this.repo = repo;
        this.logger = logger;
        this.publisher = publisher;
    }
    public void matricularEstudiante(Estudiante estudiante, PeriodoAcademico periodo, Grupo grupo,
                                     List<Asignatura> asignaturas, String fecha, EstadoMatricula estado) {
        if (estudiante == null || periodo == null || grupo == null)
            throw new IllegalArgumentException("Estudiante, periodo y grupo son obligatorios.");
        if (repo.buscarPorEstudianteYPeriodo(estudiante.getCodigo(), periodo.getNombre()).isPresent())
            throw new IllegalArgumentException("El estudiante ya está matriculado en ese periodo.");
        Matricula m = new Matricula(estudiante, periodo, grupo, asignaturas, fecha, estado);
        repo.agregar(m);
        logger.audit(String.format("op=CREATE entity=MATRICULA id=%s-%s | grupo=%s estado=%s",
                estudiante.getCodigo(), periodo.getNombre(), grupo.getNombre(), estado));
        publisher.notificar(TipoEvento.MATRICULAS);
    }
    public void matricularPorAsignaturas(Estudiante estudiante, PeriodoAcademico periodo,
                                         List<InscripcionAsignatura> inscripciones, String fecha, EstadoMatricula estado) {
        if (estudiante == null || periodo == null || inscripciones == null || inscripciones.isEmpty())
            throw new IllegalArgumentException("Estudiante, periodo e inscripciones son obligatorios.");
        if (repo.buscarPorEstudianteYPeriodo(estudiante.getCodigo(), periodo.getNombre()).isPresent())
            throw new IllegalArgumentException("El estudiante ya está matriculado en ese periodo.");
        Matricula m = new Matricula(estudiante, periodo, inscripciones, fecha, estado);
        repo.agregar(m);
        logger.audit(String.format("op=CREATE entity=MATRICULA id=%s-%s | mode=UNIVERSIDAD asignaturas=%d estado=%s",
                estudiante.getCodigo(), periodo.getNombre(), inscripciones.size(), estado));
        publisher.notificar(TipoEvento.MATRICULAS);
    }
    public List<Matricula> listarTodas() { return repo.listarTodos(); }
    public Matricula buscarMatricula(String codigoEstudiante, String nombrePeriodo) {
        return repo.buscarPorEstudianteYPeriodo(codigoEstudiante, nombrePeriodo).orElse(null);
    }
    public List<Matricula> listarPorGrupo(String nombreGrupo) { return repo.buscarPorGrupo(nombreGrupo); }
    public void actualizar(Matricula matricula) {
        if (matricula == null) throw new IllegalArgumentException("La matrícula no puede ser nula.");
        repo.actualizar(matricula, matricula);
        logger.audit(String.format("op=UPDATE entity=MATRICULA id=%s-%s | after=%s",
                matricula.getEstudiante().getCodigo(), matricula.getPeriodo().getNombre(), matricula));
        publisher.notificar(TipoEvento.MATRICULAS);
    }
    public void actualizarEstado(Matricula matricula, EstadoMatricula nuevoEstado) {
        if (nuevoEstado == null) throw new IllegalArgumentException("El estado no puede ser nulo.");
        EstadoMatricula antes = matricula.getEstado();
        matricula.setEstado(nuevoEstado);
        repo.actualizar(matricula, matricula);
        logger.audit(String.format("op=UPDATE entity=MATRICULA id=%s-%s | estado=%s->%s",
                matricula.getEstudiante().getCodigo(), matricula.getPeriodo().getNombre(), antes, nuevoEstado));
        publisher.notificar(TipoEvento.MATRICULAS);
    }
    public void eliminarMatricula(Matricula matricula) {
        if (matricula == null) throw new IllegalArgumentException("La matrícula no puede ser nula.");
        String snapshot = matricula.toString();
        repo.eliminar(matricula);
        logger.audit(String.format("op=DELETE entity=MATRICULA id=%s-%s | snapshot=%s",
                matricula.getEstudiante().getCodigo(), matricula.getPeriodo().getNombre(), snapshot));
        publisher.notificar(TipoEvento.MATRICULAS);
    }
}

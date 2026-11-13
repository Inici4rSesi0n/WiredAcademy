/*
 * SPDX-FileCopyrightText: 2026 inici4rsesi0n
 * SPDX-License-Identifier: GPL-3.0-or-later
 */
package aplicacion.casosdeuso.portaldeingreso.miareapersonal;
import dominio.modelo.Asignatura;
import dominio.modelo.Evaluacion;
import dominio.modelo.TipoEvento;
import dominio.puerto.externo.EventPublisher;
import dominio.puerto.externo.LoggerPort;
import dominio.puerto.repositorio.RepositorioEvaluaciones;
import org.springframework.stereotype.Service;
import java.util.List;
/**
 *
 * @author inici4rsesi0n
 */
@Service
public class GestionEvaluaciones {
    private final RepositorioEvaluaciones repo;
    private final LoggerPort logger;
    private final EventPublisher publisher;
    public GestionEvaluaciones(RepositorioEvaluaciones repo, LoggerPort logger, EventPublisher publisher) {
        this.repo = repo;
        this.logger = logger;
        this.publisher = publisher;
    }
    public void crearEvaluacion(String nombre, double peso, Asignatura asignatura) {
        if (nombre == null || nombre.isBlank()) throw new IllegalArgumentException("El nombre no puede estar vacío.");
        if (peso <= 0 || peso > 1) throw new IllegalArgumentException("El peso debe ser mayor a 0 y menor o igual a 1.");
        if (asignatura == null) throw new IllegalArgumentException("La asignatura es obligatoria.");
        repo.agregar(new Evaluacion(null, nombre, peso, asignatura));
        logger.audit(String.format("op=CREATE entity=EVALUACION id=%s | peso=%.2f asignatura=%s", nombre, peso, asignatura.getNombre()));
        publisher.notificar(TipoEvento.EVALUACIONES);
    }
    public List<Evaluacion> listarPorAsignatura(String nombreAsignatura) {
        return repo.buscarPorAsignatura(nombreAsignatura);
    }
    public void eliminarEvaluacion(Evaluacion evaluacion) {
        if (evaluacion == null) throw new IllegalArgumentException("La evaluación no puede ser nula.");
        repo.eliminar(evaluacion);
        logger.audit(String.format("op=DELETE entity=EVALUACION id=%s", evaluacion.getNombre()));
        publisher.notificar(TipoEvento.EVALUACIONES);
    }
}

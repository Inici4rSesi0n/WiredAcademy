/*
 * SPDX-FileCopyrightText: 2026 inici4rsesi0n
 * SPDX-License-Identifier: GPL-3.0-or-later
 */
package aplicacion.casosdeuso.portaldeingreso.miareapersonal;
import dominio.modelo.Calificacion;
import dominio.modelo.Estudiante;
import dominio.modelo.Evaluacion;
import dominio.modelo.TipoEvento;
import dominio.puerto.externo.EventPublisher;
import dominio.puerto.externo.LoggerPort;
import dominio.puerto.repositorio.RepositorioCalificaciones;
import org.springframework.stereotype.Service;
import java.util.List;
/**
 *
 * @author inici4rsesi0n
 */
@Service
public class GestionCalificaciones {
    private final RepositorioCalificaciones repo;
    private final LoggerPort logger;
    private final EventPublisher publisher;
    public GestionCalificaciones(RepositorioCalificaciones repo, LoggerPort logger, EventPublisher publisher) {
        this.repo = repo;
        this.logger = logger;
        this.publisher = publisher;
    }
    public void registrarNota(Evaluacion evaluacion, Estudiante estudiante, Double nota) {
        if (evaluacion == null || estudiante == null) throw new IllegalArgumentException("Evaluación y estudiante son obligatorios.");
        if (nota != null && (nota < 0 || nota > 20)) throw new IllegalArgumentException("La nota debe estar entre 0 y 20.");
        repo.agregar(new Calificacion(null, evaluacion, estudiante, nota));
        logger.audit(String.format("op=CREATE entity=CALIFICACION eval=%s est=%s nota=%.2f", evaluacion.getNombre(), estudiante.getCodigo(), nota));
        publisher.notificar(TipoEvento.CALIFICACIONES);
    }
    public List<Calificacion> listarPorEstudiante(String codigo) {
        return repo.buscarPorEstudiante(codigo);
    }
    public List<Calificacion> listarPorEvaluacion(Long idEvaluacion) {
        return repo.buscarPorEvaluacion(idEvaluacion);
    }
    public double calcularPromedioPonderado(String codigoEstudiante, List<Evaluacion> evaluaciones) {
        List<Calificacion> notasEstudiante = repo.buscarPorEstudiante(codigoEstudiante);
        double sumaPonderada = 0.0;
        double sumaPesos = 0.0;
        for (Evaluacion ev : evaluaciones) {
            Calificacion c = notasEstudiante.stream()
                    .filter(n -> n.getEvaluacion().getId().equals(ev.getId()))
                    .findFirst().orElse(null);
            if (c != null && c.getNota() != null) {
                sumaPonderada += c.getNota() * ev.getPeso();
                sumaPesos += ev.getPeso();
            }
        }
        return sumaPesos > 0 ? sumaPonderada / sumaPesos : 0.0;
    }
}

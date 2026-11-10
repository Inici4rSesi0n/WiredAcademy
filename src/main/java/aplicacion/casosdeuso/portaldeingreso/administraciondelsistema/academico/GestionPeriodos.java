/*
 * SPDX-FileCopyrightText: 2026 inici4rsesi0n
 * SPDX-License-Identifier: GPL-3.0-or-later
 */
package aplicacion.casosdeuso.portaldeingreso.administraciondelsistema.academico;
import dominio.modelo.PeriodoAcademico;
import dominio.puerto.externo.LoggerPort;
import dominio.puerto.externo.EventPublisher;
import dominio.modelo.TipoEvento;
import dominio.puerto.repositorio.RepositorioPeriodos;
import org.springframework.stereotype.Service;
import java.util.List;
/**
 *
 * @author inici4rsesi0n
 */
@Service
public class GestionPeriodos {
    private final RepositorioPeriodos repo;
    private final LoggerPort logger;
    private final EventPublisher publisher;
    public GestionPeriodos(RepositorioPeriodos repo, LoggerPort logger, EventPublisher publisher) {
        this.repo = repo;
        this.logger = logger;
        this.publisher = publisher;
    }
    public void crearPeriodo(String nombre, String fechaInicio, String fechaFin, String estado) {
        if (nombre == null || nombre.isBlank()) throw new IllegalArgumentException("El nombre no puede estar vacío.");
        if (repo.buscarPorNombre(nombre).isPresent()) throw new IllegalArgumentException("Ya existe un periodo con ese nombre.");
        String estadoFinal = (estado != null && !estado.isBlank()) ? estado : "Activo";
        repo.agregar(new PeriodoAcademico(nombre, fechaInicio, fechaFin, estadoFinal));
        logger.audit(String.format("op=CREATE entity=PERIODO id=%s | inicio=%s fin=%s estado=%s", nombre, fechaInicio, fechaFin, estadoFinal));
        publisher.notificar(TipoEvento.PERIODOS);
    }
    public PeriodoAcademico buscarPorNombre(String nombre) { return repo.buscarPorNombre(nombre).orElse(null); }
    public List<PeriodoAcademico> listarTodos() { return repo.listarTodos(); }
    public void actualizarPeriodo(PeriodoAcademico original, String nuevoNombre, String nuevaFechaInicio, String nuevaFechaFin, String nuevoEstado) {
        if (nuevoNombre == null || nuevoNombre.isBlank()) throw new IllegalArgumentException("El nombre no puede estar vacío.");
        String antes = original.toString();
        PeriodoAcademico actualizado = new PeriodoAcademico(nuevoNombre, nuevaFechaInicio, nuevaFechaFin, nuevoEstado);
        repo.actualizar(original, actualizado);
        logger.audit(String.format("op=UPDATE entity=PERIODO id=%s | before=%s after=%s", nuevoNombre, antes, actualizado));
        publisher.notificar(TipoEvento.PERIODOS);
    }
    public void eliminarPeriodo(PeriodoAcademico periodo) {
        if (periodo == null) throw new IllegalArgumentException("El periodo no puede ser nulo");
        String snapshot = periodo.toString();
        repo.eliminar(periodo);
        logger.audit(String.format("op=DELETE entity=PERIODO id=%s | snapshot=%s", periodo.getNombre(), snapshot));
        publisher.notificar(TipoEvento.PERIODOS);
    }
}

/*
 * SPDX-FileCopyrightText: 2026 inici4rsesi0n
 * SPDX-License-Identifier: GPL-3.0-or-later
 */
package aplicacion.casosdeuso.portaldeingreso.administraciondelsistema.academico;
import dominio.modelo.Asignatura;
import dominio.puerto.externo.LoggerPort;
import dominio.puerto.externo.EventPublisher;
import dominio.modelo.TipoEvento;
import dominio.puerto.repositorio.RepositorioAsignaturas;
import org.springframework.stereotype.Service;
import java.util.List;
/**
 *
 * @author inici4rsesi0n
 */
@Service
public class GestionAsignaturas {
    private final RepositorioAsignaturas repo;
    private final LoggerPort logger;
    private final EventPublisher publisher;
    public GestionAsignaturas(RepositorioAsignaturas repo, LoggerPort logger, EventPublisher publisher) {
        this.repo = repo;
        this.logger = logger;
        this.publisher = publisher;
    }
    public void crearAsignatura(String nombre) {
        if (nombre == null || nombre.isBlank()) throw new IllegalArgumentException("El nombre no puede estar vacío.");
        if (repo.buscarPorNombre(nombre).isPresent()) throw new IllegalArgumentException("Ya existe una asignatura con ese nombre.");
        repo.agregar(new Asignatura(nombre));
        logger.audit(String.format("op=CREATE entity=ASIGNATURA id=%s", nombre));
        publisher.notificar(TipoEvento.ASIGNATURAS);
    }
    public List<Asignatura> listarTodos() { return repo.listarTodos(); }
    public Asignatura buscarAsignatura(String nombre) { return repo.buscarPorNombre(nombre).orElse(null); }
    public void actualizarAsignatura(Asignatura original, String nuevoNombre) {
        if (nuevoNombre == null || nuevoNombre.isBlank()) throw new IllegalArgumentException("El nuevo nombre no puede estar vacío.");
        String antes = original.getNombre();
        Asignatura actualizada = new Asignatura(nuevoNombre);
        repo.actualizar(original, actualizada);
        logger.audit(String.format("op=UPDATE entity=ASIGNATURA id=%s | before=%s after=%s", nuevoNombre, antes, nuevoNombre));
        publisher.notificar(TipoEvento.ASIGNATURAS);
    }
    public void eliminarAsignatura(Asignatura asignatura) {
        if (asignatura == null) throw new IllegalArgumentException("La asignatura no puede ser nula");
        String nombre = asignatura.getNombre();
        repo.eliminar(asignatura);
        logger.audit(String.format("op=DELETE entity=ASIGNATURA id=%s", nombre));
        publisher.notificar(TipoEvento.ASIGNATURAS);
    }
}

/*
 * SPDX-FileCopyrightText: 2026 inici4rsesi0n
 * SPDX-License-Identifier: GPL-3.0-or-later
 */
package aplicacion.casosdeuso.portaldeingreso.administraciondelsistema.academico;
import dominio.modelo.Aula;
import dominio.modelo.ModalidadAula;
import dominio.puerto.externo.LoggerPort;
import dominio.puerto.externo.EventPublisher;
import dominio.modelo.TipoEvento;
import dominio.puerto.repositorio.RepositorioAulas;
import org.springframework.stereotype.Service;
import java.util.List;
/**
 *
 * @author inici4rsesi0n
 */
@Service
public class GestionAulas {
    private final RepositorioAulas repo;
    private final LoggerPort logger;
    private final EventPublisher publisher;
    public GestionAulas(RepositorioAulas repo, LoggerPort logger, EventPublisher publisher) {
        this.repo = repo;
        this.logger = logger;
        this.publisher = publisher;
    }
    public void crearAula(String nombre, int capacidad, String ubicacion, String tipo, ModalidadAula modalidad) {
        if (nombre == null || nombre.isBlank()) throw new IllegalArgumentException("El nombre no puede estar vacío.");
        if (capacidad <= 0) throw new IllegalArgumentException("La capacidad debe ser mayor a 0.");
        if (repo.buscarPorNombre(nombre).isPresent()) throw new IllegalArgumentException("Ya existe un aula con ese nombre.");
        repo.agregar(new Aula(nombre, capacidad, ubicacion, tipo, modalidad));
        logger.audit(String.format("op=CREATE entity=AULA id=%s | capacidad=%d modalidad=%s", nombre, capacidad, modalidad));
        publisher.notificar(TipoEvento.AULAS);
    }
    public List<Aula> listarTodos() { return repo.listarTodos(); }
    public Aula buscarAula(String nombre) { return repo.buscarPorNombre(nombre).orElse(null); }
    public void actualizarAula(Aula original, String nuevoNombre, int nuevaCapacidad, String nuevaUbicacion, String nuevoTipo, ModalidadAula nuevaModalidad) {
        if (nuevoNombre == null || nuevoNombre.isBlank()) throw new IllegalArgumentException("El nombre no puede estar vacío.");
        if (nuevaCapacidad <= 0) throw new IllegalArgumentException("La capacidad debe ser mayor a 0.");
        String antes = original.toString();
        Aula actualizada = new Aula(nuevoNombre, nuevaCapacidad, nuevaUbicacion, nuevoTipo, nuevaModalidad);
        repo.actualizar(original, actualizada);
        logger.audit(String.format("op=UPDATE entity=AULA id=%s | before=%s after=%s", nuevoNombre, antes, actualizada));
        publisher.notificar(TipoEvento.AULAS);
    }
    public void eliminarAula(Aula aula) {
        if (aula == null) throw new IllegalArgumentException("El aula no puede ser nula");
        String snapshot = aula.toString();
        repo.eliminar(aula);
        logger.audit(String.format("op=DELETE entity=AULA id=%s | snapshot=%s", aula.getNombre(), snapshot));
        publisher.notificar(TipoEvento.AULAS);
    }
}

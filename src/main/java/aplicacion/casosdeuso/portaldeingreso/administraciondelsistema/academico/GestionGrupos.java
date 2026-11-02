package aplicacion.casosdeuso.portaldeingreso.administraciondelsistema.academico;
import dominio.modelo.Docente;
import dominio.modelo.Grado;
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
public class GestionGrupos {
    private final RepositorioGrupos repo;
    private final RepositorioDocentes repoDocentes;
    private final LoggerPort logger;
    private final EventPublisher publisher;
    public GestionGrupos(RepositorioGrupos repo, RepositorioDocentes repoDocentes, LoggerPort logger, EventPublisher publisher) {
        this.repo = repo;
        this.repoDocentes = repoDocentes;
        this.logger = logger;
        this.publisher = publisher;
    }
    public void crearGrupo(String nombre, Grado grado) {
        if (nombre == null || nombre.isBlank()) throw new IllegalArgumentException("El nombre no puede estar vacío.");
        if (grado == null) throw new IllegalArgumentException("El grado no puede ser nulo.");
        if (repo.buscarPorNombre(nombre).isPresent()) throw new IllegalArgumentException("Ya existe un grupo con ese nombre.");
        repo.agregar(new Grupo(nombre, grado));
        logger.audit(String.format("op=CREATE entity=GRUPO id=%s | grado=%s", nombre, grado.getNombre()));
        publisher.notificar(TipoEvento.GRUPOS);
    }
    public Grupo buscarPorNombre(String nombre) { return repo.buscarPorNombre(nombre).orElse(null); }
    public List<Grupo> listarTodos() { return repo.listarTodos(); }
    public List<Grupo> buscarPorNombreYGrado(String nombreGrupo, String nombreGrado) { return repo.buscarPorNombreYGrado(nombreGrupo, nombreGrado); }
    public void actualizarGrupo(Grupo original, String nuevoNombre, Grado nuevoGrado) {
        if (nuevoNombre == null || nuevoNombre.isBlank()) throw new IllegalArgumentException("El nombre no puede estar vacío.");
        String antes = original.toString();
        Grupo actualizado = new Grupo(nuevoNombre, nuevoGrado);
        actualizado.asignarTutor(original.getTutor());
        for (var e : original.getEstudiantes()) actualizado.agregarEstudiante(e);
        repo.actualizar(original, actualizado);
        logger.audit(String.format("op=UPDATE entity=GRUPO id=%s | before=%s after=%s", nuevoNombre, antes, actualizado));
        publisher.notificar(TipoEvento.GRUPOS);
    }
    public void eliminarGrupo(Grupo grupo) {
        if (grupo == null) throw new IllegalArgumentException("El grupo no puede ser nulo.");
        String snapshot = grupo.toString();
        repo.eliminar(grupo);
        logger.audit(String.format("op=DELETE entity=GRUPO id=%s | snapshot=%s", grupo.getNombre(), snapshot));
        publisher.notificar(TipoEvento.GRUPOS);
    }
    @Transactional
    public void asignarTutor(Grupo grupo, Docente tutor) {
        if (grupo == null || tutor == null) throw new IllegalArgumentException("Grupo y tutor son obligatorios.");
        Docente tutorAnterior = grupo.getTutor();
        grupo.asignarTutor(tutor);
        if (tutorAnterior != null && !tutorAnterior.equals(tutor)) repoDocentes.actualizar(tutorAnterior, tutorAnterior);
        repoDocentes.actualizar(tutor, tutor);
        repo.actualizar(grupo, grupo);
        logger.audit(String.format("op=LINK entity=GRUPO id=%s -> DOCENTE id=%s | role=TUTOR", grupo.getNombre(), tutor.getCodigo()));
        publisher.notificar(TipoEvento.DOCENTES);
        publisher.notificar(TipoEvento.GRUPOS);
    }
    @Transactional
    public void removerTutor(Grupo grupo) {
        if (grupo == null) throw new IllegalArgumentException("El grupo no puede ser nulo.");
        Docente tutorAnterior = grupo.getTutor();
        if (tutorAnterior == null) return;
        grupo.asignarTutor(null);
        repoDocentes.actualizar(tutorAnterior, tutorAnterior);
        repo.actualizar(grupo, grupo);
        logger.audit(String.format("op=UNLINK entity=GRUPO id=%s -> DOCENTE id=%s | role=TUTOR", grupo.getNombre(), tutorAnterior.getCodigo()));
        publisher.notificar(TipoEvento.DOCENTES);
        publisher.notificar(TipoEvento.GRUPOS);
    }
}

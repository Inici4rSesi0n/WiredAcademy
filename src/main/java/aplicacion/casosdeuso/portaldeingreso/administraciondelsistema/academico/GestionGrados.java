package aplicacion.casosdeuso.portaldeingreso.administraciondelsistema.academico;
import dominio.modelo.Grado;
import dominio.puerto.externo.LoggerPort;
import dominio.puerto.externo.EventPublisher;
import dominio.modelo.TipoEvento;
import dominio.puerto.repositorio.RepositorioGrados;
import org.springframework.stereotype.Service;
import java.util.List;
/**
 *
 * @author inici4rsesi0n
 */
@Service
public class GestionGrados {
    private final RepositorioGrados repo;
    private final LoggerPort logger;
    private final EventPublisher publisher;
    public GestionGrados(RepositorioGrados repo, LoggerPort logger, EventPublisher publisher) {
        this.repo = repo;
        this.logger = logger;
        this.publisher = publisher;
    }
    public void crearGrado(String nombre, String nivel) {
        if (nombre == null || nombre.isBlank()) throw new IllegalArgumentException("El nombre no puede estar vacío.");
        if (repo.buscarPorNombreYNivel(nombre, nivel).isPresent()) throw new IllegalArgumentException("Ya existe un grado con ese nombre y nivel.");
        repo.agregar(new Grado(nombre, nivel));
        logger.audit(String.format("op=CREATE entity=GRADO id=%s | nivel=%s", nombre, nivel));
        publisher.notificar(TipoEvento.GRADOS);
    }
    public Grado buscarPorNombre(String nombre) { return repo.buscarPorNombre(nombre).orElse(null); }
    public Grado buscarPorNombreYNivel(String nombre, String nivel) { return repo.buscarPorNombreYNivel(nombre, nivel).orElse(null); }
    public List<Grado> listarTodos() { return repo.listarTodos(); }
    public void actualizarGrado(Grado original, String nuevoNombre, String nuevoNivel) {
        if (nuevoNombre == null || nuevoNombre.isBlank()) throw new IllegalArgumentException("El nombre no puede estar vacío.");
        String antes = original.toString();
        Grado actualizado = new Grado(nuevoNombre, nuevoNivel);
        repo.actualizar(original, actualizado);
        logger.audit(String.format("op=UPDATE entity=GRADO id=%s | before=%s after=%s", nuevoNombre, antes, actualizado));
        publisher.notificar(TipoEvento.GRADOS);
    }
    public void eliminarGrado(Grado grado) {
        if (grado == null) throw new IllegalArgumentException("El grado no puede ser nulo");
        String snapshot = grado.toString();
        repo.eliminar(grado);
        logger.audit(String.format("op=DELETE entity=GRADO id=%s | snapshot=%s", grado.getNombre(), snapshot));
        publisher.notificar(TipoEvento.GRADOS);
    }
}

package aplicacion.casosdeuso.portaldeingreso.administraciondelsistema.usuarios;
import aplicacion.dto.CrearUsuarioCommand;
import aplicacion.servicio.BuscadorUsuario;
import dominio.modelo.*;
import dominio.puerto.externo.EventPublisher;
import dominio.puerto.externo.LoggerPort;
import dominio.puerto.repositorio.*;
import org.springframework.stereotype.Service;
import java.util.ArrayList;
import java.util.List;
/**
 *
 * @author inici4rsesi0n
 */
@Service
public class GestionUsuarios {
    private final RepositorioAdministradores repoAdmin;
    private final RepositorioDirectores repoDir;
    private final RepositorioDocentes repoDoc;
    private final RepositorioEstudiantes repoEst;
    private final RepositorioSecretarios repoSec;
    private final RepositorioCoordinadores repoCoord;
    private final RepositorioPadres repoPad;
    private final LoggerPort logger;
    private final BuscadorUsuario buscadorUsuario;
    private final EventPublisher publisher;
    public GestionUsuarios(RepositorioAdministradores repoAdmin, RepositorioDirectores repoDir,
                           RepositorioDocentes repoDoc, RepositorioEstudiantes repoEst,
                           RepositorioSecretarios repoSec, RepositorioCoordinadores repoCoord,
                           RepositorioPadres repoPad, LoggerPort logger, BuscadorUsuario buscadorUsuario,
                           EventPublisher publisher) {
        this.repoAdmin = repoAdmin;
        this.repoDir = repoDir;
        this.repoDoc = repoDoc;
        this.repoEst = repoEst;
        this.repoSec = repoSec;
        this.repoCoord = repoCoord;
        this.repoPad = repoPad;
        this.logger = logger;
        this.buscadorUsuario = buscadorUsuario;
        this.publisher = publisher;
    }
    public List<Usuario> listarTodos() {
        logger.debug("Listando todos los usuarios del sistema.");
        List<Usuario> todos = new ArrayList<>();
        todos.addAll(repoAdmin.listarTodos());
        todos.addAll(repoDir.listarTodos());
        todos.addAll(repoDoc.listarTodos());
        todos.addAll(repoEst.listarTodos());
        todos.addAll(repoSec.listarTodos());
        todos.addAll(repoCoord.listarTodos());
        todos.addAll(repoPad.listarTodos());
        logger.debug("Total de usuarios encontrados: {}", todos.size());
        return todos;
    }
    public Usuario buscarPorCodigo(String codigo){ return buscadorUsuario.buscarPorCodigo(codigo); }
    public boolean existeDni(String dni, Usuario excluir) {
        return listarTodos().stream().anyMatch(u -> u.getDni().equalsIgnoreCase(dni) && !u.equals(excluir));
    }
    public void agregarUsuario(CrearUsuarioCommand cmd) {
        validarUnicidad(cmd.codigo(), cmd.dni(), null);
        switch (cmd.rol()) {
            case ADMINISTRADOR -> repoAdmin.agregar(new Administrador(
                    cmd.codigo(), cmd.hash(), cmd.dni(), cmd.nombre(), cmd.apellido(), cmd.edad()));
            case DIRECTOR -> repoDir.agregar(new Director(
                    cmd.codigo(), cmd.hash(), cmd.dni(), cmd.nombre(), cmd.apellido(), cmd.edad()));
            case SECRETARIO -> repoSec.agregar(new Secretario(
                    cmd.codigo(), cmd.hash(), cmd.dni(), cmd.nombre(), cmd.apellido(), cmd.edad()));
            case COORDINADOR -> repoCoord.agregar(new CoordinadorAcademico(
                    cmd.codigo(), cmd.hash(), cmd.dni(), cmd.nombre(), cmd.apellido(), cmd.edad()));
            case DOCENTE -> {
                String esp = (cmd.especialidad() != null && !cmd.especialidad().isBlank())
                        ? cmd.especialidad() : "Sin asignar";
                repoDoc.agregar(new Docente(
                        cmd.codigo(), cmd.hash(), cmd.dni(), cmd.nombre(), cmd.apellido(),
                        cmd.edad(), esp, new ArrayList<>()));
            }
            case ESTUDIANTE -> repoEst.agregar(new Estudiante(
                    cmd.codigo(), cmd.hash(), cmd.dni(), cmd.nombre(), cmd.apellido(), cmd.edad()));
            case PADRE -> repoPad.agregar(new Padre(
                    cmd.codigo(), cmd.hash(), cmd.dni(), cmd.nombre(), cmd.apellido(),
                    cmd.edad(), new ArrayList<>()));
        }
        logger.audit(String.format("op=CREATE entity=%s id=%s | nombre=%s %s dni=%s",
                cmd.rol(), cmd.codigo(), cmd.nombre(), cmd.apellido(), cmd.dni()));
        publisher.notificar(TipoEvento.USUARIOS);
        notificarEventoDeRol(cmd.rol());
    }
    public void actualizarUsuario(Usuario usuario) {
        if (usuario == null) throw new IllegalArgumentException("El usuario no puede ser nulo");
        String antes = usuario.toString();
        switch (usuario.getRol()) {
            case ADMINISTRADOR -> repoAdmin.actualizar((Administrador) usuario, (Administrador) usuario);
            case DIRECTOR -> repoDir.actualizar((Director) usuario, (Director) usuario);
            case DOCENTE -> repoDoc.actualizar((Docente) usuario, (Docente) usuario);
            case ESTUDIANTE -> repoEst.actualizar((Estudiante) usuario, (Estudiante) usuario);
            case SECRETARIO -> repoSec.actualizar((Secretario) usuario, (Secretario) usuario);
            case COORDINADOR -> repoCoord.actualizar((CoordinadorAcademico) usuario, (CoordinadorAcademico) usuario);
            case PADRE -> repoPad.actualizar((Padre) usuario, (Padre) usuario);
        }
        logger.audit(String.format("op=UPDATE entity=%s id=%s | before=%s after=%s",
                usuario.getRol(), usuario.getCodigo(), antes, usuario));
        publisher.notificar(TipoEvento.USUARIOS);
        notificarEventoDeRol(usuario.getRol());
    }
    public void eliminarUsuario(Usuario usuario) {
        if (usuario == null) throw new IllegalArgumentException("El usuario no puede ser nulo");
        String snapshot = usuario.toString();
        switch (usuario.getRol()) {
            case ADMINISTRADOR -> repoAdmin.eliminar((Administrador) usuario);
            case DIRECTOR -> repoDir.eliminar((Director) usuario);
            case DOCENTE -> repoDoc.eliminar((Docente) usuario);
            case ESTUDIANTE -> repoEst.eliminar((Estudiante) usuario);
            case SECRETARIO -> repoSec.eliminar((Secretario) usuario);
            case COORDINADOR -> repoCoord.eliminar((CoordinadorAcademico) usuario);
            case PADRE -> repoPad.eliminar((Padre) usuario);
        }
        logger.audit(String.format("op=DELETE entity=%s id=%s | snapshot=%s",
                usuario.getRol(), usuario.getCodigo(), snapshot));
        publisher.notificar(TipoEvento.USUARIOS);
        notificarEventoDeRol(usuario.getRol());
    }
    private void notificarEventoDeRol(Usuario.Rol rol) {
        TipoEvento evento = switch (rol) {
            case DOCENTE       -> TipoEvento.DOCENTES;
            case ESTUDIANTE    -> TipoEvento.ESTUDIANTES;
            case PADRE         -> TipoEvento.PADRES;
            case COORDINADOR   -> TipoEvento.COORDINADORES;
            case ADMINISTRADOR -> TipoEvento.ADMINISTRADORES;
            case DIRECTOR      -> TipoEvento.DIRECTORES;
            case SECRETARIO    -> TipoEvento.SECRETARIOS;
        };
        publisher.notificar(evento);
    }
    private void validarUnicidad(String codigo, String dni, Usuario excluir) {
        if (buscarPorCodigo(codigo) != null) {
            throw new IllegalArgumentException("Ya existe un usuario con el código " + codigo);
        }
        if (existeDni(dni, excluir)) {
            throw new IllegalArgumentException("Ya existe un usuario con el DNI " + dni);
        }
    }
}

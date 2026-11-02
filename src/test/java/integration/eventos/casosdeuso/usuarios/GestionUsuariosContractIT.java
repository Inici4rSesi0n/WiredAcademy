package integration.eventos.casosdeuso.usuarios;
import aplicacion.casosdeuso.portaldeingreso.administraciondelsistema.usuarios.GestionUsuarios;
import aplicacion.dto.CrearUsuarioCommand;
import dominio.modelo.Docente;
import dominio.modelo.TipoEvento;
import dominio.modelo.Usuario;
import integration.eventos.BaseContractTest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;
import static org.awaitility.Awaitility.await;
import static org.junit.jupiter.api.Assertions.*;
/**
 *
 * @author inici4rsesi0n
 */
class GestionUsuariosContractIT extends BaseContractTest {
    @Autowired private GestionUsuarios gestionUsuarios;
    private CrearUsuarioCommand cmdDocente(String codigo, String dni) {
        return new CrearUsuarioCommand(codigo, "hash", dni, "User", "CT", 30, Usuario.Rol.DOCENTE, "Mat");
    }
    @Test
    void agregarUsuario_docente_emiteUSUARIOS() {
        gestionUsuarios.agregarUsuario(cmdDocente("CT-Usr-001", "95000001"));
        await().atMost(2, TimeUnit.SECONDS).until(() -> recorder.contiene(TipoEvento.USUARIOS));
        assertTrue(recorder.contiene(TipoEvento.USUARIOS));
    }
    @Test
    void agregarUsuario_docente_emiteDOCENTES() {
        gestionUsuarios.agregarUsuario(cmdDocente("CT-Usr-002", "95000002"));
        await().atMost(2, TimeUnit.SECONDS).until(() -> recorder.contiene(TipoEvento.DOCENTES));
        assertTrue(recorder.contiene(TipoEvento.DOCENTES));
    }
    @Test
    void actualizarUsuario_docente_emiteUSUARIOS() {
        gestionUsuarios.agregarUsuario(cmdDocente("CT-Usr-003", "95000003"));
        Docente actualizado = new Docente("CT-Usr-003", "hash", "95000003", "User", "Updated", 30, "Mat", new ArrayList<>());
        recorder.reset();
        gestionUsuarios.actualizarUsuario(actualizado);
        await().atMost(2, TimeUnit.SECONDS).until(() -> recorder.contiene(TipoEvento.USUARIOS));
        assertTrue(recorder.contiene(TipoEvento.USUARIOS));
    }
    @Test
    void actualizarUsuario_docente_emiteDOCENTES() {
        gestionUsuarios.agregarUsuario(cmdDocente("CT-Usr-004", "95000004"));
        Docente actualizado = new Docente("CT-Usr-004", "hash", "95000004", "User", "Updated", 30, "Mat", new ArrayList<>());
        recorder.reset();
        gestionUsuarios.actualizarUsuario(actualizado);
        await().atMost(2, TimeUnit.SECONDS).until(() -> recorder.contiene(TipoEvento.DOCENTES));
        assertTrue(recorder.contiene(TipoEvento.DOCENTES));
    }
    @Test
    void eliminarUsuario_docente_emiteUSUARIOS() {
        gestionUsuarios.agregarUsuario(cmdDocente("CT-Usr-005", "95000005"));
        Usuario usuario = gestionUsuarios.buscarPorCodigo("CT-Usr-005");
        recorder.reset();
        gestionUsuarios.eliminarUsuario(usuario);
        await().atMost(2, TimeUnit.SECONDS).until(() -> recorder.contiene(TipoEvento.USUARIOS));
        assertTrue(recorder.contiene(TipoEvento.USUARIOS));
    }
    @Test
    void eliminarUsuario_docente_emiteDOCENTES() {
        gestionUsuarios.agregarUsuario(cmdDocente("CT-Usr-006", "95000006"));
        Usuario usuario = gestionUsuarios.buscarPorCodigo("CT-Usr-006");
        recorder.reset();
        gestionUsuarios.eliminarUsuario(usuario);
        await().atMost(2, TimeUnit.SECONDS).until(() -> recorder.contiene(TipoEvento.DOCENTES));
        assertTrue(recorder.contiene(TipoEvento.DOCENTES));
    }
}

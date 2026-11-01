package aplicacion.casosdeuso.portaldeingreso.administraciondelsistema.usuarios;
import aplicacion.dto.CrearUsuarioCommand;
import aplicacion.servicio.BuscadorUsuario;
import dominio.modelo.*;
import dominio.puerto.externo.LoggerPort;
import dominio.puerto.repositorio.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 *
 * @author inici4rsesi0n
 */
class GestionUsuariosTest {

    private RepositorioAdministradores repoAdmin;
    private RepositorioDirectores repoDir;
    private RepositorioDocentes repoDoc;
    private RepositorioEstudiantes repoEst;
    private RepositorioSecretarios repoSec;
    private RepositorioCoordinadores repoCoord;
    private RepositorioPadres repoPad;
    private LoggerPort loggerPort;
    private BuscadorUsuario buscadorUsuario;
    private GestionUsuarios casoUso;

    @BeforeEach
    void setUp() {
        repoAdmin = mock(RepositorioAdministradores.class);
        repoDir = mock(RepositorioDirectores.class);
        repoDoc = mock(RepositorioDocentes.class);
        repoEst = mock(RepositorioEstudiantes.class);
        repoSec = mock(RepositorioSecretarios.class);
        repoCoord = mock(RepositorioCoordinadores.class);
        repoPad = mock(RepositorioPadres.class);
        loggerPort = mock(LoggerPort.class);

        buscadorUsuario = new BuscadorUsuario(repoAdmin, repoDir, repoDoc, repoEst, repoSec, repoCoord, repoPad);

        casoUso = new GestionUsuarios(repoAdmin, repoDir, repoDoc, repoEst, repoSec, repoCoord, repoPad,
                loggerPort, buscadorUsuario);
    }



    @Test
    void agregarUsuario_codigoDuplicado_debeLanzarExcepcion() {
        when(repoAdmin.buscarPorCodigo("A001"))
                .thenReturn(Optional.of(new Administrador("A001", "hash", "111", "Admin", "Uno", 30)));
        assertThrows(IllegalArgumentException.class,
            () -> casoUso.agregarUsuario(new CrearUsuarioCommand(
                    "A001", "hash", "222", "Otro", "Dos", 35, Usuario.Rol.ADMINISTRADOR, null)));
    }

    @Test
    void existeDni_debeRetornarTrueSiYaExiste() {
        mockListarTodosConDni("111");

        assertTrue(casoUso.existeDni("111", null));
    }

    @Test
    void existeDni_debeRetornarFalseSiNoExiste() {
        mockListarTodosVacios();

        assertFalse(casoUso.existeDni("999", null));
    }

    @Test
    void listarTodos_debeCombinarTodosLosRepositorios() {
        when(repoAdmin.listarTodos()).thenReturn(List.of(new Administrador("A001", "hash", "111", "Admin", "Uno", 30)));
        when(repoDir.listarTodos()).thenReturn(List.of());
        when(repoDoc.listarTodos()).thenReturn(List.of(new Docente("D001", "hash", "222", "Doc", "Uno", 30, "Mat", new ArrayList<>())));
        when(repoEst.listarTodos()).thenReturn(List.of(new Estudiante("E001", "hash", "333", "Est", "Uno", 15)));
        when(repoSec.listarTodos()).thenReturn(List.of());
        when(repoCoord.listarTodos()).thenReturn(List.of());
        when(repoPad.listarTodos()).thenReturn(List.of());

        List<Usuario> resultado = casoUso.listarTodos();

        assertEquals(3, resultado.size());
    }

    @Test
    void actualizarUsuario_administrador_debeActualizar() {
        Administrador admin = new Administrador("A001", "hash", "111", "Admin", "Uno", 30);

        casoUso.actualizarUsuario(admin);

        verify(repoAdmin).actualizar(admin, admin);
    }


    @Test
    void actualizarUsuario_noDebePerderAsignaturasDeDocente() {
        Docente docente = new Docente("D001", "hash", "111", "Juan", "Perez", 30, "Mat", new ArrayList<>());
        Asignatura mat = new Asignatura("Matemáticas");
        Asignatura fis = new Asignatura("Física");
        docente.agregarAsignatura(mat);
        docente.agregarAsignatura(fis);
        docente.setNombre("Juan Carlos");
        casoUso.actualizarUsuario(docente);
        assertEquals(2, docente.getAsignaturas().size(), "Editar el nombre no debe afectar las asignaturas");
        assertTrue(docente.getAsignaturas().contains(mat));
        assertTrue(docente.getAsignaturas().contains(fis));
    }

    private void mockListarTodosVacios() {
        when(repoAdmin.listarTodos()).thenReturn(List.of());
        when(repoDir.listarTodos()).thenReturn(List.of());
        when(repoDoc.listarTodos()).thenReturn(List.of());
        when(repoEst.listarTodos()).thenReturn(List.of());
        when(repoSec.listarTodos()).thenReturn(List.of());
        when(repoCoord.listarTodos()).thenReturn(List.of());
        when(repoPad.listarTodos()).thenReturn(List.of());
    }

    private void mockListarTodosConDni(String dni) {
        when(repoAdmin.listarTodos()).thenReturn(List.of(new Administrador("A001", "hash", dni, "Admin", "Uno", 30)));
        when(repoDir.listarTodos()).thenReturn(List.of());
        when(repoDoc.listarTodos()).thenReturn(List.of());
        when(repoEst.listarTodos()).thenReturn(List.of());
        when(repoSec.listarTodos()).thenReturn(List.of());
        when(repoCoord.listarTodos()).thenReturn(List.of());
        when(repoPad.listarTodos()).thenReturn(List.of());
    }
}

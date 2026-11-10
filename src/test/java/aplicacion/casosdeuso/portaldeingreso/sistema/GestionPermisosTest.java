/*
 * SPDX-FileCopyrightText: 2026 inici4rsesi0n
 * SPDX-License-Identifier: GPL-3.0-or-later
 */
package aplicacion.casosdeuso.portaldeingreso.sistema;
import aplicacion.casosdeuso.portaldeingreso.sistema.GestionPermisos.SeccionMenu;
import dominio.modelo.Usuario;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
/**
 *
 * @author inici4rsesi0n
 */
class GestionPermisosTest {
    private GestionPermisos gestionPermisos;
    @BeforeEach
    void setUp() {
        gestionPermisos = new GestionPermisos();
    }
    @Test
    void administrador_debeVerTodasLasSecciones() {
        Usuario.Rol rol = Usuario.Rol.ADMINISTRADOR;
        for (SeccionMenu s : SeccionMenu.values()) {
            assertTrue(gestionPermisos.esSeccionVisible(rol, s),
                    "ADMINISTRADOR debe ver la sección " + s);
        }
    }

    @Test
    void docente_debeVerSoloSeccionesPermitidas() {
        Usuario.Rol rol = Usuario.Rol.DOCENTE;
        assertTrue(gestionPermisos.esSeccionVisible(rol, SeccionMenu.MIS_CURSOS));
        assertTrue(gestionPermisos.esSeccionVisible(rol, SeccionMenu.SUBIR_MATERIAL));
        assertFalse(gestionPermisos.esSeccionVisible(rol, SeccionMenu.GESTION_USUARIOS));
        assertFalse(gestionPermisos.esSeccionVisible(rol, SeccionMenu.CONFIGURACION_SISTEMA));
        assertFalse(gestionPermisos.esSeccionVisible(rol, SeccionMenu.TRAMITES));
    }

    @Test
    void estudiante_debeVerSoloSeccionesPermitidas() {
        Usuario.Rol rol = Usuario.Rol.ESTUDIANTE;
        assertTrue(gestionPermisos.esSeccionVisible(rol, SeccionMenu.CALIFICACIONES));
        assertTrue(gestionPermisos.esSeccionVisible(rol, SeccionMenu.MATRICULA));
        assertFalse(gestionPermisos.esSeccionVisible(rol, SeccionMenu.SUBIR_MATERIAL));
        assertFalse(gestionPermisos.esSeccionVisible(rol, SeccionMenu.GESTION_USUARIOS));
    }

    @Test
    void padre_debeVerSoloSeccionesPermitidas() {
        Usuario.Rol rol = Usuario.Rol.PADRE;
        assertTrue(gestionPermisos.esSeccionVisible(rol, SeccionMenu.TRAMITES));
        assertTrue(gestionPermisos.esSeccionVisible(rol, SeccionMenu.MATRICULA));
        assertFalse(gestionPermisos.esSeccionVisible(rol, SeccionMenu.GESTION_ACADEMICA));
        assertFalse(gestionPermisos.esSeccionVisible(rol, SeccionMenu.SUBIR_MATERIAL));
    }

    @Test
    void esSeccionVisible_conRolNulo_debeRetornarFalse() {
        assertFalse(gestionPermisos.esSeccionVisible(null, SeccionMenu.MIS_CURSOS));
    }

    @Test
    void esSeccionVisible_conSeccionNula_debeRetornarFalse() {
        assertFalse(gestionPermisos.esSeccionVisible(Usuario.Rol.ADMINISTRADOR, null));
    }

    @Test
    void seccionMenu_codigos_debenSerUnicos() {
        java.util.Set<String> codigos = new java.util.HashSet<>();
        for (SeccionMenu s : SeccionMenu.values()) {
            assertTrue(codigos.add(s.getCodigo()),
                    "Código duplicado en SeccionMenu: " + s.getCodigo());
        }
    }


    @Test
    void puedeAccederPortal_ingreso_paraRolesAutorizados() {
        assertTrue(gestionPermisos.puedeAccederPortal(Usuario.Rol.ADMINISTRADOR, "INGRESO"));
        assertTrue(gestionPermisos.puedeAccederPortal(Usuario.Rol.DIRECTOR, "INGRESO"));
        assertTrue(gestionPermisos.puedeAccederPortal(Usuario.Rol.SECRETARIO, "INGRESO"));
        assertTrue(gestionPermisos.puedeAccederPortal(Usuario.Rol.COORDINADOR, "INGRESO"));
        assertTrue(gestionPermisos.puedeAccederPortal(Usuario.Rol.DOCENTE, "INGRESO"));
        assertTrue(gestionPermisos.puedeAccederPortal(Usuario.Rol.ESTUDIANTE, "INGRESO"));
    }

    @Test
    void puedeAccederPortal_ingreso_padreNoAutorizado() {
        assertFalse(gestionPermisos.puedeAccederPortal(Usuario.Rol.PADRE, "INGRESO"));
    }

    @Test
    void puedeAccederPortal_tramites_paraRolesAutorizados() {
        assertTrue(gestionPermisos.puedeAccederPortal(Usuario.Rol.ESTUDIANTE, "TRAMITES"));
        assertTrue(gestionPermisos.puedeAccederPortal(Usuario.Rol.PADRE, "TRAMITES"));
        assertTrue(gestionPermisos.puedeAccederPortal(Usuario.Rol.SECRETARIO, "TRAMITES"));
    }

    @Test
    void puedeAccederPortal_tramites_administradorNoAutorizado() {
        assertFalse(gestionPermisos.puedeAccederPortal(Usuario.Rol.ADMINISTRADOR, "TRAMITES"));
        assertFalse(gestionPermisos.puedeAccederPortal(Usuario.Rol.DOCENTE, "TRAMITES"));
        assertFalse(gestionPermisos.puedeAccederPortal(Usuario.Rol.DIRECTOR, "TRAMITES"));
    }

    @Test
    void puedeAccederPortal_matricula_paraRolesAutorizados() {
        assertTrue(gestionPermisos.puedeAccederPortal(Usuario.Rol.ESTUDIANTE, "MATRICULA"));
        assertTrue(gestionPermisos.puedeAccederPortal(Usuario.Rol.PADRE, "MATRICULA"));
        assertTrue(gestionPermisos.puedeAccederPortal(Usuario.Rol.SECRETARIO, "MATRICULA"));
    }

    @Test
    void puedeAccederPortal_matricula_administradorNoAutorizado() {
        assertFalse(gestionPermisos.puedeAccederPortal(Usuario.Rol.ADMINISTRADOR, "MATRICULA"));
        assertFalse(gestionPermisos.puedeAccederPortal(Usuario.Rol.DOCENTE, "MATRICULA"));
    }

    @Test
    void puedeAccederPortal_portalInexistente_debeRetornarFalse() {
        assertFalse(gestionPermisos.puedeAccederPortal(Usuario.Rol.ADMINISTRADOR, "PORTAL_RARO"));
    }

    @Test
    void puedeAccederPortal_conRolNulo_debeRetornarFalse() {
        assertFalse(gestionPermisos.puedeAccederPortal(null, "INGRESO"));
    }

    @Test
    void puedeAccederPortal_conPortalNulo_debeRetornarFalse() {
        assertFalse(gestionPermisos.puedeAccederPortal(Usuario.Rol.ADMINISTRADOR, null));
    }
}

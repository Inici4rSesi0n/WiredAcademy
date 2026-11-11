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
    void administrador_noDebeVerMisCursos() {
        assertFalse(gestionPermisos.esSeccionVisible(Usuario.Rol.ADMINISTRADOR, SeccionMenu.MIS_CURSOS));
    }
    @Test
    void administrador_debeVerConfiguracionSistemaYNoPerfil() {
        assertTrue(gestionPermisos.esSeccionVisible(Usuario.Rol.ADMINISTRADOR, SeccionMenu.CONFIGURACION_SISTEMA));
        assertFalse(gestionPermisos.esSeccionVisible(Usuario.Rol.ADMINISTRADOR, SeccionMenu.CONFIGURACION_PERFIL));
    }
    @Test
    void docente_debeVerSoloSeccionesPermitidas() {
        Usuario.Rol rol = Usuario.Rol.DOCENTE;
        assertTrue(gestionPermisos.esSeccionVisible(rol, SeccionMenu.MIS_CURSOS));
        assertTrue(gestionPermisos.esSeccionVisible(rol, SeccionMenu.SUBIR_MATERIAL));
        assertTrue(gestionPermisos.esSeccionVisible(rol, SeccionMenu.CONFIGURACION_PERFIL));
        assertFalse(gestionPermisos.esSeccionVisible(rol, SeccionMenu.GESTION_USUARIOS));
        assertFalse(gestionPermisos.esSeccionVisible(rol, SeccionMenu.MATRICULA));
    }
    @Test
    void estudiante_debeVerSoloSeccionesPermitidas() {
        Usuario.Rol rol = Usuario.Rol.ESTUDIANTE;
        assertTrue(gestionPermisos.esSeccionVisible(rol, SeccionMenu.MIS_CURSOS));
        assertTrue(gestionPermisos.esSeccionVisible(rol, SeccionMenu.CALIFICACIONES));
        assertTrue(gestionPermisos.esSeccionVisible(rol, SeccionMenu.CONFIGURACION_PERFIL));
        assertFalse(gestionPermisos.esSeccionVisible(rol, SeccionMenu.MATRICULA));
        assertFalse(gestionPermisos.esSeccionVisible(rol, SeccionMenu.GESTION_USUARIOS));
    }
    @Test
    void director_debeVerConfiguracionPerfil() {
        assertTrue(gestionPermisos.esSeccionVisible(Usuario.Rol.DIRECTOR, SeccionMenu.CONFIGURACION_PERFIL));
    }
    @Test
    void tesorero_debeVerReportesYPerfil() {
        Usuario.Rol rol = Usuario.Rol.TESORERO;
        assertTrue(gestionPermisos.esSeccionVisible(rol, SeccionMenu.REPORTES));
        assertTrue(gestionPermisos.esSeccionVisible(rol, SeccionMenu.CONFIGURACION_PERFIL));
        assertFalse(gestionPermisos.esSeccionVisible(rol, SeccionMenu.GESTION_ACADEMICA));
    }
    @Test
    void subdirector_debeVerGestionUsuariosAcademicaYPerfil() {
        Usuario.Rol rol = Usuario.Rol.SUBDIRECTOR;
        assertTrue(gestionPermisos.esSeccionVisible(rol, SeccionMenu.GESTION_USUARIOS));
        assertTrue(gestionPermisos.esSeccionVisible(rol, SeccionMenu.GESTION_ACADEMICA));
        assertTrue(gestionPermisos.esSeccionVisible(rol, SeccionMenu.CONFIGURACION_PERFIL));
        assertFalse(gestionPermisos.esSeccionVisible(rol, SeccionMenu.CONFIGURACION_SISTEMA));
    }
    @Test
    void puedeAccederPortal_ingreso() {
        assertTrue(gestionPermisos.puedeAccederPortal(Usuario.Rol.ADMINISTRADOR, "INGRESO"));
        assertTrue(gestionPermisos.puedeAccederPortal(Usuario.Rol.DOCENTE, "INGRESO"));
        assertTrue(gestionPermisos.puedeAccederPortal(Usuario.Rol.ESTUDIANTE, "INGRESO"));
        assertFalse(gestionPermisos.puedeAccederPortal(Usuario.Rol.PADRE, "INGRESO"));
    }
    @Test
    void puedeAccederPortal_tramitesYPagos() {
        assertTrue(gestionPermisos.puedeAccederPortal(Usuario.Rol.ESTUDIANTE, "TRAMITES"));
        assertTrue(gestionPermisos.puedeAccederPortal(Usuario.Rol.DOCENTE, "TRAMITES"));
        assertTrue(gestionPermisos.puedeAccederPortal(Usuario.Rol.ESTUDIANTE, "PAGOS"));
        assertFalse(gestionPermisos.puedeAccederPortal(Usuario.Rol.DOCENTE, "PAGOS"));
    }
}

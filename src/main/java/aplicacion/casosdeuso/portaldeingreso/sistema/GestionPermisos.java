/*
 * SPDX-FileCopyrightText: 2026 inici4rsesi0n
 * SPDX-License-Identifier: GPL-3.0-or-later
 */
package aplicacion.casosdeuso.portaldeingreso.sistema;
import dominio.modelo.Usuario;
import org.springframework.stereotype.Service;
/**
 *
 * @author inici4rsesi0n
 */
@Service
public class GestionPermisos {
    public enum SeccionMenu {
        MIS_CURSOS("MisCursos"),
        MI_HORARIO("MiHorario"),
        CALIFICACIONES("Calificaciones"),
        SUBIR_MATERIAL("SubirMaterial"),
        REGISTRAR_ASISTENCIA("RegistrarAsistencia"),
        TRAMITES("Tramites"),
        MATRICULA("Matricula"),
        GESTION_USUARIOS("GestionUsuarios"),
        GESTION_ACADEMICA("GestionAcademica"),
        REPORTES("Reportes"),
        CONFIGURACION_SISTEMA("ConfiguracionSistema");
        private final String codigo;
        SeccionMenu(String codigo) {
            this.codigo = codigo;
        }
        public String getCodigo() {
            return codigo;
        }
    }
    public boolean esSeccionVisible(Usuario.Rol rol, SeccionMenu seccion) {
        if (rol == null || seccion == null) return false;
        return switch (seccion) {
            case MIS_CURSOS, MI_HORARIO, CALIFICACIONES ->
                    rol == Usuario.Rol.ADMINISTRADOR
                            || rol == Usuario.Rol.DOCENTE
                            || rol == Usuario.Rol.ESTUDIANTE
                            || rol == Usuario.Rol.PADRE;

            case SUBIR_MATERIAL, REGISTRAR_ASISTENCIA ->
                    rol == Usuario.Rol.ADMINISTRADOR
                            || rol == Usuario.Rol.DOCENTE;

            case TRAMITES, MATRICULA ->
                    rol == Usuario.Rol.ADMINISTRADOR
                            || rol == Usuario.Rol.SECRETARIO
                            || rol == Usuario.Rol.ESTUDIANTE
                            || rol == Usuario.Rol.PADRE;

            case GESTION_USUARIOS ->
                    rol == Usuario.Rol.ADMINISTRADOR
                            || rol == Usuario.Rol.DIRECTOR;

            case GESTION_ACADEMICA, REPORTES ->
                    rol == Usuario.Rol.ADMINISTRADOR
                            || rol == Usuario.Rol.DIRECTOR
                            || rol == Usuario.Rol.COORDINADOR;

            case CONFIGURACION_SISTEMA ->
                    rol == Usuario.Rol.ADMINISTRADOR;
        };
    }
    public boolean puedeAccederPortal(Usuario.Rol rol, String portal) {
        if (rol == null || portal == null) return false;
        return switch (portal) {
            case "INGRESO" ->
                    rol == Usuario.Rol.ADMINISTRADOR
                            || rol == Usuario.Rol.DIRECTOR
                            || rol == Usuario.Rol.SECRETARIO
                            || rol == Usuario.Rol.COORDINADOR
                            || rol == Usuario.Rol.DOCENTE
                            || rol == Usuario.Rol.ESTUDIANTE;

            case "TRAMITES", "MATRICULA" ->
                    rol == Usuario.Rol.ESTUDIANTE
                            || rol == Usuario.Rol.PADRE
                            || rol == Usuario.Rol.SECRETARIO;
            default -> false;
        };
    }
}

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
        CONFIGURACION_SISTEMA("ConfiguracionSistema"),
        CONFIGURACION_PERFIL("ConfiguracionPerfil");
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
            case MIS_CURSOS -> rol == Usuario.Rol.DOCENTE || rol == Usuario.Rol.ESTUDIANTE;
            case MI_HORARIO -> rol == Usuario.Rol.ADMINISTRADOR || rol == Usuario.Rol.DIRECTOR || 
                               rol == Usuario.Rol.SUBDIRECTOR || rol == Usuario.Rol.COORDINADOR || 
                               rol == Usuario.Rol.DOCENTE || rol == Usuario.Rol.ESTUDIANTE;
            case CALIFICACIONES -> rol == Usuario.Rol.DOCENTE || rol == Usuario.Rol.ESTUDIANTE;
            case SUBIR_MATERIAL -> rol == Usuario.Rol.ADMINISTRADOR || rol == Usuario.Rol.DIRECTOR || 
                                   rol == Usuario.Rol.SUBDIRECTOR || rol == Usuario.Rol.COORDINADOR || 
                                   rol == Usuario.Rol.SECRETARIO || rol == Usuario.Rol.DOCENTE;
            case REGISTRAR_ASISTENCIA -> rol == Usuario.Rol.DOCENTE;
            case TRAMITES -> rol == Usuario.Rol.ADMINISTRADOR || rol == Usuario.Rol.DIRECTOR || 
                             rol == Usuario.Rol.SUBDIRECTOR || rol == Usuario.Rol.COORDINADOR || 
                             rol == Usuario.Rol.SECRETARIO;
            case MATRICULA -> rol == Usuario.Rol.ADMINISTRADOR || rol == Usuario.Rol.COORDINADOR || 
                              rol == Usuario.Rol.SECRETARIO;
            case GESTION_USUARIOS -> rol == Usuario.Rol.ADMINISTRADOR || rol == Usuario.Rol.DIRECTOR || 
                                     rol == Usuario.Rol.SUBDIRECTOR;
            case GESTION_ACADEMICA -> rol == Usuario.Rol.ADMINISTRADOR || rol == Usuario.Rol.DIRECTOR || 
                                      rol == Usuario.Rol.SUBDIRECTOR || rol == Usuario.Rol.COORDINADOR;
            case REPORTES -> rol == Usuario.Rol.ADMINISTRADOR || rol == Usuario.Rol.DIRECTOR || 
                             rol == Usuario.Rol.SUBDIRECTOR || rol == Usuario.Rol.COORDINADOR || 
                             rol == Usuario.Rol.TESORERO;
            case CONFIGURACION_SISTEMA -> rol == Usuario.Rol.ADMINISTRADOR;
            case CONFIGURACION_PERFIL -> rol != Usuario.Rol.ADMINISTRADOR;
        };
    }
    public boolean puedeAccederPortal(Usuario.Rol rol, String portal) {
        if (rol == null || portal == null) return false;
        return switch (portal) {
            case "INGRESO" -> rol != Usuario.Rol.PADRE;
            case "TRAMITES" -> rol == Usuario.Rol.DOCENTE || rol == Usuario.Rol.ESTUDIANTE;
            case "PAGOS", "MATRICULA" -> rol == Usuario.Rol.ESTUDIANTE;
            default -> false;
        };
    }
}

/*
 * SPDX-FileCopyrightText: 2026 inici4rsesi0n
 * SPDX-License-Identifier: GPL-3.0-or-later
 */
package dominio.puerto.repositorio;
import dominio.modelo.Asistencia;
import java.time.LocalDate;
import java.util.List;
public interface RepositorioAsistencias extends Repositorio<Asistencia> {
    List<Asistencia> buscarPorGrupoAsignaturaYFecha(String grupoNombre, String asignaturaNombre, LocalDate fecha);
}

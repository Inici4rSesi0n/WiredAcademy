/*
 * SPDX-FileCopyrightText: 2026 inici4rsesi0n
 * SPDX-License-Identifier: GPL-3.0-or-later
 */
package dominio.puerto.repositorio;
import dominio.modelo.Calificacion;
import java.util.List;
public interface RepositorioCalificaciones extends Repositorio<Calificacion> {
    List<Calificacion> buscarPorEstudiante(String codigoEstudiante);
    List<Calificacion> buscarPorEvaluacion(Long idEvaluacion);
}

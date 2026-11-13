/*
 * SPDX-FileCopyrightText: 2026 inici4rsesi0n
 * SPDX-License-Identifier: GPL-3.0-or-later
 */
package dominio.puerto.repositorio;
import dominio.modelo.Evaluacion;
import java.util.List;
public interface RepositorioEvaluaciones extends Repositorio<Evaluacion> {
    List<Evaluacion> buscarPorAsignatura(String nombreAsignatura);
}

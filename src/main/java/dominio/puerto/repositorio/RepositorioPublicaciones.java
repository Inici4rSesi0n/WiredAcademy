/*
 * SPDX-FileCopyrightText: 2026 inici4rsesi0n
 * SPDX-License-Identifier: GPL-3.0-or-later
 */
package dominio.puerto.repositorio;
import dominio.modelo.Publicacion;
import java.util.List;
public interface RepositorioPublicaciones extends Repositorio<Publicacion> {
    List<Publicacion> buscarPorAsignatura(String nombreAsignatura);
    List<Publicacion> buscarGlobales();
}

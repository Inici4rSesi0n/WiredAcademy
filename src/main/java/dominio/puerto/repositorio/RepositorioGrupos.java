/*
 * SPDX-FileCopyrightText: 2026 inici4rsesi0n
 * SPDX-License-Identifier: GPL-3.0-or-later
 */
package dominio.puerto.repositorio;
import dominio.modelo.Grupo;
import java.util.List;
import java.util.Optional;
/**
 *
 * @author inici4rsesi0n
 */
public interface RepositorioGrupos extends Repositorio<Grupo> {
    Optional<Grupo> buscarPorNombre(String nombre);
    List<Grupo> buscarPorNombreYGrado(String nombreGrupo, String nombreGrado);
}
/*
 * SPDX-FileCopyrightText: 2026 inici4rsesi0n
 * SPDX-License-Identifier: GPL-3.0-or-later
 */
package dominio.puerto.repositorio;
import dominio.modelo.Aula;
import java.util.Optional;
/**
 *
 * @author inici4rsesi0n
 */
public interface RepositorioAulas extends Repositorio<Aula> {
    Optional<Aula> buscarPorNombre(String nombre);
}
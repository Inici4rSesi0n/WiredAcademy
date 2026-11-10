/*
 * SPDX-FileCopyrightText: 2026 inici4rsesi0n
 * SPDX-License-Identifier: GPL-3.0-or-later
 */
package dominio.puerto.repositorio;
import dominio.modelo.CoordinadorAcademico;
import java.util.Optional;
/**
 *
 * @author inici4rsesi0n
 */
public interface RepositorioCoordinadores extends Repositorio<CoordinadorAcademico> {
    Optional<CoordinadorAcademico> buscarPorCodigo(String codigo);
}
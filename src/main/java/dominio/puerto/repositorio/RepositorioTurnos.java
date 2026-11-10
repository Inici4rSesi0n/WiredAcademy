/*
 * SPDX-FileCopyrightText: 2026 inici4rsesi0n
 * SPDX-License-Identifier: GPL-3.0-or-later
 */
package dominio.puerto.repositorio;
import dominio.modelo.Turno;
import java.util.Optional;
/**
 *
 * @author inici4rsesi0n
 */
public interface RepositorioTurnos extends Repositorio<Turno> {
    Optional<Turno> buscarPorNombre(String nombre);
}
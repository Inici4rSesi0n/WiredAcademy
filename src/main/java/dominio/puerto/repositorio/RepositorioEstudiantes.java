/*
 * SPDX-FileCopyrightText: 2026 inici4rsesi0n
 * SPDX-License-Identifier: GPL-3.0-or-later
 */
package dominio.puerto.repositorio;
import dominio.modelo.Estudiante;
import java.util.Optional;
/**
 *
 * @author inici4rsesi0n
 */
public interface RepositorioEstudiantes extends Repositorio<Estudiante> {
    Optional<Estudiante> buscarPorDni(String dni);
    Optional<Estudiante> buscarPorCodigo(String codigo);
}
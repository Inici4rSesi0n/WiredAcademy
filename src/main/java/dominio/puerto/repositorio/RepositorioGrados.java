/*
 * SPDX-FileCopyrightText: 2026 inici4rsesi0n
 * SPDX-License-Identifier: GPL-3.0-or-later
 */
package dominio.puerto.repositorio;
import dominio.modelo.Grado;
import java.util.Optional;
/**
 *
 * @author inici4rsesi0n
 */
public interface RepositorioGrados extends Repositorio<Grado> {
    Optional<Grado> buscarPorNombre(String nombre);
    Optional<Grado> buscarPorNombreYNivel(String nombre, String nivel);
}

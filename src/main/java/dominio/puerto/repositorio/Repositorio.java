/*
 * SPDX-FileCopyrightText: 2026 inici4rsesi0n
 * SPDX-License-Identifier: GPL-3.0-or-later
 */
package dominio.puerto.repositorio;
import java.util.List;
/**
 *
 * @author inici4rsesi0n
 */
public interface Repositorio<T> {
    void agregar(T entidad);
    List<T> listarTodos();
    void actualizar(T original, T actualizada);
    void eliminar(T entidad);
}
/*
 * SPDX-FileCopyrightText: 2026 inici4rsesi0n
 * SPDX-License-Identifier: GPL-3.0-or-later
 */
package aplicacion.servicio;
import dominio.modelo.Usuario;
import dominio.puerto.repositorio.RepositorioAdministradores;
import dominio.puerto.repositorio.RepositorioCoordinadores;
import dominio.puerto.repositorio.RepositorioDirectores;
import dominio.puerto.repositorio.RepositorioDocentes;
import dominio.puerto.repositorio.RepositorioEstudiantes;
import dominio.puerto.repositorio.RepositorioPadres;
import dominio.puerto.repositorio.RepositorioSecretarios;
import org.springframework.stereotype.Service;
import java.util.List;
/**
 * Servicio colaborador que centraliza la búsqueda de usuarios por código
 * a través de todos los repositorios específicos por rol.
 *
 * Elimina la duplicación de la cadena de búsqueda entre AutenticarUsuario
 * y GestionUsuarios, y cumple OCP: añadir un rol nuevo solo requiere
 * un nuevo resolutor en la lista.
 *
 * @author inici4rsesi0n
 */
@Service
public class BuscadorUsuario {
    @FunctionalInterface
    private interface Resolutor {
        Usuario buscar(String codigo);
    }
    private final List<Resolutor> resolutores;
    public BuscadorUsuario(RepositorioAdministradores repoAdmin,
                           RepositorioDirectores repoDir,
                           RepositorioDocentes repoDoc,
                           RepositorioEstudiantes repoEst,
                           RepositorioSecretarios repoSec,
                           RepositorioCoordinadores repoCoord,
                           RepositorioPadres repoPad) {
        this.resolutores = List.of(
                cod -> repoAdmin.buscarPorCodigo(cod).orElse(null),
                cod -> repoDir.buscarPorCodigo(cod).orElse(null),
                cod -> repoDoc.buscarPorCodigo(cod).orElse(null),
                cod -> repoEst.buscarPorCodigo(cod).orElse(null),
                cod -> repoSec.buscarPorCodigo(cod).orElse(null),
                cod -> repoCoord.buscarPorCodigo(cod).orElse(null),
                cod -> repoPad.buscarPorCodigo(cod).orElse(null)
        );
    }
    /**
     * Busca un usuario por código en todos los repositorios, en orden.
     * Retorna null si no se encuentra o si el código es nulo/vacío.
     */
    public Usuario buscarPorCodigo(String codigo) {
        if (codigo == null || codigo.isBlank()) return null;
        for (Resolutor r : resolutores) {
            Usuario u = r.buscar(codigo);
            if (u != null) return u;
        }
        return null;
    }
}

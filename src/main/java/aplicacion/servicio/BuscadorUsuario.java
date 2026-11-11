/*
 * SPDX-FileCopyrightText: 2026 inici4rsesi0n
 * SPDX-License-Identifier: GPL-3.0-or-later
 */
package aplicacion.servicio;
import dominio.modelo.Usuario;
import dominio.puerto.repositorio.*;
import org.springframework.stereotype.Service;
import java.util.List;
/**
 * @author inici4rsesi0n
 */
@Service
public class BuscadorUsuario {
    @FunctionalInterface
    private interface Resolutor {
        Usuario buscar(String codigo);
    }
    private final List<Resolutor> resolutores;
    public BuscadorUsuario(RepositorioAdministradores repoAdmin, RepositorioDirectores repoDir,
                           RepositorioDocentes repoDoc, RepositorioEstudiantes repoEst,
                           RepositorioSecretarios repoSec, RepositorioCoordinadores repoCoord,
                           RepositorioPadres repoPad, RepositorioSubdirectores repoSub,
                           RepositorioTesoreros repoTes, RepositorioPsicologos repoPsi) {
        this.resolutores = List.of(
                cod -> repoAdmin.buscarPorCodigo(cod).orElse(null),
                cod -> repoDir.buscarPorCodigo(cod).orElse(null),
                cod -> repoDoc.buscarPorCodigo(cod).orElse(null),
                cod -> repoEst.buscarPorCodigo(cod).orElse(null),
                cod -> repoSec.buscarPorCodigo(cod).orElse(null),
                cod -> repoCoord.buscarPorCodigo(cod).orElse(null),
                cod -> repoPad.buscarPorCodigo(cod).orElse(null),
                cod -> repoSub.buscarPorCodigo(cod).orElse(null),
                cod -> repoTes.buscarPorCodigo(cod).orElse(null),
                cod -> repoPsi.buscarPorCodigo(cod).orElse(null)
        );
    }
    public Usuario buscarPorCodigo(String codigo) {
        if (codigo == null || codigo.isBlank()) return null;
        for (Resolutor r : resolutores) {
            Usuario u = r.buscar(codigo);
            if (u != null) return u;
        }
        return null;
    }
}

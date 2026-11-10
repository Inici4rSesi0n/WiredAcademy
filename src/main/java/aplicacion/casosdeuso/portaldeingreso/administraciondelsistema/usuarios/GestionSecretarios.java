/*
 * SPDX-FileCopyrightText: 2026 inici4rsesi0n
 * SPDX-License-Identifier: GPL-3.0-or-later
 */
package aplicacion.casosdeuso.portaldeingreso.administraciondelsistema.usuarios;
import dominio.modelo.Secretario;
import dominio.puerto.externo.LoggerPort;
import dominio.puerto.repositorio.RepositorioSecretarios;
import org.springframework.stereotype.Service;
import java.util.List;
/**
 *
 * @author inici4rsesi0n
 */
@Service
public class GestionSecretarios {
    private final RepositorioSecretarios repo;
    private final LoggerPort logger;
    public GestionSecretarios(RepositorioSecretarios repo, LoggerPort logger) {
        this.repo = repo;
        this.logger = logger;
    }
    public void agregar(Secretario secretario) {
        if (secretario == null) throw new IllegalArgumentException("El secretario no puede ser nulo");
        if (repo.buscarPorCodigo(secretario.getCodigo()).isPresent()) {
            throw new IllegalArgumentException("Ya existe un secretario con ese código");
        }
        repo.agregar(secretario);
        logger.audit(String.format(
                "op=CREATE entity=SECRETARIO id=%s | nombre=%s %s",
                secretario.getCodigo(), secretario.getNombre(), secretario.getApellido()));
    }
    public Secretario buscarPorCodigo(String codigo) {
        return repo.buscarPorCodigo(codigo).orElse(null);
    }
    public List<Secretario> listarTodos() {
        return repo.listarTodos();
    }
    public void actualizar(Secretario original, Secretario actualizado) {
        if (original == null || actualizado == null) {
            throw new IllegalArgumentException("Los secretarios no pueden ser nulos");
        }
        String antes = original.toString();
        repo.actualizar(original, actualizado);
        logger.audit(String.format(
                "op=UPDATE entity=SECRETARIO id=%s | before=%s after=%s",
                original.getCodigo(), antes, actualizado));
    }
    public void eliminar(Secretario secretario) {
        if (secretario == null) throw new IllegalArgumentException("El secretario no puede ser nulo");
        String snapshot = secretario.toString();
        repo.eliminar(secretario);
        logger.audit(String.format(
                "op=DELETE entity=SECRETARIO id=%s | snapshot=%s",
                secretario.getCodigo(), snapshot));
    }
}

/*
 * SPDX-FileCopyrightText: 2026 inici4rsesi0n
 * SPDX-License-Identifier: GPL-3.0-or-later
 */
package aplicacion.casosdeuso.portaldeingreso.administraciondelsistema.usuarios;
import dominio.modelo.Director;
import dominio.puerto.externo.LoggerPort;
import dominio.puerto.repositorio.RepositorioDirectores;
import org.springframework.stereotype.Service;
import java.util.List;
/**
 *
 * @author inici4rsesi0n
 */
@Service
public class GestionDirectores {
    private final RepositorioDirectores repo;
    private final LoggerPort logger;
    public GestionDirectores(RepositorioDirectores repo, LoggerPort logger) {
        this.repo = repo;
        this.logger = logger;
    }
    public void agregar(Director director) {
        if (director == null) throw new IllegalArgumentException("El director no puede ser nulo");
        if (repo.buscarPorCodigo(director.getCodigo()).isPresent()) {
            throw new IllegalArgumentException("Ya existe un director con ese código");
        }
        repo.agregar(director);
        logger.audit(String.format(
                "op=CREATE entity=DIRECTOR id=%s | nombre=%s %s",
                director.getCodigo(), director.getNombre(), director.getApellido()));
    }
    public Director buscarPorCodigo(String codigo) {
        return repo.buscarPorCodigo(codigo).orElse(null);
    }
    public List<Director> listarTodos() {
        return repo.listarTodos();
    }
    public void actualizar(Director original, Director actualizado) {
        if (original == null || actualizado == null) {
            throw new IllegalArgumentException("Los directores no pueden ser nulos");
        }
        String antes = original.toString();
        repo.actualizar(original, actualizado);
        logger.audit(String.format(
                "op=UPDATE entity=DIRECTOR id=%s | before=%s after=%s",
                original.getCodigo(), antes, actualizado));
    }
    public void eliminar(Director director) {
        if (director == null) throw new IllegalArgumentException("El director no puede ser nulo");
        String snapshot = director.toString();
        repo.eliminar(director);
        logger.audit(String.format(
                "op=DELETE entity=DIRECTOR id=%s | snapshot=%s",
                director.getCodigo(), snapshot));
    }
}

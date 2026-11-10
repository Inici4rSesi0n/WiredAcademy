/*
 * SPDX-FileCopyrightText: 2026 inici4rsesi0n
 * SPDX-License-Identifier: GPL-3.0-or-later
 */
package aplicacion.casosdeuso.portaldeingreso.administraciondelsistema.usuarios;

import dominio.modelo.Administrador;
import dominio.puerto.externo.LoggerPort;
import dominio.puerto.repositorio.RepositorioAdministradores;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 *
 * @author inici4rsesi0n
 */
@Service
public class GestionAdministradores {

    private final RepositorioAdministradores repo;
    private final LoggerPort logger;

    public GestionAdministradores(RepositorioAdministradores repo, LoggerPort logger) {
        this.repo = repo;
        this.logger = logger;
    }

    public void agregar(Administrador administrador) {
        if (administrador == null) throw new IllegalArgumentException("El administrador no puede ser nulo");
        if (repo.buscarPorCodigo(administrador.getCodigo()).isPresent()) {
            throw new IllegalArgumentException("Ya existe un administrador con ese código");
        }
        repo.agregar(administrador);
        logger.audit(String.format(
                "op=CREATE entity=ADMINISTRADOR id=%s | nombre=%s %s",
                administrador.getCodigo(), administrador.getNombre(), administrador.getApellido()));
    }

    public Administrador buscarPorCodigo(String codigo) {
        return repo.buscarPorCodigo(codigo).orElse(null);
    }

    public List<Administrador> listarTodos() {
        return repo.listarTodos();
    }

    public void actualizar(Administrador original, Administrador actualizado) {
        if (original == null || actualizado == null) {
            throw new IllegalArgumentException("Los administradores no pueden ser nulos");
        }
        String antes = original.toString();
        repo.actualizar(original, actualizado);
        logger.audit(String.format(
                "op=UPDATE entity=ADMINISTRADOR id=%s | before=%s after=%s",
                original.getCodigo(), antes, actualizado));
    }

    public void eliminar(Administrador administrador) {
        if (administrador == null) throw new IllegalArgumentException("El administrador no puede ser nulo");
        String snapshot = administrador.toString();
        repo.eliminar(administrador);
        logger.audit(String.format(
                "op=DELETE entity=ADMINISTRADOR id=%s | snapshot=%s",
                administrador.getCodigo(), snapshot));
    }
}

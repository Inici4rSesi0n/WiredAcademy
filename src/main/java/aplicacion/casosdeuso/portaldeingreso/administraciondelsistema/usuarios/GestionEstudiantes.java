/*
 * SPDX-FileCopyrightText: 2026 inici4rsesi0n
 * SPDX-License-Identifier: GPL-3.0-or-later
 */
package aplicacion.casosdeuso.portaldeingreso.administraciondelsistema.usuarios;

import dominio.modelo.Estudiante;
import dominio.puerto.externo.LoggerPort;
import dominio.puerto.repositorio.RepositorioEstudiantes;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 *
 * @author inici4rsesi0n
 */
@Service
public class GestionEstudiantes {

    private final RepositorioEstudiantes repo;
    private final LoggerPort logger;

    public GestionEstudiantes(RepositorioEstudiantes repo, LoggerPort logger) {
        this.repo = repo;
        this.logger = logger;
    }

    public void agregar(Estudiante estudiante) {
        if (estudiante == null) throw new IllegalArgumentException("El estudiante no puede ser nulo");
        if (repo.buscarPorCodigo(estudiante.getCodigo()).isPresent()) {
            throw new IllegalArgumentException("Ya existe un estudiante con ese código");
        }
        if (repo.buscarPorDni(estudiante.getDni()).isPresent()) {
            throw new IllegalArgumentException("Ya existe un estudiante con ese DNI");
        }
        repo.agregar(estudiante);
        logger.audit(String.format(
                "op=CREATE entity=ESTUDIANTE id=%s | nombre=%s %s dni=%s",
                estudiante.getCodigo(), estudiante.getNombre(),
                estudiante.getApellido(), estudiante.getDni()));
    }

    public Estudiante buscarPorCodigo(String codigo) {
        return repo.buscarPorCodigo(codigo).orElse(null);
    }

    public Estudiante buscarPorDni(String dni) {
        return repo.buscarPorDni(dni).orElse(null);
    }

    public List<Estudiante> listarTodos() {
        return repo.listarTodos();
    }

    public void actualizar(Estudiante original, Estudiante actualizado) {
        if (original == null || actualizado == null) {
            throw new IllegalArgumentException("Los estudiantes no pueden ser nulos");
        }
        String antes = original.toString();
        repo.actualizar(original, actualizado);
        logger.audit(String.format(
                "op=UPDATE entity=ESTUDIANTE id=%s | before=%s after=%s",
                original.getCodigo(), antes, actualizado));
    }

    public void eliminar(Estudiante estudiante) {
        if (estudiante == null) throw new IllegalArgumentException("El estudiante no puede ser nulo");
        String snapshot = estudiante.toString();
        repo.eliminar(estudiante);
        logger.audit(String.format(
                "op=DELETE entity=ESTUDIANTE id=%s | snapshot=%s",
                estudiante.getCodigo(), snapshot));
    }
}

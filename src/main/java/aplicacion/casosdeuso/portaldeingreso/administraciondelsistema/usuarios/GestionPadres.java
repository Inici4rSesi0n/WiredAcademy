/*
 * SPDX-FileCopyrightText: 2026 inici4rsesi0n
 * SPDX-License-Identifier: GPL-3.0-or-later
 */
package aplicacion.casosdeuso.portaldeingreso.administraciondelsistema.usuarios;

import dominio.modelo.Estudiante;
import dominio.modelo.Padre;
import dominio.puerto.externo.LoggerPort;
import dominio.puerto.repositorio.RepositorioPadres;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 *
 * @author inici4rsesi0n
 */
@Service
public class GestionPadres {

    private final RepositorioPadres repo;
    private final LoggerPort logger;

    public GestionPadres(RepositorioPadres repo, LoggerPort logger) {
        this.repo = repo;
        this.logger = logger;
    }

    public void agregar(Padre padre) {
        if (padre == null) throw new IllegalArgumentException("El padre no puede ser nulo");
        if (repo.buscarPorCodigo(padre.getCodigo()).isPresent())
            throw new IllegalArgumentException("Ya existe un padre con ese código");
        repo.agregar(padre);
        logger.audit(String.format(
                "op=CREATE entity=PADRE id=%s | nombre=%s %s",
                padre.getCodigo(), padre.getNombre(), padre.getApellido()));
    }

    public Padre buscarPorCodigo(String codigo) { return repo.buscarPorCodigo(codigo).orElse(null); }
    public List<Padre> listarTodos() { return repo.listarTodos(); }

    public void actualizar(Padre original, Padre actualizado) {
        if (original == null || actualizado == null) {
            throw new IllegalArgumentException("Los padres no pueden ser nulos");
        }
        String antes = original.toString();
        repo.actualizar(original, actualizado);
        logger.audit(String.format(
                "op=UPDATE entity=PADRE id=%s | before=%s after=%s",
                original.getCodigo(), antes, actualizado));
    }

    public void eliminar(Padre padre) {
        if (padre == null) throw new IllegalArgumentException("El padre no puede ser nulo");
        String snapshot = padre.toString();
        repo.eliminar(padre);
        logger.audit(String.format(
                "op=DELETE entity=PADRE id=%s | snapshot=%s",
                padre.getCodigo(), snapshot));
    }

    public void vincularEstudiante(Padre padre, Estudiante estudiante) {
        if (estudiante == null) throw new IllegalArgumentException("El estudiante no puede ser nulo");
        padre.agregarHijo(estudiante);
        repo.actualizar(padre, padre);
        logger.audit(String.format(
                "op=LINK entity=PADRE id=%s -> ESTUDIANTE id=%s",
                padre.getCodigo(), estudiante.getCodigo()));
    }

    public void desvincularEstudiante(Padre padre, Estudiante estudiante) {
        if (estudiante == null) throw new IllegalArgumentException("El estudiante no puede ser nulo");
        padre.removerHijo(estudiante);
        repo.actualizar(padre, padre);
        logger.audit(String.format(
                "op=UNLINK entity=PADRE id=%s -> ESTUDIANTE id=%s",
                padre.getCodigo(), estudiante.getCodigo()));
    }
}

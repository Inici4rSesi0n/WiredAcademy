/*
 * SPDX-FileCopyrightText: 2026 inici4rsesi0n
 * SPDX-License-Identifier: GPL-3.0-or-later
 */
package aplicacion.casosdeuso.portaldeingreso.administraciondelsistema.usuarios;
import dominio.modelo.CoordinadorAcademico;
import dominio.modelo.Grado;
import dominio.modelo.TipoEvento;
import dominio.puerto.externo.EventPublisher;
import dominio.puerto.externo.LoggerPort;
import dominio.puerto.repositorio.RepositorioCoordinadores;
import org.springframework.stereotype.Service;
import java.util.List;
/**
 *
 * @author inici4rsesi0n
 */
@Service
public class GestionCoordinadores {
    private final RepositorioCoordinadores repo;
    private final LoggerPort logger;
    private final EventPublisher publisher;
    public GestionCoordinadores(RepositorioCoordinadores repo, LoggerPort logger, EventPublisher publisher) {
        this.repo = repo;
        this.logger = logger;
        this.publisher = publisher;
    }
    public void agregar(CoordinadorAcademico coordinador) {
        if (coordinador == null) throw new IllegalArgumentException("El coordinador no puede ser nulo");
        if (repo.buscarPorCodigo(coordinador.getCodigo()).isPresent())
            throw new IllegalArgumentException("Ya existe un coordinador con ese código");
        repo.agregar(coordinador);
        logger.audit(String.format("op=CREATE entity=COORDINADOR id=%s | nombre=%s %s",
                coordinador.getCodigo(), coordinador.getNombre(), coordinador.getApellido()));
    }
    public CoordinadorAcademico buscarPorCodigo(String codigo) {
        return repo.buscarPorCodigo(codigo).orElse(null);
    }
    public List<CoordinadorAcademico> listarTodos() { return repo.listarTodos(); }
    public void actualizar(CoordinadorAcademico original, CoordinadorAcademico actualizado) {
        if (original == null || actualizado == null)
            throw new IllegalArgumentException("Los coordinadores no pueden ser nulos");
        String antes = original.toString();
        repo.actualizar(original, actualizado);
        logger.audit(String.format("op=UPDATE entity=COORDINADOR id=%s | before=%s after=%s",
                original.getCodigo(), antes, actualizado));
    }
    public void eliminar(CoordinadorAcademico coordinador) {
        if (coordinador == null) throw new IllegalArgumentException("El coordinador no puede ser nulo");
        String snapshot = coordinador.toString();
        repo.eliminar(coordinador);
        logger.audit(String.format("op=DELETE entity=COORDINADOR id=%s | snapshot=%s",
                coordinador.getCodigo(), snapshot));
    }
    public void agregarGrado(String codigoCoordinador, Grado grado) {
        if (codigoCoordinador == null || codigoCoordinador.isBlank())
            throw new IllegalArgumentException("El código del coordinador es obligatorio");
        if (grado == null) throw new IllegalArgumentException("El grado no puede ser nulo");
        CoordinadorAcademico coord = repo.buscarPorCodigo(codigoCoordinador)
                .orElseThrow(() -> new IllegalArgumentException("El coordinador no existe"));
        coord.agregarGrado(grado);
        repo.actualizar(coord, coord);
        logger.audit(String.format("op=LINK entity=COORDINADOR id=%s -> GRADO id=%s",
                codigoCoordinador, grado.getNombre()));
        publisher.notificar(TipoEvento.COORDINADORES);
        publisher.notificar(TipoEvento.GRADOS);
    }
    public void removerGrado(String codigoCoordinador, Grado grado) {
        if (codigoCoordinador == null || codigoCoordinador.isBlank())
            throw new IllegalArgumentException("El código del coordinador es obligatorio");
        if (grado == null) throw new IllegalArgumentException("El grado no puede ser nulo");
        CoordinadorAcademico coord = repo.buscarPorCodigo(codigoCoordinador)
                .orElseThrow(() -> new IllegalArgumentException("El coordinador no existe"));
        coord.removerGrado(grado);
        repo.actualizar(coord, coord);
        logger.audit(String.format("op=UNLINK entity=COORDINADOR id=%s -> GRADO id=%s",
                codigoCoordinador, grado.getNombre()));
        publisher.notificar(TipoEvento.COORDINADORES);
        publisher.notificar(TipoEvento.GRADOS);
    }
}

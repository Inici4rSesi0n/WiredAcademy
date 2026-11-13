/*
 * SPDX-FileCopyrightText: 2026 inici4rsesi0n
 * SPDX-License-Identifier: GPL-3.0-or-later
 */
package aplicacion.casosdeuso.portaldeingreso.publicaciones;
import dominio.modelo.*;
import dominio.puerto.externo.EventPublisher;
import dominio.puerto.externo.LoggerPort;
import dominio.puerto.repositorio.RepositorioPublicaciones;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.List;
@Service
public class GestionPublicaciones {
    private final RepositorioPublicaciones repo;
    private final LoggerPort logger;
    private final EventPublisher publisher;
    public GestionPublicaciones(RepositorioPublicaciones repo, LoggerPort logger, EventPublisher publisher) {
        this.repo = repo;
        this.logger = logger;
        this.publisher = publisher;
    }
    public void crearPublicacion(String titulo, String descripcion, TipoPublicacion tipo,
                                 Integer numeroSemana, LocalDateTime fechaLimite, String urlArchivo,
                                 Asignatura asignatura, Usuario autor) {
        if (titulo == null || titulo.isBlank()) throw new IllegalArgumentException("El título no puede estar vacío.");
        if (tipo == null || autor == null) throw new IllegalArgumentException("Tipo y autor son obligatorios.");
        if ((tipo == TipoPublicacion.MATERIAL || tipo == TipoPublicacion.TAREA || tipo == TipoPublicacion.EVALUACION)
                && asignatura == null) {
            throw new IllegalArgumentException("Este tipo de publicación requiere una asignatura.");
        }
        Publicacion pub = new Publicacion(null, titulo, descripcion, tipo, numeroSemana, fechaLimite, urlArchivo, asignatura, autor);
        repo.agregar(pub);
        logger.audit(String.format("op=PUBLISH entity=%s id=%s | tipo=%s", tipo, titulo, tipo));
        publisher.notificar(TipoEvento.PUBLICACIONES);
    }
    public List<Publicacion> listarTodos() {
        return repo.listarTodos();
    }
    public List<Publicacion> listarPorAsignatura(String nombreAsignatura) {
        return repo.buscarPorAsignatura(nombreAsignatura);
    }
    public List<Publicacion> listarGlobales() {
        return repo.buscarGlobales();
    }
    public void eliminarPublicacion(Publicacion pub) {
        if (pub == null) throw new IllegalArgumentException("La publicación no puede ser nula.");
        repo.eliminar(pub);
        logger.audit(String.format("op=DELETE entity=PUBLICACION id=%s", pub.getTitulo()));
        publisher.notificar(TipoEvento.PUBLICACIONES);
    }
}

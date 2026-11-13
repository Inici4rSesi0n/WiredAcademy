/*
 * SPDX-FileCopyrightText: 2026 inici4rsesi0n
 * SPDX-License-Identifier: GPL-3.0-or-later
 */
package infraestructura.persistencia.adaptadores;
import aplicacion.servicio.BuscadorUsuario;
import dominio.modelo.Publicacion;
import dominio.puerto.repositorio.RepositorioPublicaciones;
import infraestructura.persistencia.entidades.PublicacionEntity;
import infraestructura.persistencia.jpa.AsignaturaJpaRepository;
import infraestructura.persistencia.jpa.PublicacionJpaRepository;
import infraestructura.persistencia.jpa.UsuarioJpaRepository;
import infraestructura.persistencia.mappers.PublicacionMapper;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.List;
@Repository
public class AdaptadorRepositorioPublicaciones implements RepositorioPublicaciones {
    private final PublicacionJpaRepository jpaRepository;
    private final AsignaturaJpaRepository asignaturaJpa;
    private final UsuarioJpaRepository usuarioJpa;
    private final PublicacionMapper mapper;
    public AdaptadorRepositorioPublicaciones(PublicacionJpaRepository jpaRepository, AsignaturaJpaRepository asignaturaJpa,
                                             UsuarioJpaRepository usuarioJpa, PublicacionMapper mapper) {
        this.jpaRepository = jpaRepository;
        this.asignaturaJpa = asignaturaJpa;
        this.usuarioJpa = usuarioJpa;
        this.mapper = mapper;
    }
    @Override @Transactional
    public void agregar(Publicacion pub) {
        PublicacionEntity entity = mapper.toEntity(pub);
        if (pub.getAsignatura() != null) {
            entity.setAsignatura(asignaturaJpa.findByNombre(pub.getAsignatura().getNombre()).orElseThrow());
        }
        entity.setAutor(usuarioJpa.findByCodigo(pub.getAutor().getCodigo()).orElseThrow());
        entity.setFechaCreacion(LocalDateTime.now());
        jpaRepository.save(entity);
    }
    @Override public List<Publicacion> listarTodos() { return mapper.toDomainList(jpaRepository.findAll()); }
    @Override @Transactional
    public void actualizar(Publicacion original, Publicacion actualizada) {
        PublicacionEntity entity = jpaRepository.findById(original.getId()).orElseThrow();
        entity.setTitulo(actualizada.getTitulo());
        entity.setDescripcion(actualizada.getDescripcion());
        entity.setFechaLimite(actualizada.getFechaLimite());
        entity.setUrlArchivo(actualizada.getUrlArchivo());
        jpaRepository.save(entity);
    }
    @Override @Transactional
    public void eliminar(Publicacion pub) { jpaRepository.deleteById(pub.getId()); }
    @Override public List<Publicacion> buscarPorAsignatura(String nombre) {
        return mapper.toDomainList(jpaRepository.findByAsignaturaNombreOrderByFechaCreacionDesc(nombre));
    }
    @Override public List<Publicacion> buscarGlobales() {
        return mapper.toDomainList(jpaRepository.findByAsignaturaIsNullOrderByFechaCreacionDesc());
    }
}

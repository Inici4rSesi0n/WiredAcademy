/*
 * SPDX-FileCopyrightText: 2026 inici4rsesi0n
 * SPDX-License-Identifier: GPL-3.0-or-later
 */
package infraestructura.persistencia.adaptadores;
import dominio.modelo.Asistencia;
import dominio.puerto.repositorio.RepositorioAsistencias;
import infraestructura.persistencia.entidades.AsistenciaEntity;
import infraestructura.persistencia.jpa.AsistenciaJpaRepository;
import infraestructura.persistencia.jpa.AsignaturaJpaRepository;
import infraestructura.persistencia.jpa.EstudianteJpaRepository;
import infraestructura.persistencia.jpa.GrupoJpaRepository;
import infraestructura.persistencia.mappers.AsistenciaMapper;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDate;
import java.util.List;
@Repository
public class AdaptadorRepositorioAsistencias implements RepositorioAsistencias {
    private final AsistenciaJpaRepository jpa;
    private final GrupoJpaRepository grupoJpa;
    private final AsignaturaJpaRepository asigJpa;
    private final EstudianteJpaRepository estJpa;
    private final AsistenciaMapper mapper;
    public AdaptadorRepositorioAsistencias(AsistenciaJpaRepository jpa, GrupoJpaRepository grupoJpa, AsignaturaJpaRepository asigJpa, EstudianteJpaRepository estJpa, AsistenciaMapper mapper) {
        this.jpa = jpa;
        this.grupoJpa = grupoJpa;
        this.asigJpa = asigJpa;
        this.estJpa = estJpa;
        this.mapper = mapper;
    }
    @Override @Transactional
    public void agregar(Asistencia a) {
        AsistenciaEntity entity = jpa.findByGrupoNombreAndAsignaturaNombreAndFecha(a.getGrupo().getNombre(), a.getAsignatura().getNombre(), a.getFecha())
            .stream().filter(e -> e.getEstudiante().getCodigo().equals(a.getEstudiante().getCodigo()))
            .findFirst().orElse(new AsistenciaEntity());
        entity.setGrupo(grupoJpa.findByNombre(a.getGrupo().getNombre()).orElseThrow());
        entity.setAsignatura(asigJpa.findByNombre(a.getAsignatura().getNombre()).orElseThrow());
        entity.setEstudiante(estJpa.findByCodigo(a.getEstudiante().getCodigo()).orElseThrow());
        entity.setFecha(a.getFecha());
        entity.setEstado(a.getEstado().name());
        jpa.save(entity);
    }
    @Override public List<Asistencia> listarTodos() { return mapper.toDomainList(jpa.findAll()); }
    @Override @Transactional public void actualizar(Asistencia o, Asistencia a) {}
    @Override @Transactional public void eliminar(Asistencia a) { jpa.deleteById(a.getId()); }
    @Override public List<Asistencia> buscarPorGrupoAsignaturaYFecha(String grupoNombre, String asignaturaNombre, LocalDate fecha) {
        return mapper.toDomainList(jpa.findByGrupoNombreAndAsignaturaNombreAndFecha(grupoNombre, asignaturaNombre, fecha));
    }
}

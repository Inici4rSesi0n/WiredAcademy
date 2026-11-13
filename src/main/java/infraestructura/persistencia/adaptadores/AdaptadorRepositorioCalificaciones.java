/*
 * SPDX-FileCopyrightText: 2026 inici4rsesi0n
 * SPDX-License-Identifier: GPL-3.0-or-later
 */
package infraestructura.persistencia.adaptadores;
import dominio.modelo.Calificacion;
import dominio.puerto.repositorio.RepositorioCalificaciones;
import infraestructura.persistencia.entidades.CalificacionEntity;
import infraestructura.persistencia.jpa.CalificacionJpaRepository;
import infraestructura.persistencia.jpa.EvaluacionJpaRepository;
import infraestructura.persistencia.jpa.EstudianteJpaRepository;
import infraestructura.persistencia.mappers.CalificacionMapper;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
@Repository
public class AdaptadorRepositorioCalificaciones implements RepositorioCalificaciones {
    private final CalificacionJpaRepository jpaRepository;
    private final EvaluacionJpaRepository evaluacionJpa;
    private final EstudianteJpaRepository estudianteJpa;
    private final CalificacionMapper mapper;
    public AdaptadorRepositorioCalificaciones(CalificacionJpaRepository jpaRepository, EvaluacionJpaRepository evaluacionJpa, EstudianteJpaRepository estudianteJpa, CalificacionMapper mapper) {
        this.jpaRepository=jpaRepository;
        this.evaluacionJpa=evaluacionJpa;
        this.estudianteJpa=estudianteJpa;
        this.mapper=mapper;
    }
    @Override @Transactional
    public void agregar(Calificacion calificacion) {
        CalificacionEntity entity = jpaRepository.findByEvaluacionIdAndEstudianteCodigo(
                calificacion.getEvaluacion().getId(), calificacion.getEstudiante().getCodigo())
                .orElse(new CalificacionEntity());
        entity.setEvaluacion(evaluacionJpa.findById(calificacion.getEvaluacion().getId()).orElseThrow());
        entity.setEstudiante(estudianteJpa.findByCodigo(calificacion.getEstudiante().getCodigo()).orElseThrow());
        entity.setNota(calificacion.getNota());
        jpaRepository.save(entity);
    }
    @Override public List<Calificacion> listarTodos() { return mapper.toDomainList(jpaRepository.findAll()); }
    @Override @Transactional
    public void actualizar(Calificacion original, Calificacion actualizada) {
        CalificacionEntity entity = jpaRepository.findById(original.getId()).orElseThrow();
        entity.setNota(actualizada.getNota());
        jpaRepository.save(entity);
    }
    @Override @Transactional
    public void eliminar(Calificacion calificacion) { jpaRepository.deleteById(calificacion.getId()); }
    @Override public List<Calificacion> buscarPorEstudiante(String codigoEstudiante) {
        return mapper.toDomainList(jpaRepository.findByEstudianteCodigo(codigoEstudiante));
    }
    @Override public List<Calificacion> buscarPorEvaluacion(Long idEvaluacion) {
        return mapper.toDomainList(jpaRepository.findByEvaluacionId(idEvaluacion));
    }
}

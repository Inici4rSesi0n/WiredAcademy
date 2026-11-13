/*
 * SPDX-FileCopyrightText: 2026 inici4rsesi0n
 * SPDX-License-Identifier: GPL-3.0-or-later
 */
package infraestructura.persistencia.adaptadores;
import dominio.modelo.Evaluacion;
import dominio.puerto.repositorio.RepositorioEvaluaciones;
import infraestructura.persistencia.entidades.EvaluacionEntity;
import infraestructura.persistencia.jpa.AsignaturaJpaRepository;
import infraestructura.persistencia.jpa.EvaluacionJpaRepository;
import infraestructura.persistencia.mappers.EvaluacionMapper;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
@Repository
public class AdaptadorRepositorioEvaluaciones implements RepositorioEvaluaciones {
    private final EvaluacionJpaRepository jpaRepository;
    private final AsignaturaJpaRepository asignaturaJpa;
    private final EvaluacionMapper mapper;
    public AdaptadorRepositorioEvaluaciones(EvaluacionJpaRepository jpaRepository, AsignaturaJpaRepository asignaturaJpa, EvaluacionMapper mapper) {
        this.jpaRepository=jpaRepository;
        this.asignaturaJpa=asignaturaJpa;
        this.mapper=mapper;
    }
    @Override @Transactional
    public void agregar(Evaluacion evaluacion) {
        EvaluacionEntity entity = mapper.toEntity(evaluacion);
        entity.setAsignatura(asignaturaJpa.findByNombre(evaluacion.getAsignatura().getNombre()).orElseThrow());
        jpaRepository.save(entity);
    }
    @Override public List<Evaluacion> listarTodos() { return mapper.toDomainList(jpaRepository.findAll()); }
    @Override @Transactional
    public void actualizar(Evaluacion original, Evaluacion actualizada) {
        EvaluacionEntity entity = jpaRepository.findById(original.getId()).orElseThrow();
        entity.setNombre(actualizada.getNombre());
        entity.setPeso(actualizada.getPeso());
        jpaRepository.save(entity);
    }
    @Override @Transactional
    public void eliminar(Evaluacion evaluacion) {
        jpaRepository.deleteById(evaluacion.getId());
    }
    @Override public List<Evaluacion> buscarPorAsignatura(String nombreAsignatura) {
        return mapper.toDomainList(jpaRepository.findByAsignaturaNombreOrderByIdAsc(nombreAsignatura));
    }
}

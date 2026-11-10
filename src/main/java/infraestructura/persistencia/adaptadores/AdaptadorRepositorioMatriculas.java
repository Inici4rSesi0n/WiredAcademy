/*
 * SPDX-FileCopyrightText: 2026 inici4rsesi0n
 * SPDX-License-Identifier: GPL-3.0-or-later
 */
package infraestructura.persistencia.adaptadores;
import dominio.modelo.Matricula;
import dominio.puerto.repositorio.RepositorioMatriculas;
import infraestructura.persistencia.entidades.AsignaturaEntity;
import infraestructura.persistencia.entidades.MatriculaEntity;
import infraestructura.persistencia.jpa.*;
import infraestructura.persistencia.mappers.MatriculaMapper;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.Optional;
/**
 *
 * @author inici4rsesi0n
 */
@Repository
public class AdaptadorRepositorioMatriculas implements RepositorioMatriculas{
    private final MatriculaJpaRepository jpaRepository;
    private final EstudianteJpaRepository estudianteJpa;
    private final PeriodoJpaRepository periodoJpa;
    private final GrupoJpaRepository grupoJpa;
    private final AsignaturaJpaRepository asignaturaJpa;
    private final MatriculaMapper mapper;
    public AdaptadorRepositorioMatriculas(MatriculaJpaRepository jpaRepository, EstudianteJpaRepository estudianteJpa,
            PeriodoJpaRepository periodoJpa, GrupoJpaRepository grupoJpa, AsignaturaJpaRepository asignaturaJpa,
            MatriculaMapper mapper){
        this.jpaRepository=jpaRepository;
        this.estudianteJpa=estudianteJpa;
        this.periodoJpa=periodoJpa;
        this.grupoJpa=grupoJpa;
        this.asignaturaJpa=asignaturaJpa;
        this.mapper=mapper;
    }
    @Override @Transactional
    public void agregar(Matricula matricula){
        if(matricula==null) throw new IllegalArgumentException("La matrícula no puede ser nula");
        MatriculaEntity entity = mapper.toEntity(matricula);
        resolverRelaciones(entity, matricula);
        jpaRepository.saveAndFlush(entity);
    }
    @Override
    public Optional<Matricula> buscarPorEstudianteYPeriodo(String codigoEstudiante, String nombrePeriodo){
        return jpaRepository.findByEstudianteCodigoAndPeriodoNombre(codigoEstudiante, nombrePeriodo).map(mapper::toDomain);
    }
    @Override
    public List<Matricula> buscarPorGrupo(String nombreGrupo){
        return mapper.toDomainList(jpaRepository.findByGrupoNombre(nombreGrupo));
    }
    @Override
    public List<Matricula> listarTodos(){
        return mapper.toDomainList(jpaRepository.findAll());
    }
    @Override @Transactional
    public void actualizar(Matricula original, Matricula actualizada){
        MatriculaEntity entity=jpaRepository.findByEstudianteCodigoAndPeriodoNombre(
                original.getEstudiante().getCodigo(), original.getPeriodo().getNombre())
                .orElseThrow(()->new IllegalArgumentException("La matrícula no existe"));
        entity.setFecha(actualizada.getFecha());
        entity.setEstado(actualizada.getEstado().name());
        jpaRepository.saveAndFlush(entity);
    }
    @Override @Transactional
    public void eliminar(Matricula matricula){
        jpaRepository.findByEstudianteCodigoAndPeriodoNombre(
                matricula.getEstudiante().getCodigo(), matricula.getPeriodo().getNombre())
                .ifPresent(jpaRepository::delete);
    }
    private void resolverRelaciones(MatriculaEntity entity, Matricula domain){
        entity.setEstudiante(estudianteJpa.findByCodigo(domain.getEstudiante().getCodigo())
                .orElseThrow(()->new IllegalArgumentException("Estudiante no existe")));
        entity.setPeriodo(periodoJpa.findByNombre(domain.getPeriodo().getNombre())
                .orElseThrow(()->new IllegalArgumentException("Periodo no existe")));
        entity.setGrupo(grupoJpa.findByNombre(domain.getGrupo().getNombre())
                .orElseThrow(()->new IllegalArgumentException("Grupo no existe")));
        if(domain.getAsignaturas()!=null){
            List<AsignaturaEntity> asignaturas = domain.getAsignaturas().stream()
                    .map(a -> asignaturaJpa.findByNombre(a.getNombre())
                            .orElseThrow(()->new IllegalArgumentException("Asignatura no existe: " + a.getNombre())))
                    .collect(java.util.stream.Collectors.toList());
            entity.setAsignaturas(asignaturas);
        }
    }
}

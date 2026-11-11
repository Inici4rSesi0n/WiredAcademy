/*
 * SPDX-FileCopyrightText: 2026 inici4rsesi0n
 * SPDX-License-Identifier: GPL-3.0-or-later
 */
package infraestructura.persistencia.adaptadores;
import dominio.modelo.Clase;
import dominio.puerto.repositorio.RepositorioClases;
import infraestructura.persistencia.entidades.ClaseEntity;
import infraestructura.persistencia.jpa.*;
import infraestructura.persistencia.mappers.ClaseMapper;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.Optional;
/**
 *
 * @author inici4rsesi0n
 */
@Repository
public class AdaptadorRepositorioClases implements RepositorioClases{
    private final ClaseJpaRepository jpaRepository;
    private final PeriodoJpaRepository periodoJpa;
    private final AsignaturaJpaRepository asignaturaJpa;
    private final DocenteJpaRepository docenteJpa;
    private final GrupoJpaRepository grupoJpa;
    private final AulaJpaRepository aulaJpa;
    private final ClaseMapper mapper;
    public AdaptadorRepositorioClases(ClaseJpaRepository jpaRepository, PeriodoJpaRepository periodoJpa,
            AsignaturaJpaRepository asignaturaJpa, DocenteJpaRepository docenteJpa,
            GrupoJpaRepository grupoJpa, AulaJpaRepository aulaJpa, ClaseMapper mapper){
        this.jpaRepository=jpaRepository;
        this.periodoJpa=periodoJpa;
        this.asignaturaJpa=asignaturaJpa;
        this.docenteJpa=docenteJpa;
        this.grupoJpa=grupoJpa;
        this.aulaJpa=aulaJpa;
        this.mapper=mapper;
    }
    @Override @Transactional
    public void agregar(Clase clase){
        if(clase==null) throw new IllegalArgumentException("La clase no puede ser nula");
        ClaseEntity entity = mapper.toEntity(clase);
        resolverRelaciones(entity, clase);
        jpaRepository.save(entity);
    }
    @Override
    public List<Clase> buscarPorGrupoYGrado(String nombreGrupo, String nombreGrado){
        return mapper.toDomainList(jpaRepository.findByGrupoNombreAndGrupoGradoNombreOrderByDiaSemanaAscHoraInicioAsc(nombreGrupo, nombreGrado));
    }
    @Override
    public List<Clase> buscarPorDocente(String codigoDocente, String nombrePeriodo){
        return mapper.toDomainList(jpaRepository.findByDocenteCodigoAndPeriodoNombreOrderByDiaSemanaAscHoraInicioAsc(codigoDocente, nombrePeriodo));
    }
    @Override
    public List<Clase> buscarPorAula(String nombreAula, String nombrePeriodo){
        return mapper.toDomainList(jpaRepository.findByAulaNombreAndPeriodoNombreOrderByDiaSemanaAscHoraInicioAsc(nombreAula, nombrePeriodo));
    }
    @Override
    public Optional<Clase> buscarClase(String nombreGrado, String nombreGrupo, String dia, String horaInicio, String horaFin){
        return jpaRepository.findByGrupoGradoNombreAndGrupoNombreAndDiaSemanaAndHoraInicioAndHoraFin(nombreGrado, nombreGrupo, dia, horaInicio, horaFin).map(mapper::toDomain);
    }
    @Override
    public boolean existeConflictoDocente(String codigoDocente, String dia, String horaInicio, String horaFin, Clase claseExcluida){
        Long excluirId = claseExcluida != null ? buscarIdPorClase(claseExcluida) : null;
        return jpaRepository.existeConflictoDocente(codigoDocente, dia, horaInicio, horaFin, excluirId);
    }
    @Override
    public boolean existeConflictoAula(String nombreAula, String dia, String horaInicio, String horaFin, Clase claseExcluida){
        Long excluirId = claseExcluida != null ? buscarIdPorClase(claseExcluida) : null;
        return jpaRepository.existeConflictoAula(nombreAula, dia, horaInicio, horaFin, excluirId);
    }
    @Override
    public boolean existeConflictoGrupo(String nombreGrupo, String nombreGrado, String dia, String horaInicio, String horaFin, Clase claseExcluida){
        Long excluirId = claseExcluida != null ? buscarIdPorClase(claseExcluida) : null;
        return jpaRepository.existeConflictoGrupo(nombreGrupo, nombreGrado, dia, horaInicio, horaFin, excluirId);
    }
    @Override
    public List<Clase> listarTodos(){
        return mapper.toDomainList(jpaRepository.findAll());
    }
    @Override @Transactional
    public void actualizar(Clase original, Clase actualizada){
        ClaseEntity entity=jpaRepository.findByGrupoGradoNombreAndGrupoNombreAndDiaSemanaAndHoraInicioAndHoraFin(
                original.getGrupo().getGrado().getNombre(), original.getGrupo().getNombre(),
                original.getDiaSemana(), original.getHoraInicio(), original.getHoraFin())
                .orElseThrow(()->new IllegalArgumentException("La clase no existe"));
        entity.setDiaSemana(actualizada.getDiaSemana());
        entity.setHoraInicio(actualizada.getHoraInicio());
        entity.setHoraFin(actualizada.getHoraFin());
        entity.setPeriodo(periodoJpa.findByNombre(actualizada.getPeriodo().getNombre())
                .orElseThrow(() -> new IllegalArgumentException("Periodo no existe")));
        entity.setAsignatura(asignaturaJpa.findByNombre(actualizada.getAsignatura().getNombre())
                .orElseThrow(() -> new IllegalArgumentException("Asignatura no existe")));
        entity.setDocente(docenteJpa.findByCodigo(actualizada.getDocente().getCodigo())
                .orElseThrow(() -> new IllegalArgumentException("Docente no existe")));
        entity.setGrupo(grupoJpa.findByNombre(actualizada.getGrupo().getNombre())
                .orElseThrow(() -> new IllegalArgumentException("Grupo no existe")));
        entity.setAula(aulaJpa.findByNombre(actualizada.getAula().getNombre())
                .orElseThrow(() -> new IllegalArgumentException("Aula no existe")));
        jpaRepository.save(entity);
    }
    @Override @Transactional
    public void eliminar(Clase clase){
        jpaRepository.findByGrupoGradoNombreAndGrupoNombreAndDiaSemanaAndHoraInicioAndHoraFin(
                clase.getGrupo().getGrado().getNombre(), clase.getGrupo().getNombre(),
                clase.getDiaSemana(), clase.getHoraInicio(), clase.getHoraFin())
                .ifPresent(jpaRepository::delete);
    }
    private Long buscarIdPorClase(Clase clase){
        return jpaRepository.findByGrupoGradoNombreAndGrupoNombreAndDiaSemanaAndHoraInicioAndHoraFin(
                clase.getGrupo().getGrado().getNombre(), clase.getGrupo().getNombre(),
                clase.getDiaSemana(), clase.getHoraInicio(), clase.getHoraFin())
                .map(ClaseEntity::getId).orElse(null);
    }
    private void resolverRelaciones(ClaseEntity entity, Clase domain){
        entity.setPeriodo(periodoJpa.findByNombre(domain.getPeriodo().getNombre())
                .orElseThrow(()->new IllegalArgumentException("Periodo no existe")));
        entity.setAsignatura(asignaturaJpa.findByNombre(domain.getAsignatura().getNombre())
                .orElseThrow(()->new IllegalArgumentException("Asignatura no existe")));
        entity.setDocente(docenteJpa.findByCodigo(domain.getDocente().getCodigo())
                .orElseThrow(()->new IllegalArgumentException("Docente no existe")));
        entity.setGrupo(grupoJpa.findByNombre(domain.getGrupo().getNombre())
                .orElseThrow(()->new IllegalArgumentException("Grupo no existe")));
        entity.setAula(aulaJpa.findByNombre(domain.getAula().getNombre())
                .orElseThrow(()->new IllegalArgumentException("Aula no existe")));
    }
}

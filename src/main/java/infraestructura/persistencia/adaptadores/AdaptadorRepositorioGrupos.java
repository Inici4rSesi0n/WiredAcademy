/*
 * SPDX-FileCopyrightText: 2026 inici4rsesi0n
 * SPDX-License-Identifier: GPL-3.0-or-later
 */
package infraestructura.persistencia.adaptadores;
import dominio.modelo.Grupo;
import dominio.puerto.repositorio.RepositorioGrupos;
import infraestructura.persistencia.entidades.GrupoEntity;
import infraestructura.persistencia.jpa.DocenteJpaRepository;
import infraestructura.persistencia.jpa.GradoJpaRepository;
import infraestructura.persistencia.jpa.GrupoJpaRepository;
import infraestructura.persistencia.mappers.GrupoMapper;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.Optional;
/**
 *
 * @author inici4rsesi0n
 */
@Repository
public class AdaptadorRepositorioGrupos implements RepositorioGrupos{
    private final GrupoJpaRepository jpaRepository;
    private final GradoJpaRepository gradoJpa;
    private final DocenteJpaRepository docenteJpa;
    private final GrupoMapper mapper;
    public AdaptadorRepositorioGrupos(GrupoJpaRepository jpaRepository, GradoJpaRepository gradoJpa,
                                       DocenteJpaRepository docenteJpa, GrupoMapper mapper){
        this.jpaRepository=jpaRepository;
        this.gradoJpa=gradoJpa;
        this.docenteJpa=docenteJpa;
        this.mapper=mapper;
    }
    @Override @Transactional
    public void agregar(Grupo grupo){
        if(grupo==null) throw new IllegalArgumentException("El grupo no puede ser nulo");
        GrupoEntity entity = mapper.toEntity(grupo);
        entity.setModalidad(grupo.getModalidad().name());
        entity.setGrado(gradoJpa.findByNombreAndNivel(grupo.getGrado().getNombre(), grupo.getGrado().getNivel())
                .orElseThrow(()->new IllegalArgumentException("Grado no existe")));
        if(grupo.getTutor() != null) {
            entity.setTutor(docenteJpa.findByCodigo(grupo.getTutor().getCodigo())
                    .orElseThrow(()->new IllegalArgumentException("Tutor no existe")));
        }
        jpaRepository.saveAndFlush(entity);
    }
    @Override
    public Optional<Grupo> buscarPorNombre(String nombre){
        return jpaRepository.findByNombre(nombre).map(mapper::toDomain);
    }
    @Override
    public List<Grupo> buscarPorNombreYGrado(String nombreGrupo, String nombreGrado){
        return mapper.toDomainList(jpaRepository.findByNombreAndGradoNombre(nombreGrupo, nombreGrado));
    }
    @Override
    public List<Grupo> listarTodos(){
        return mapper.toDomainList(jpaRepository.findAll());
    }
    @Override @Transactional
    public void actualizar(Grupo original, Grupo actualizado){
        GrupoEntity entity=jpaRepository.findByNombre(original.getNombre())
                .orElseThrow(()->new IllegalArgumentException("El grupo no existe"));
        entity.setNombre(actualizado.getNombre());
        entity.setGrado(gradoJpa.findByNombreAndNivel(actualizado.getGrado().getNombre(), actualizado.getGrado().getNivel())
                .orElseThrow(()->new IllegalArgumentException("Grado no existe")));
        if(actualizado.getTutor() != null) {
            entity.setModalidad(actualizado.getModalidad().name());
        entity.setTutor(docenteJpa.findByCodigo(actualizado.getTutor().getCodigo())
                    .orElseThrow(()->new IllegalArgumentException("Tutor no existe")));
        } else {
            entity.setTutor(null);
        }
        jpaRepository.saveAndFlush(entity);
    }
    @Override @Transactional
    public void eliminar(Grupo grupo){
        jpaRepository.findByNombre(grupo.getNombre()).ifPresent(jpaRepository::delete);
    }
}

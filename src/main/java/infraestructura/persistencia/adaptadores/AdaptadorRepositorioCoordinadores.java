/*
 * SPDX-FileCopyrightText: 2026 inici4rsesi0n
 * SPDX-License-Identifier: GPL-3.0-or-later
 */
package infraestructura.persistencia.adaptadores;
import dominio.modelo.CoordinadorAcademico;
import dominio.puerto.repositorio.RepositorioCoordinadores;
import infraestructura.persistencia.entidades.CoordinadorEntity;
import infraestructura.persistencia.entidades.GradoEntity;
import infraestructura.persistencia.jpa.CoordinadorJpaRepository;
import infraestructura.persistencia.jpa.GradoJpaRepository;
import infraestructura.persistencia.mappers.CoordinadorMapper;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.Optional;
/**
 *
 * @author inici4rsesi0n
 */
@Repository
public class AdaptadorRepositorioCoordinadores implements RepositorioCoordinadores{
    private final CoordinadorJpaRepository jpaRepository;
    private final GradoJpaRepository gradoJpa;
    private final CoordinadorMapper mapper;
    
    public AdaptadorRepositorioCoordinadores(CoordinadorJpaRepository jpaRepository, GradoJpaRepository gradoJpa, CoordinadorMapper mapper){
        this.jpaRepository=jpaRepository;
        this.gradoJpa=gradoJpa;
        this.mapper=mapper;
    }
    
    @Override @Transactional
    public void agregar(CoordinadorAcademico coordinador){
        if(coordinador==null) throw new IllegalArgumentException("El coordinador no puede ser nulo");
        CoordinadorEntity entity = mapper.toEntity(coordinador);
        entity.setGradosSupervisados(resolverGrados(coordinador));
        jpaRepository.saveAndFlush(entity);
    }
    @Override
    public Optional<CoordinadorAcademico> buscarPorCodigo(String codigo){
        return jpaRepository.findByCodigo(codigo).map(mapper::toDomain);
    }
    @Override
    public List<CoordinadorAcademico> listarTodos(){
        return mapper.toDomainList(jpaRepository.findAll());
    }
    @Override @Transactional
    public void actualizar(CoordinadorAcademico original, CoordinadorAcademico actualizado){
        CoordinadorEntity entity=jpaRepository.findByCodigo(original.getCodigo())
                .orElseThrow(()->new IllegalArgumentException("El coordinador no existe"));
        entity.setNombre(actualizado.getNombre());
        entity.setApellido(actualizado.getApellido());
        entity.setEdad(actualizado.getEdad());
        entity.setHashContrasena(actualizado.getHashContrasena());
        entity.setDni(actualizado.getDni());
        entity.setGradosSupervisados(resolverGrados(actualizado));
        jpaRepository.saveAndFlush(entity);
    }
    @Override @Transactional
    public void eliminar(CoordinadorAcademico coordinador){
        jpaRepository.findByCodigo(coordinador.getCodigo()).ifPresent(jpaRepository::delete);
    }
    
    private List<GradoEntity> resolverGrados(CoordinadorAcademico coord){
        if(coord.getGradosSupervisados() == null || coord.getGradosSupervisados().isEmpty()) return new java.util.ArrayList<>();
        return coord.getGradosSupervisados().stream()
                .map(g -> gradoJpa.findByNombreAndNivel(g.getNombre(), g.getNivel())
                        .orElseThrow(()->new IllegalArgumentException("Grado no existe: " + g.getNombre())))
                .collect(java.util.stream.Collectors.toList());
    }
}

/*
 * SPDX-FileCopyrightText: 2026 inici4rsesi0n
 * SPDX-License-Identifier: GPL-3.0-or-later
 */
package infraestructura.persistencia.adaptadores;
import dominio.modelo.Estudiante;
import dominio.puerto.repositorio.RepositorioEstudiantes;
import infraestructura.persistencia.entidades.EstudianteEntity;
import infraestructura.persistencia.jpa.EstudianteJpaRepository;
import infraestructura.persistencia.mappers.EstudianteMapper;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.Optional;
/**
 *
 * @author inici4rsesi0n
 */
@Repository
public class AdaptadorRepositorioEstudiantes implements RepositorioEstudiantes{
    private final EstudianteJpaRepository jpaRepository;
    private final EstudianteMapper mapper;
    public AdaptadorRepositorioEstudiantes(EstudianteJpaRepository jpaRepository, EstudianteMapper mapper){
        this.jpaRepository=jpaRepository;
        this.mapper=mapper;
    }
    @Override
    @Transactional
    public void agregar(Estudiante estudiante){
        if(estudiante==null) throw new IllegalArgumentException("El estudiante no puede ser nulo");
        jpaRepository.save(mapper.toEntity(estudiante));
    }
    @Override
    public Optional<Estudiante> buscarPorDni(String dni){
        return jpaRepository.findByDni(dni).map(mapper::toDomain);
    }
    @Override
    public Optional<Estudiante> buscarPorCodigo(String codigo){
        return jpaRepository.findByCodigo(codigo).map(mapper::toDomain);
    }
    @Override
    public List<Estudiante> listarTodos(){
        return mapper.toDomainList(jpaRepository.findAll());
    }
    @Override
    @Transactional
    public void actualizar(Estudiante original, Estudiante actualizado){
        EstudianteEntity entity=jpaRepository.findByCodigo(original.getCodigo())
                .orElseThrow(()->new IllegalArgumentException("El estudiante no existe"));
        entity.setNombre(actualizado.getNombre());
        entity.setApellido(actualizado.getApellido());
        entity.setEdad(actualizado.getEdad());
        entity.setHashContrasena(actualizado.getHashContrasena());
        entity.setDni(actualizado.getDni());
        jpaRepository.save(entity);
    }
    @Override
    @Transactional
    public void eliminar(Estudiante estudiante){
        jpaRepository.findByCodigo(estudiante.getCodigo()).ifPresent(jpaRepository::delete);
    }
}

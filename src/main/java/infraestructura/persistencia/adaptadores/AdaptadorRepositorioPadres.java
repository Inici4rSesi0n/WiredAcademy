/*
 * SPDX-FileCopyrightText: 2026 inici4rsesi0n
 * SPDX-License-Identifier: GPL-3.0-or-later
 */
package infraestructura.persistencia.adaptadores;
import dominio.modelo.Padre;
import dominio.puerto.repositorio.RepositorioPadres;
import infraestructura.persistencia.entidades.EstudianteEntity;
import infraestructura.persistencia.entidades.PadreEntity;
import infraestructura.persistencia.jpa.EstudianteJpaRepository;
import infraestructura.persistencia.jpa.PadreJpaRepository;
import infraestructura.persistencia.mappers.PadreMapper;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.Optional;
/**
 *
 * @author inici4rsesi0n
 */
@Repository
public class AdaptadorRepositorioPadres implements RepositorioPadres{
    private final PadreJpaRepository jpaRepository;
    private final EstudianteJpaRepository estudianteJpa;
    private final PadreMapper mapper;
    public AdaptadorRepositorioPadres(PadreJpaRepository jpaRepository, EstudianteJpaRepository estudianteJpa, PadreMapper mapper){
        this.jpaRepository=jpaRepository;
        this.estudianteJpa=estudianteJpa;
        this.mapper=mapper;
    }
    @Override @Transactional
    public void agregar(Padre padre){
        if(padre==null) throw new IllegalArgumentException("El padre no puede ser nulo");
        PadreEntity entity = mapper.toEntity(padre);
        entity.setEstudiantes(resolverEstudiantes(padre));
        jpaRepository.save(entity);
    }
    @Override
    public Optional<Padre> buscarPorCodigo(String codigo){
        return jpaRepository.findByCodigo(codigo).map(mapper::toDomain);
    }
    @Override
    public List<Padre> listarTodos(){
        return mapper.toDomainList(jpaRepository.findAll());
    }
    @Override @Transactional
    public void actualizar(Padre original, Padre actualizado){
        PadreEntity entity=jpaRepository.findByCodigo(original.getCodigo())
                .orElseThrow(()->new IllegalArgumentException("El padre no existe"));
        entity.setNombre(actualizado.getNombre());
        entity.setApellido(actualizado.getApellido());
        entity.setEdad(actualizado.getEdad());
        entity.setHashContrasena(actualizado.getHashContrasena());
        entity.setDni(actualizado.getDni());
        entity.setEstudiantes(resolverEstudiantes(actualizado));
        jpaRepository.save(entity);
    }
    @Override @Transactional
    public void eliminar(Padre padre){
        jpaRepository.findByCodigo(padre.getCodigo()).ifPresent(jpaRepository::delete);
    }
    private List<EstudianteEntity> resolverEstudiantes(Padre padre){
        if(padre.getEstudiantes() == null || padre.getEstudiantes().isEmpty()) return new java.util.ArrayList<>();
        return padre.getEstudiantes().stream()
                .map(e -> estudianteJpa.findByCodigo(e.getCodigo())
                        .orElseThrow(()->new IllegalArgumentException("Estudiante no existe: " + e.getCodigo())))
                .collect(java.util.stream.Collectors.toList());
    }
}

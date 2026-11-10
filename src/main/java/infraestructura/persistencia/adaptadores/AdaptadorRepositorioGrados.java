/*
 * SPDX-FileCopyrightText: 2026 inici4rsesi0n
 * SPDX-License-Identifier: GPL-3.0-or-later
 */
package infraestructura.persistencia.adaptadores;
import dominio.modelo.Grado;
import dominio.puerto.repositorio.RepositorioGrados;
import infraestructura.persistencia.entidades.GradoEntity;
import infraestructura.persistencia.jpa.GradoJpaRepository;
import infraestructura.persistencia.mappers.GradoMapper;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.Optional;
/**
 *
 * @author inici4rsesi0n
 */
@Repository
public class AdaptadorRepositorioGrados implements RepositorioGrados{
    private final GradoJpaRepository jpaRepository;
    private final GradoMapper mapper;
    public AdaptadorRepositorioGrados(GradoJpaRepository jpaRepository, GradoMapper mapper){
        this.jpaRepository=jpaRepository;
        this.mapper=mapper;
    }
    @Override @Transactional
    public void agregar(Grado grado){
        if(grado==null) throw new IllegalArgumentException("El grado no puede ser nulo");
        jpaRepository.save(mapper.toEntity(grado));
    }
    @Override
    public Optional<Grado> buscarPorNombre(String nombre){
        return jpaRepository.findByNombre(nombre).map(mapper::toDomain);
    }
    @Override
    public Optional<Grado> buscarPorNombreYNivel(String nombre, String nivel){
        return jpaRepository.findByNombreAndNivel(nombre, nivel).map(mapper::toDomain);
    }
    @Override
    public List<Grado> listarTodos(){
        return mapper.toDomainList(jpaRepository.findAll());
    }
    @Override @Transactional
    public void actualizar(Grado original, Grado actualizado){
        GradoEntity entity=jpaRepository.findByNombreAndNivel(original.getNombre(), original.getNivel())
                .orElseThrow(()->new IllegalArgumentException("El grado no existe"));
        entity.setNombre(actualizado.getNombre());
        entity.setNivel(actualizado.getNivel());
        jpaRepository.save(entity);
    }
    @Override @Transactional
    public void eliminar(Grado grado){
        jpaRepository.findByNombreAndNivel(grado.getNombre(), grado.getNivel()).ifPresent(jpaRepository::delete);
    }
}

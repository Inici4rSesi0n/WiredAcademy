/*
 * SPDX-FileCopyrightText: 2026 inici4rsesi0n
 * SPDX-License-Identifier: GPL-3.0-or-later
 */
package infraestructura.persistencia.adaptadores;
import dominio.modelo.Tesorero;
import dominio.puerto.repositorio.RepositorioTesoreros;
import infraestructura.persistencia.entidades.TesoreroEntity;
import infraestructura.persistencia.jpa.TesoreroJpaRepository;
import infraestructura.persistencia.mappers.TesoreroMapper;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.Optional;
/**
 *
 * @author inici4rsesi0n
 */
@Repository
public class AdaptadorRepositorioTesoreros implements RepositorioTesoreros{
    private final TesoreroJpaRepository jpaRepository;
    private final TesoreroMapper mapper;
    public AdaptadorRepositorioTesoreros(TesoreroJpaRepository jpaRepository, TesoreroMapper mapper){
        this.jpaRepository=jpaRepository;
        this.mapper=mapper;
    }
    @Override @Transactional
    public void agregar(Tesorero tesorero){
        if(tesorero==null) throw new IllegalArgumentException("El tesorero no puede ser nulo");
        jpaRepository.save(mapper.toEntity(tesorero));
    }
    @Override
    public Optional<Tesorero> buscarPorCodigo(String codigo){
        return jpaRepository.findByCodigo(codigo).map(mapper::toDomain);
    }
    @Override
    public List<Tesorero> listarTodos(){
        return mapper.toDomainList(jpaRepository.findAll());
    }
    @Override @Transactional
    public void actualizar(Tesorero original, Tesorero actualizado){
        TesoreroEntity entity=jpaRepository.findByCodigo(original.getCodigo())
                .orElseThrow(()->new IllegalArgumentException("El tesorero no existe"));
        entity.setNombre(actualizado.getNombre());
        entity.setApellido(actualizado.getApellido());
        entity.setEdad(actualizado.getEdad());
        entity.setHashContrasena(actualizado.getHashContrasena());
        entity.setDni(actualizado.getDni());
        jpaRepository.save(entity);
    }
    @Override @Transactional
    public void eliminar(Tesorero tesorero){
        jpaRepository.findByCodigo(tesorero.getCodigo()).ifPresent(jpaRepository::delete);
    }
}

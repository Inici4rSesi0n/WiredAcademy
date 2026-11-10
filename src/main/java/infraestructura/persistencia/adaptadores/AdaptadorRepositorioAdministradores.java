/*
 * SPDX-FileCopyrightText: 2026 inici4rsesi0n
 * SPDX-License-Identifier: GPL-3.0-or-later
 */
package infraestructura.persistencia.adaptadores;
import dominio.modelo.Administrador;
import dominio.puerto.repositorio.RepositorioAdministradores;
import infraestructura.persistencia.entidades.AdministradorEntity;
import infraestructura.persistencia.jpa.AdministradorJpaRepository;
import infraestructura.persistencia.mappers.AdministradorMapper;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.Optional;
/**
 *
 * @author inici4rsesi0n
 */
@Repository
public class AdaptadorRepositorioAdministradores implements RepositorioAdministradores{
    private final AdministradorJpaRepository jpaRepository;
    private final AdministradorMapper mapper;
    public AdaptadorRepositorioAdministradores(AdministradorJpaRepository jpaRepository, AdministradorMapper mapper){
        this.jpaRepository=jpaRepository;
        this.mapper=mapper;
    }
    @Override @Transactional
    public void agregar(Administrador administrador){
        if(administrador==null) throw new IllegalArgumentException("El administrador no puede ser nulo");
        jpaRepository.save(mapper.toEntity(administrador));
    }
    @Override
    public Optional<Administrador> buscarPorCodigo(String codigo){
        return jpaRepository.findByCodigo(codigo).map(mapper::toDomain);
    }
    @Override
    public List<Administrador> listarTodos(){
        return mapper.toDomainList(jpaRepository.findAll());
    }
    @Override @Transactional
    public void actualizar(Administrador original, Administrador actualizado){
        AdministradorEntity entity=jpaRepository.findByCodigo(original.getCodigo())
                .orElseThrow(()->new IllegalArgumentException("El administrador no existe"));
        entity.setNombre(actualizado.getNombre());
        entity.setApellido(actualizado.getApellido());
        entity.setEdad(actualizado.getEdad());
        entity.setHashContrasena(actualizado.getHashContrasena());
        entity.setDni(actualizado.getDni());
        jpaRepository.save(entity);
    }
    @Override @Transactional
    public void eliminar(Administrador administrador){
        jpaRepository.findByCodigo(administrador.getCodigo()).ifPresent(jpaRepository::delete);
    }
}

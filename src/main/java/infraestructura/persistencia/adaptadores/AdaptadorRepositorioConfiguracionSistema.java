/*
 * SPDX-FileCopyrightText: 2026 inici4rsesi0n
 * SPDX-License-Identifier: GPL-3.0-or-later
 */
package infraestructura.persistencia.adaptadores;
import dominio.modelo.configuracion.ConfiguracionSistema;
import dominio.puerto.repositorio.RepositorioConfiguracionSistema;
import infraestructura.persistencia.entidades.ConfiguracionSistemaEntity;
import infraestructura.persistencia.jpa.ConfiguracionSistemaJpaRepository;
import infraestructura.persistencia.mappers.ConfiguracionSistemaMapper;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import java.util.Optional;
/**
 *
 * @author inici4rsesi0n
 */
@Repository
public class AdaptadorRepositorioConfiguracionSistema implements RepositorioConfiguracionSistema {
    private final ConfiguracionSistemaJpaRepository jpaRepository;
    private final ConfiguracionSistemaMapper mapper;
    public AdaptadorRepositorioConfiguracionSistema(ConfiguracionSistemaJpaRepository jpaRepository, ConfiguracionSistemaMapper mapper) {
        this.jpaRepository = jpaRepository;
        this.mapper = mapper;
    }
    @Override
    public Optional<ConfiguracionSistema> obtener() {
        return jpaRepository.findAll().stream().findFirst().map(mapper::toDomain);
    }
    @Override @Transactional
    public void guardar(ConfiguracionSistema configuracion) {
        ConfiguracionSistemaEntity entity = jpaRepository.findAll().stream().findFirst().orElse(new ConfiguracionSistemaEntity());
        entity.setTipoInstitucion(configuracion.getTipoInstitucion().name());
        jpaRepository.save(entity);
    }
}

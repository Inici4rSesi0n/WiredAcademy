/*
 * SPDX-FileCopyrightText: 2026 inici4rsesi0n
 * SPDX-License-Identifier: GPL-3.0-or-later
 */
package infraestructura.persistencia.jpa;
import infraestructura.persistencia.entidades.ConfiguracionSistemaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
/**
 *
 * @author inici4rsesi0n
 */
@Repository
public interface ConfiguracionSistemaJpaRepository extends JpaRepository<ConfiguracionSistemaEntity, Long>{}

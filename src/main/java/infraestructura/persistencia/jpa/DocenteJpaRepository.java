/*
 * SPDX-FileCopyrightText: 2026 inici4rsesi0n
 * SPDX-License-Identifier: GPL-3.0-or-later
 */
package infraestructura.persistencia.jpa;
import infraestructura.persistencia.entidades.DocenteEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;
/**
 *
 * @author inici4rsesi0n
 */
@Repository
public interface DocenteJpaRepository extends JpaRepository<DocenteEntity, Long>{
    Optional<DocenteEntity> findByCodigo(String codigo);
    Optional<DocenteEntity> findByDni(String dni);
}

/*
 * SPDX-FileCopyrightText: 2026 inici4rsesi0n
 * SPDX-License-Identifier: GPL-3.0-or-later
 */
package infraestructura.persistencia.jpa;
import infraestructura.persistencia.entidades.PublicacionEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
@Repository
public interface PublicacionJpaRepository extends JpaRepository<PublicacionEntity, Long> {
    List<PublicacionEntity> findByAsignaturaNombreOrderByFechaCreacionDesc(String nombreAsignatura);
    List<PublicacionEntity> findByAsignaturaIsNullOrderByFechaCreacionDesc();
}

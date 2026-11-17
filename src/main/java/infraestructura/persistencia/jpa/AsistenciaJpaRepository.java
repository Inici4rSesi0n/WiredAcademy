/*
 * SPDX-FileCopyrightText: 2026 inici4rsesi0n
 * SPDX-License-Identifier: GPL-3.0-or-later
 */
package infraestructura.persistencia.jpa;
import infraestructura.persistencia.entidades.AsistenciaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.time.LocalDate;
import java.util.List;
@Repository
public interface AsistenciaJpaRepository extends JpaRepository<AsistenciaEntity, Long> {
    List<AsistenciaEntity> findByGrupoNombreAndAsignaturaNombreAndFecha(String grupoNombre, String asignaturaNombre, LocalDate fecha);
}

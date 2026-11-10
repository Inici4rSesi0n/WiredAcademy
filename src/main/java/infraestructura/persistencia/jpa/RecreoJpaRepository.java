/*
 * SPDX-FileCopyrightText: 2026 inici4rsesi0n
 * SPDX-License-Identifier: GPL-3.0-or-later
 */
package infraestructura.persistencia.jpa;
import infraestructura.persistencia.entidades.RecreoEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
/**
 *
 * @author inici4rsesi0n
 */
@Repository
public interface RecreoJpaRepository extends JpaRepository<RecreoEntity, Long>{
    List<RecreoEntity> findByDescripcion(String descripcion);
    List<RecreoEntity> findByDescripcionAndDiaSemanaAndHoraInicioAndHoraFin(
            String descripcion, String diaSemana, String horaInicio, String horaFin);
}

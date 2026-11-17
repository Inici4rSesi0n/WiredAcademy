/*
 * SPDX-FileCopyrightText: 2026 inici4rsesi0n
 * SPDX-License-Identifier: GPL-3.0-or-later
 */
package aplicacion.casosdeuso.portaldeingreso.miareapersonal;
import dominio.modelo.*;
import dominio.puerto.externo.EventPublisher;
import dominio.puerto.externo.LoggerPort;
import dominio.puerto.repositorio.RepositorioAsistencias;
import org.springframework.stereotype.Service;
import java.time.LocalDate;
import java.util.List;
@Service
public class GestionAsistencias {
    private final RepositorioAsistencias repo;
    private final LoggerPort logger;
    private final EventPublisher publisher;
    public GestionAsistencias(RepositorioAsistencias repo, LoggerPort logger, EventPublisher publisher) {
        this.repo = repo;
        this.logger = logger;
        this.publisher = publisher;
    }
    public void registrarAsistencia(Grupo grupo, Asignatura asignatura, Estudiante estudiante, LocalDate fecha, EstadoAsistencia estado) {
        Asistencia asistencia = new Asistencia(null, grupo, asignatura, estudiante, fecha, estado);
        repo.agregar(asistencia);
        logger.audit(String.format("op=CREATE entity=ASISTENCIA grupo=%s asignatura=%s estudiante=%s fecha=%s estado=%s",
                grupo.getNombre(), asignatura.getNombre(), estudiante.getCodigo(), fecha, estado));
        publisher.notificar(TipoEvento.ASISTENCIAS);
    }
    public List<Asistencia> listarPorGrupoAsignaturaYFecha(String grupoNombre, String asignaturaNombre, LocalDate fecha) {
        return repo.buscarPorGrupoAsignaturaYFecha(grupoNombre, asignaturaNombre, fecha);
    }
}

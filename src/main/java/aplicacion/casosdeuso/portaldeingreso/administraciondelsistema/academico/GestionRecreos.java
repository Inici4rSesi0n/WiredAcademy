/*
 * SPDX-FileCopyrightText: 2026 inici4rsesi0n
 * SPDX-License-Identifier: GPL-3.0-or-later
 */
package aplicacion.casosdeuso.portaldeingreso.administraciondelsistema.academico;
import dominio.modelo.FranjaHoraria;
import dominio.modelo.PeriodoAcademico;
import dominio.modelo.Recreo;
import dominio.puerto.externo.LoggerPort;
import dominio.puerto.externo.EventPublisher;
import dominio.modelo.TipoEvento;
import dominio.puerto.repositorio.RepositorioRecreos;
import org.springframework.stereotype.Service;
import java.util.List;
/**
 *
 * @author inici4rsesi0n
 */
@Service
public class GestionRecreos {
    private final RepositorioRecreos repo;
    private final LoggerPort logger;
    private final EventPublisher publisher;
    public GestionRecreos(RepositorioRecreos repo, LoggerPort logger, EventPublisher publisher) {
        this.repo = repo;
        this.logger = logger;
        this.publisher = publisher;
    }
    public void crearRecreo(FranjaHoraria franja, String descripcion, List<PeriodoAcademico> periodos) {
        if (franja == null) throw new IllegalArgumentException("La franja horaria es obligatoria.");
        if (periodos == null || periodos.isEmpty()) throw new IllegalArgumentException("Debe asociar al menos un periodo al recreo.");
        String descFinal = (descripcion != null && !descripcion.isBlank()) ? descripcion : "Recreo";
        validarDuplicado(franja, descFinal, periodos, null);
        repo.agregar(new Recreo(franja, descFinal, periodos));
        logger.audit(String.format("op=CREATE entity=RECREO id=%s | desc=%s periodos=%d", franja, descFinal, periodos.size()));
        publisher.notificar(TipoEvento.RECREOS);
    }
    public Recreo buscarPorDescripcion(String descripcion) { return repo.buscarPorDescripcion(descripcion).orElse(null); }
    public List<Recreo> listarTodos() { return repo.listarTodos(); }
    public void actualizarRecreo(Recreo original, FranjaHoraria franja, String descripcion, List<PeriodoAcademico> periodos) {
        if (franja == null) throw new IllegalArgumentException("La franja horaria es obligatoria.");
        String descFinal = (descripcion != null && !descripcion.isBlank()) ? descripcion : "Recreo";
        validarDuplicado(franja, descFinal, periodos, original);
        String antes = original.toString();
        Recreo actualizado = new Recreo(franja, descFinal, periodos);
        repo.actualizar(original, actualizado);
        logger.audit(String.format("op=UPDATE entity=RECREO | before=%s after=%s", antes, actualizado));
        publisher.notificar(TipoEvento.RECREOS);
    }
    public void aplicarA(Recreo recreo, PeriodoAcademico periodo) {
        if (recreo == null || periodo == null) throw new IllegalArgumentException("Recreo y periodo son obligatorios.");
        recreo.aplicarA(periodo);
        repo.actualizar(recreo, recreo);
        logger.audit(String.format("op=LINK entity=RECREO id=%s -> PERIODO id=%s", recreo.getFranja(), periodo.getNombre()));
        publisher.notificar(TipoEvento.RECREOS);
    }
    public void removerDe(Recreo recreo, PeriodoAcademico periodo) {
        if (recreo == null || periodo == null) throw new IllegalArgumentException("Recreo y periodo son obligatorios.");
        recreo.removerDe(periodo);
        repo.actualizar(recreo, recreo);
        logger.audit(String.format("op=UNLINK entity=RECREO id=%s -> PERIODO id=%s", recreo.getFranja(), periodo.getNombre()));
        publisher.notificar(TipoEvento.RECREOS);
    }
    public void eliminarRecreo(Recreo recreo) {
        if (recreo == null) throw new IllegalArgumentException("El recreo no puede ser nulo");
        String snapshot = recreo.toString();
        repo.eliminar(recreo);
        logger.audit(String.format("op=DELETE entity=RECREO | snapshot=%s", snapshot));
        publisher.notificar(TipoEvento.RECREOS);
    }
    private void validarDuplicado(FranjaHoraria franja, String descripcion, List<PeriodoAcademico> periodos, Recreo excluido) {
        for (Recreo r : repo.listarTodos()) {
            if (r == excluido) continue;
            if (!r.getFranja().equals(franja)) continue;
            if (!r.getDescripcion().equalsIgnoreCase(descripcion)) continue;
            for (PeriodoAcademico p : r.getPeriodos()) {
                if (periodos.contains(p)) throw new IllegalArgumentException("Ya existe un recreo con esa franja, descripción y periodo compartido.");
            }
        }
    }
}

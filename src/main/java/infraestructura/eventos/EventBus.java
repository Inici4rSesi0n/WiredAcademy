package infraestructura.eventos;
import dominio.modelo.TipoEvento;
import dominio.puerto.externo.EventBusListener;
import jakarta.annotation.PreDestroy;
import javafx.application.Platform;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
/**
 * @author inici4rsesi0n
 */
@Component
public class EventBus {
    private static final Logger log = LoggerFactory.getLogger("WiredAcademy.EVENTBUS");
    private final Map<TipoEvento, List<WeakReference<EventBusListener>>> suscriptores = new EnumMap<>(TipoEvento.class);
    public void suscribir(TipoEvento evento, EventBusListener listener) {
        if (evento == null || listener == null) return;
        suscriptores.computeIfAbsent(evento, k -> new ArrayList<>()).add(new WeakReference<>(listener));
        log.debug("[EVENT] suscribir evento={} listener={}", evento, listener.getClass().getSimpleName());
    }
    public void desuscribir(TipoEvento evento, EventBusListener listener) {
        if (evento == null || listener == null) return;
        List<WeakReference<EventBusListener>> lista = suscriptores.get(evento);
        if (lista != null && lista.removeIf(ref -> ref.get() == null || ref.get().equals(listener))) {
            log.debug("[EVENT] desuscribir evento={} listener={}", evento, listener.getClass().getSimpleName());
        }
    }
    public void notificar(TipoEvento... eventos) {
        if (eventos == null) return;
        for (TipoEvento e : eventos) notificar(e);
    }
    public void notificar(TipoEvento evento) {
        if (evento == null) return;
        List<WeakReference<EventBusListener>> lista = suscriptores.get(evento);
        if (lista == null || lista.isEmpty()) {
            log.warn("[EVENT] publicar evento={} SIN SUSCRIPTORES", evento);
            return;
        }
        List<EventBusListener> activos = new ArrayList<>();
        lista.removeIf(ref -> {
            EventBusListener l = ref.get();
            if (l != null) { activos.add(l); return false; }
            return true;
        });
        if (activos.isEmpty()) {
            log.warn("[EVENT] publicar evento={} SIN SUSCRIPTORES ACTIVOS (GC)", evento);
            return;
        }
        log.debug("[EVENT] publicar evento={} subscribers={}", evento, activos.size());
        final TipoEvento evt = evento;
        Platform.runLater(() -> {
            for (EventBusListener l : activos) {
                try {
                    l.onEvent(evt);
                } catch (Exception e) {
                    log.error("[EVENT] listener={} fallo para evento={}: {}", l.getClass().getSimpleName(), evt, e.getMessage());
                }
            }
        });
    }
    @PreDestroy
    public void shutdown() {
        int total = suscriptores.values().stream().mapToInt(List::size).sum();
        suscriptores.clear();
        log.info("[EVENT] EventBus detenido ({} suscripciones limpiadas)", total);
    }
}

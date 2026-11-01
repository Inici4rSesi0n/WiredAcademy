package dominio.puerto.externo;
import dominio.modelo.TipoEvento;
/**
 * @author inici4rsesi0n
 */
public interface EventPublisher {
    void notificar(TipoEvento evento);
    void notificar(TipoEvento... eventos);
}

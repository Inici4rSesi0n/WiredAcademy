package dominio.puerto.externo;
import dominio.modelo.TipoEvento;
/**
 * @author inici4rsesi0n
 */
public interface EventBusListener {
    void onEvent(TipoEvento evento);
}

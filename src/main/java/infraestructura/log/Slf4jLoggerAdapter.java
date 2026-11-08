package infraestructura.log;
import dominio.puerto.externo.LoggerPort;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.stereotype.Component;
/**
 *
 * @author inici4rsesi0n
 */
@Component
public class Slf4jLoggerAdapter implements LoggerPort {
    private static final String ROOT_NAME = "WiredAcademy";
    private static final String AUDIT_NAME = "WiredAcademy.AUDIT";
    private final Logger root;
    private final Logger audit;
    public Slf4jLoggerAdapter() {
        this.root = LoggerFactory.getLogger(ROOT_NAME);
        this.audit = LoggerFactory.getLogger(AUDIT_NAME);
    }
    @Override
    public void setUsuario(String codigoUsuario) {
        if (codigoUsuario == null || codigoUsuario.isBlank()) {
            MDC.remove("usuario");
        } else {
            MDC.put("usuario", codigoUsuario);
        }
    }
    @Override public void info(String mensaje) { root.info(mensaje); }
    @Override public void info(String formato, Object... argumentos) { root.info(formato, argumentos); }
    @Override public void warn(String mensaje) { root.warn(mensaje); }
    @Override public void warn(String formato, Object... argumentos) { root.warn(formato, argumentos); }
    @Override public void error(String mensaje) { root.error(mensaje); }
    @Override public void error(String mensaje, Throwable excepcion) { root.error(mensaje, excepcion); }
    @Override public void debug(String mensaje) { root.debug(mensaje); }
    @Override public void debug(String formato, Object... argumentos) { root.debug(formato, argumentos); }
    @Override public void trace(String mensaje) { root.trace(mensaje); }
    @Override public void audit(String mensaje) { audit.info(mensaje); }
    @Override public void audit(String formato, Object... argumentos) { audit.info(formato, argumentos); }
}

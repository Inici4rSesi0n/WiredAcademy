# LOGGING.md — Convención de logging de WiredAcademy

> Sistema de logging estructurado. Define namespaces, patrones, MDC y convenciones de privacidad.

**Versión:** 2.0  
**Estado:** Activo  
**Mantenedor:** inici4rsesi0n  
**Última revisión:** 2026-11-13

---

## 1. Convención general

- **Un logger raíz** (`WiredAcademy`) para operaciones normales.
- **Un logger de auditoría** (`WiredAcademy.AUDIT`) para operaciones sensibles o trazables post-mortem.
- **Un logger de EventBus** (`WiredAcademy.EVENTBUS`) para eventos de dominio.
- **Un logger de SQL** (`org.hibernate.SQL` → `wiredacademy-sql.log`).
- **Todo pasa por `LoggerPort`** (puerto de dominio). Ningún `LoggerFactory.getLogger` en código de producción salvo en el adaptador `Slf4jLoggerAdapter`.

---

## 2. Namespaces

| Namespace | Nivel | Destino |
|---|---|---|
| `WiredAcademy` | INFO | Consola + app.log + error.log |
| `WiredAcademy.AUDIT` | INFO | Consola + audit.log |
| `WiredAcademy.EVENTBUS` | DEBUG (dev) / WARN (test) / INFO (prod) | eventbus.log |
| `org.hibernate.SQL` | DEBUG | sql.log |

---

## 3. Patrones (`logback-spring.xml`)

```
PATRON_AUDIT   = %d{yyyy-MM-dd HH:mm:ss.SSS} [%X{usuario:--}] %msg%n
PATRON_ERROR   = %d{yyyy-MM-dd HH:mm:ss.SSS} [%thread] %-5level %logger - %msg%n%ex
PATRON_SQL     = %d{HH:mm:ss.SSS} [SQL] %msg%n
PATRON_CONSOLE = %d{HH:mm:ss.SSS} [%X{usuario:--}] %-5level %logger{36} - %msg%n
```

---

## 4. MDC con usuario

- Al hacer login (via `AutenticarUsuario`): `logger.setUsuario(codigo)` → `MDC.put("usuario", codigo)`.
- Al hacer logout: `logger.setUsuario(null)` → `MDC.remove("usuario")`.
- Todos los logs subsecuentes en ese thread muestran `[usuario]` en el header.
- Formato por defecto: `[%X{usuario:--}]` (`--` cuando no hay usuario activo).

---

## 5. No PII

**Regla de oro:** nunca loggear PII (dni, email, teléfono, dirección, contraseñas en claro).

- ✅ `op=CREATE entity=ESTUDIANTE id=EST-1`
- ❌ `op=CREATE entity=ESTUDIANTE dni=12345678 nombre=Juan`

Contraseñas viajan como `char[]` y se limpian con `UtilLimpieza.limpiarContraseña`. La limpieza es **idempotente**: se aplica en `BCryptHashProvider` **y** en `LoginController` (finally) como safety net.

---

## 6. Eventos de auditoría (`op=X entity=Y`)

Formato estándar para operaciones CRUD+link:

```
op=<CREATE|UPDATE|DELETE|LINK|UNLINK|READ|LOGIN|LOGOUT|PUBLISH> entity=<ENTIDAD> id=<identificador> [| detalles]
```

Consumidos por `scripts/analyze-logs.sh` sección 4 (conteo de actividad).

---

## 7. Eventos de dominio (`[EVENT]`)

Namespace `WiredAcademy.EVENTBUS`. Formato:

```
[EVENT] publicar evento=<TipoEvento> [SIN SUSCRIPTORES | subscribers=N]
[EVENT] suscribir evento=<TipoEvento> listener=<ClaseSimple>
[EVENT] desuscribir evento=<TipoEvento> listener=<ClaseSimple>
[EVENT] EventBus detenido (N suscripciones limpiadas)
```

Warning `SIN SUSCRIPTORES ACTIVOS (GC)` indica bug: controller destruido no desuscrito.

---

## 8. Eventos de sesión (`[SESSION]`)

Namespace `WiredAcademy.AUDIT`. Formato `[SESSION] <evento> codigo=<codigo> [campos]`.

| Evento | Emisor | Campos |
|---|---|---|
| `login_intento` | LoginController | `codigo`, `rememberMe` |
| `login_exitoso` | LoginController | `codigo`, `rol`, `rememberMe`, `persistencia=escrita\|borrada\|error_escritura` |
| `login_fallido` | LoginController | `codigo`, `motivo=campos_vacios\|credenciales_invalidas` |
| `login_cancelado` | LoginController | — |
| `autologin_intento` | WiredAcademyApp | `codigo` |
| `autologin_exitoso` | WiredAcademyApp | `codigo`, `rol` |
| `autologin_fallido` | WiredAcademyApp | `codigo`, `motivo=sin_archivo\|usuario_inexistente` |
| `logout` | MainController | `codigo`, `persistencia_borrada=bool` |
| `logout_auto` | MainController (navegarAVista) | `motivo=volver_a_main_sin_persistencia`, `codigo` |
| `seed_mode_started` | WiredAcademyApp | — |
| `seed_mode_completed` | WiredAcademyApp | — |

Ver `docs/modulos/autenticacion/LOGIN.md` para el flujo completo.

---

## 9. Bot post-ejecución

`scripts/run-bot.sh {fast|ui|full}` analiza logs tras tests:

1. Errores no conocidos en `wiredacademy-error.log` (filtrados contra `scripts/dev/log-baseline.txt`).
2. `SIN SUSCRIPTORES ACTIVOS` en `wiredacademy-eventbus.log`.
3. Bugs históricos (`Query did not return a unique result`, `value too long for type character varying`).
4. Actividad (conteos de mutaciones + eventos publicados).

---

## 10. Referencias cruzadas

- **`docs/modulos/autenticacion/LOGIN.md`** — flujo de login y sesión.
- **`docs/EVENTBUS.md`** — contrato B1 y tipos de eventos.
- **`docs/ARCHITECTURE.md`** — principios generales.
- **`docs/DATABASE.md`** — esquema y migraciones.

---

## 11. Decisiones arquitectónicas aceptadas (ADR-001, ADR-002, ADR-005)

Históricamente documentados aquí. A partir de ADR-006+ cada ADR vive en `docs/ADR/`. Ver `docs/ADR/README.md`.

- **ADR-001** — Recreos atados a múltiples periodos (N:M).
- **ADR-002** — `equalsIgnoreCase` en entidades con nombre.
- **ADR-005** — Migración de `.bin` a PostgreSQL + JPA (SINGLE_TABLE).

---

## 12. Deuda diferida a post-BBDD

- **D2** — Contraseña del keystore (encriptación de archivos sensibles).
- **D9** — Persistencia de sesión completa en BD. **Estado actual:** parcialmente resuelto por `PersistenciaSesion` (archivo `~/.wiredacademy-session` con flag opt-in). La tabla `sesiones` de V1 sigue sin uso — deuda pendiente hasta que se migre el flujo.

---

## 13. Reevaluar si

- Se integra **JaCoCo** u otra herramienta de cobertura.
- Se añade **RLS** (Row-Level Security) a PostgreSQL.
- Se decide **migrar `SesionUsuario` a BD** (tabla `sesiones` ya existe en V1).

---

**WiredAcademy — Ecosistema Educativo Digital**

# Changelog

Todos los cambios notables de este proyecto se documentan aquí.

**Formato:** [Keep a Changelog](https://keepachangelog.com/es-ES/1.1.0/).  
**Versionado:** [SemVer](https://semver.org/lang/es/).

---

## [Unreleased]

### Added

- Documentación completa: `docs/ARCHITECTURE.md`, `docs/TESTING.md`, `docs/DATABASE.md`, `docs/EVENTBUS.md`, `docs/ADR/`.
- `README.md` con misión del proyecto, casos de uso, quickstart, stack, arquitectura y licencia.
- `CONTRIBUTING.md` con guía de contribución.
- `SECURITY.md` con política de reporte de vulnerabilidades.
- `LICENSE` GPL-3.0-or-later.
- `CHANGELOG.md` (este documento).
- Test de prevención `MonoclePropertiesTest` que verifica la configuración headless del toolkit JavaFX (3 tests).

### Changed

- `LOGGING.md` movido a `docs/LOGGING.md`.

### Fixed

- **CI headless (Monocle):** los 51 contract tests fallaban en GitHub Actions con `Unable to open DISPLAY` → `ExceptionInInitializerError` → `NoClassDefFoundError` en cascada. Causa: `BaseContractTest` llamaba `Platform.startup()` sin configurar `glass.platform=Monocle` ni `monocle.platform=Headless`. Fix en `BaseContractTest` + `pom.xml`. Añadido test de prevención `MonoclePropertiesTest`.
- **CI:** healthcheck PostgreSQL con `-U wiredacademy_app -d wiredacademy_test` (evita `role "root" does not exist` cada 10 s).
- **CI:** `actions/setup-java@v4` → `@v5` (deprecation warning).
- **CI:** nombre del step "Set up JDK 17" corregido a "JDK 21" (2 ocurrencias; la versión real ya era 21).

---

## [0.1.0] — 2026-11-08

Primera consolidación interna tras un ciclo de refactorización profundo (~6 semanas). No es un release público todavía, pero marca el cierre de la fase de estabilización interna.

### Added

#### Fase 0 — Higiene

- `java.version=21` explícito en `pom.xml` (Spring Boot forzaba 17 vía parent).
- `/tmp/` en `.gitignore`.
- Scripts de diagnóstico movidos a `scripts/dev/`.

#### Fase 3 — Use cases notifican

- 12 use cases emiten eventos de dominio vía `EventPublisher` (uno por mutación).
- 51 notificaciones nuevas, distribuidas por área (académico, usuarios, trámites).

#### Fase 4 — Controllers solo consumen

- Eliminadas las 26 llamadas `eventBus.notificar` de los 13 controllers.
- Contrato B1 cerrado: los use cases son la única fuente de eventos.

#### Fase 5 — Contract tests del EventBus

- 12 archivos `*ContractIT.java` con 51 tests end-to-end.
- `TestEventRecorder` + `BaseContractTest` como infraestructura reutilizable.

#### Fase 6 — Smoke suite UI

- 15 UI tests cubriendo los flujos críticos: catálogos, asignaciones, matrícula, usuarios.
- `CatalogCleanupHelper` con orden FK correcto para aislamiento entre tests.

#### Fase 7 — Bot post-ejecución

- `scripts/analyze-logs.sh` (4 secciones).
- `scripts/run-bot.sh` (modos `fast` / `ui` / `full`).
- `scripts/dev/log-baseline.txt` (baseline de errores aceptados).

#### Fase 8 — Limpieza de logging

- Eliminados `eventBus()` y `ui()` de `LoggerPort` (huérfanos desde Fase 4).
- Eliminado `WiredAcademy.UI` de `logback-spring.xml`.
- Refactor de `Slf4jLoggerAdapter` coherente.

#### Fase 9 — DNI agnóstico de país

- Migración `V2__widen_dni_column.sql`: `usuarios.dni` VARCHAR(8) → VARCHAR(20).
- Validación `[A-Za-z0-9]{1,20}` en `Usuario`, `[A-Za-z0-9]{4,20}` en `CrearUsuarioCommand`.
- ADR-007 documentado.
- 11 tests nuevos.

#### Fase 11 — Bugs P0/P1/P2

| ID | Fix |
|---|---|
| **B1** | `@Query LOWER` en 6 JPA repos (case-insensitive contra índices `uq_*_lower`). |
| **B2** | `logger.setUsuario(null)` en logout (MDC limpio). |
| **B3** | `@Transactional` en `asignarTutor` / `removerTutor` / `asignarTutoria`. |
| **B4** | `Map<String,Integer> ORDEN_DIAS` en `GestionHorario`. |
| **B5** | `findFirst` en `AdaptadorRepositorioRecreos` (recreos ambiguos). |
| **B6** | `equalsIgnoreCase` en `Grupo.equals` + `hashCode` consistente. |
| **B7** | fallback `PRESENCIAL` en `Aula.setModalidad(null)`. |

### Changed

- **Arquitectura:** consolidación del contrato B1 (los use cases son la única fuente de eventos).
- **Testing:** de ~403 tests a ~510 tests (495 sin UI + 15 UI).
- **Logging:** jerarquía simplificada, sin métodos huérfanos.

### Fixed

- **Bug histórico 1 (2026-10-18):** `Query did not return a unique result` en recreos → mitigado por `findFirst`.
- **Bug histórico 2 (2026-10-18):** `value too long for type character varying(8)` en DNI → resuelto por migración V2 + validaciones.

### Removed

- Métodos `eventBus(String)` y `ui(String)` de `LoggerPort` (huérfanos).
- Logger `WiredAcademy.UI` en `logback-spring.xml` (sin llamadores).
- `src/test/resources/logback-test.xml` (Spring Boot 3.x prefiere `logback-spring.xml`).

---

## Notas sobre el versionado

- **`0.x`:** fase alpha, no apto para producción. API y esquema pueden cambiar sin aviso.
- **`0.2.0` (próximo):** beta pública con primeras instituciones piloto.
- **`1.0.0` (futuro):** primera versión estable con SemVer completo y API congelada.

---

## Cómo contribuir al CHANGELOG

Cada PR significativo debe actualizar este documento:

1. **Añadir entrada en `[Unreleased]`** con la categoría correspondiente (`Added`, `Changed`, `Deprecated`, `Removed`, `Fixed`, `Security`).
2. **Al hacer release:** mover las entradas de `[Unreleased]` a una nueva sección `[X.Y.Z] — YYYY-MM-DD`.
3. **Nunca borrar entradas históricas.** El histórico es el valor del changelog.

Ver [`CONTRIBUTING.md`](CONTRIBUTING.md) para el detalle completo.

---

## Ver también

- **`README.md`** — Visión general del proyecto.
- **`CONTRIBUTING.md`** — Guía de contribución.
- **`SECURITY.md`** — Política de seguridad.
- **`docs/ARCHITECTURE.md`** — Principios arquitectónicos.
- **`docs/TESTING.md`** — Estrategia de tests.
- **`docs/DATABASE.md`** — Esquema y migraciones.
- **`docs/EVENTBUS.md`** — Contrato B1.

---

**WiredAcademy — Ecosistema Educativo Digital**

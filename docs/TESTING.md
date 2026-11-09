# TESTING.md — Estrategia de testing de WiredAcademy

> Documento maestro de la estrategia de tests del proyecto.

**Versión:** 1.0  
**Estado:** Activo  
**Última revisión:** 2026-11-08  
**Mantenedor:** inici4rsesi0n

---

## 1. Categorías de tests

El proyecto usa **5 categorías** organizadas por alcance y propósito.

| Categoría | Path | Sufijo | Archivos | Ejecuta |
|---|---|---|---:|---|
| **Unitarios de dominio** | `src/test/java/dominio/` | `*Test.java` | ~20 | Instancias puras o mocks |
| **Unitarios de aplicación** | `src/test/java/aplicacion/` | `*Test.java` | ~22 | Mockito (repos + logger + publisher) |
| **Integración (IT)** | `src/test/java/integration/aplicacion/` | `*IT.java` | 20 | BD real (`wiredacademy_test`), rollback por test |
| **Contract tests (EventBus)** | `src/test/java/integration/eventos/` | `*ContractIT.java` | 12 archivos / 51 tests | End-to-end: use case → EventBus → `TestEventRecorder` |
| **UI tests (TestFX)** | `src/test/java/presentacion/` | `*UITest.java` | 15 | TestFX + Monocle headless |

**Total:** ~495 tests sin UI + 15 UI = **~510 tests** en suite completa.

### ¿Cuándo usar cada uno?

| Necesitas verificar... | Usa |
|---|---|
| Lógica pura de una entidad de dominio | **Unitario de dominio** |
| Un use case orquesta correctamente repos + logger | **Unitario de aplicación** |
| Un use case + JPA + PostgreSQL funcionan juntos | **Integración (IT)** |
| Un use case emite el evento correcto end-to-end | **Contract test** |
| Un formulario JavaFX permite crear/editar | **UI test** |

---

## 2. Comandos por modo

### Modos principales

```bash
# Rápido: unitarios + integración (sin UI) ~2 min
./scripts/test.sh fast

# Solo UI: TestFX headless ~5 min
./scripts/test.sh ui

# Full: todo ~10 min
./scripts/test.sh full

# Directo con Maven (sin script)
mvn test                    # Igual que `fast`
mvn test -Pui               # Igual que `ui`
mvn test -Pfull             # Igual que `full`
```

### Ejecutar un solo test (para iterar rápido)

```bash
mvn test -Dtest=GestionUsuariosTest              # Una clase
mvn test -Dtest='Gestion*Test'                   # Pattern
mvn test -Dtest='GestionUsuariosTest#crear*'     # Solo un método
```

### Solo los contract tests del EventBus

```bash
mvn test -Dtest='*ContractIT'
```

### Solo un UI test específico

```bash
mvn test -Pui -Dtest=AsignacionDocenteUITest
```

---

## 3. Política CI/CD

Configuración en `.github/workflows/ci.yml`.

### Triggers y jobs

| Trigger | Job | Modo |
|---|---|---|
| push a cualquier rama | `fast-tests` | `mvn clean test` (sin UI) |
| pull_request a master / main | `full-tests` | `mvn test -Pfull` (con UI) |
| push a master / main | `full-tests` | `mvn test -Pfull` (con UI) |

### Servicios en CI

- **PostgreSQL 16** (efímero, puerto 5433) con healthcheck `pg_isready`.
- **Xvfb** para los UI tests (TestFX + Monocle headless).
- **JDK 21** (Temurin).

### Duración esperada en CI

| Job | Duración |
|---|---|
| `fast-tests` | ~2 min |
| `full-tests` | ~5 min |

---

## 4. Bot post-ejecución (`run-bot.sh`)

Además de los tests, el proyecto tiene un guardián post-ejecución que analiza logs:

```bash
./scripts/run-bot.sh fast
./scripts/run-bot.sh full
```

### Secciones que valida el bot

El bot valida **4 secciones**:

1. **Errores no conocidos** en `wiredacademy-error.log`.
2. **Listeners GC'd** en `wiredacademy-eventbus.log`.
3. **Bugs históricos reincidentes** (`Query did not return a unique result`, `value too long for type character varying`).
4. **Resumen de actividad** (mutaciones + eventos).

**Baseline de errores aceptados:** `scripts/dev/log-baseline.txt` (versionado).  
Si un error está whitelisted (aceptado con justificación), el bot lo ignora.

### Scope del bot

El bot es **complemento**, no reemplazo de:

- **SAST** (Semgrep, CodeQL) — análisis estático.
- **WAF** (ModSecurity, Coraza) — seguridad web.
- **Linters** (SpotBugs, Checkstyle) — estilo.
- **Cobertura** (JaCoCo) — cobertura.

> Verde en el bot **no** significa "0 bugs en la app". Significa "0 bugs detectables en los logs analizados durante la ejecución de tests".

---

## 5. Convenciones

### Prefijo de tests para cleanup

Los UI tests usan prefijo `UITest-` para auto-limpiar datos entre ejecuciones vía `CatalogCleanupHelper`. Si añades un UI test nuevo:

1. Usa `UITest-` en los datos de prueba.
2. Hereda de `BaseUITest`.
3. Implementa `@BeforeEach setUp()` + `@AfterEach tearDown()` con `CatalogCleanupHelper`.

### Contract tests (EventBus)

Si añades un use case que notifica eventos:

1. Hereda de `BaseContractTest`.
2. Añade un `*ContractIT.java` con al menos un test por evento emitido.
3. Usa `Awaitility` con timeout de 2 segundos.
4. Verifica con `verifyNoMoreInteractions` cuando corresponda (`times(1)`).

### Aislamiento entre tests

- **ITs:** `@Transactional` (rollback automático por test).
- **Contract tests:** `@Transactional` heredado de `BaseContractTest`.
- **UI tests:** `CatalogCleanupHelper` (borrado explícito por prefijo + orden FK correcto).
- **Nunca** confíes en el orden de ejecución entre tests.

---

## 6. Troubleshooting

### Error: "Connection refused" al correr `mvn test`

Podman no está corriendo o el contenedor está parado:

```bash
podman ps
podman start wiredacademy-postgres
```

### Error: "database wiredacademy_test does not exist"

La BD de test se crea con la primera migración Flyway. Ejecutar:

```bash
podman exec -it wiredacademy-postgres psql -U wiredacademy_app -d postgres -c "CREATE DATABASE wiredacademy_test;"
mvn test -Dtest=GestionUsuariosIT
```

### Error: UI tests fallan con "Toolkit not initialized"

El FX toolkit no está headless. `BaseUITest` configura Monocle. En CI se requiere Xvfb.

### Error: "DNI already exists" en tests

Un test previo no limpió. `CatalogCleanupHelper.limpiarTodo()` en `@BeforeEach`/`@AfterEach` lo previene. Si persiste, ejecutar manualmente o cambiar el prefijo.

### El bot dice "archivo no existe" en `[1/4]`

Comportamiento conocido: `mvn test` puro (sin `-Pui`) escribe a `logs/` solo si `spring.profiles.active=dev` está activo, pero los IT activan `@ActiveProfiles("test")`. El bot tiene fallback a `logs/` vacío — significa "sin errores detectables en este run".

---

## 7. Cobertura

Actualmente **no hay integración de JaCoCo**. Planificado para fase futura.

Para añadir: `mvn jacoco:report` después de configurar el plugin en `pom.xml`.

---

## 8. Añadir un test nuevo

### Test unitario de aplicación

1. Crear `src/test/java/aplicacion/casosdeuso/.../MiUseCaseTest.java`.
2. `@BeforeEach` con `mock()` de repos + `LoggerPort` + `EventPublisher`.
3. Tests con `@Test` + `assertThrows` / `verify`.

### Test de integración

1. Crear `src/test/java/integration/aplicacion/.../MiUseCaseIT.java`.
2. Heredar de `BaseIntegrationTest`.
3. `@Autowired` los beans necesarios.
4. Usar prefijo único para datos (ej. `IT-`).

### Contract test

1. Crear `src/test/java/integration/eventos/casosdeuso/.../MiUseCaseContractIT.java`.
2. Heredar de `BaseContractTest`.
3. Patrón: `recorder.reset()` → mutación → `await().until(() -> recorder.contiene(EVENTO))`.

### UI test

1. Crear `src/test/java/presentacion/.../MiVistaUITest.java`.
2. Heredar de `BaseUITest`.
3. `start(Stage)` con `loadAndShow(stage, "...fxml")`.
4. `@BeforeEach setUp()` con `CatalogCleanupHelper`.
5. Verificar en tabla con `WaitForAsyncUtils.waitFor`.

---

## 9. Ver también

- **`ARCHITECTURE.md`** — Principios, capas y patrones arquitectónicos.
- **`EVENTBUS.md`** — Contrato B1 completo.
- **`DATABASE.md`** — Esquema y migraciones.
- **`LOGGING.md`** — Convención de logging + ADRs históricos.
- **`ADR/`** — ADRs nuevos (006+).

---

**WiredAcademy — Ecosistema Educativo Digital**

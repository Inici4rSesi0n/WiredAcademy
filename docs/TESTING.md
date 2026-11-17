# TESTING.md — Estrategia de testing de WiredAcademy

> Documento maestro de la estrategia de tests del proyecto.

**Versión:** 1.3  
**Estado:** Activo  
**Mantenedor:** inici4rsesi0n  
**Última revisión:** 2026-11-17

---

## 1. Categorías de tests

El proyecto usa **5 categorías** organizadas por alcance y propósito.

| Categoría | Path | Sufijo | Archivos | Ejecuta |
|---|---|---|---:|---|
| **Unitarios de dominio** | `src/test/java/dominio/` | `*Test.java` | ~25 | Instancias puras o mocks |
| **Unitarios de aplicación** | `src/test/java/aplicacion/` | `*Test.java` | ~25 | Mockito (repos + logger + publisher) |
| **Integración (IT)** | `src/test/java/integration/aplicacion/` | `*IT.java` | 22 | BD real (`wiredacademy_test`), rollback por test |
| **Contract tests (EventBus)** | `src/test/java/integration/eventos/` | `*ContractIT.java` | 12 archivos / 55+ tests | End-to-end: use case → EventBus → `TestEventRecorder` |
| **UI tests (TestFX)** | `src/test/java/presentacion/` | `*UITest.java` | 17 | TestFX + Monocle headless |

**Total:** **545 tests** en suite completa (528 sin UI + 17 UI).

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

```bash
./scripts/test.sh fast          # unitarios + integración (sin UI)
./scripts/test.sh ui            # Solo UI
./scripts/test.sh full          # Todo

mvn test -Dtest='*ContractIT'   # Contract tests
```

---

## 3. Política CI/CD

- **push** a cualquier rama → `fast-tests`.
- **pull_request** a `master` / `main` → `full-tests`.

**Servicios en CI:** PostgreSQL 16 efímero + Xvfb para UI.

---

## 4. Convenciones

- **UI tests:** Prefijo `UITest-` para cleanup con `CatalogCleanupHelper`.
- **Contract tests:** Heredar de `BaseContractTest` y usar `Awaitility`.
- **Aislamiento:** `@Transactional` para ITs, cleanup explícito para UI.
- **Idempotencia de `setUp`:** los métodos `@BeforeEach` deben verificar existencia antes de crear prerequisitos (grado, grupo, periodo, estudiante).

---

## 5. Seeder Headless (sin JavaFX)

Desde FASE 2, el DataSeeder puede ejecutarse **sin abrir la UI ni requerir `DISPLAY`**:

```bash
unset DISPLAY
mvn -q compile exec:java
```

### Cómo funciona

- **`SeedRunner`** (`src/main/java/boot/SeedRunner.java`) es una clase `main` que **no extiende `Application`** — no inicializa JavaFX.
- El **`exec-maven-plugin`** está configurado en `pom.xml` con `mainClass=boot.SeedRunner`.
- Antes de sembrar, invoca `BootstrapUseCase.inicializarCatalogos()` para garantizar catálogos base (grados, asignaturas, periodos) incluso si la BD está vacía.
- Todos los logs van vía `LoggerPort.audit()` con prefijos `[SEEDER]` y `[SESSION] seed_mode_started/completed` en `logs/wiredacademy-audit.log`.

### Por qué no `mvn javafx:run`

`javafx-maven-plugin` invoca `sun.launcher.LauncherHelper$FXHelper.main` **antes** del `main()` propio. Eso inicializa el toolkit GTK y requiere `DISPLAY`. Por eso el Seeder vive en una clase separada.

### Idempotente en los 3 modos (COLEGIO, INSTITUTO, UNIVERSIDAD)

- **COLEGIO:** recreos Lun-Vie + matrícula por grupo + asistencias.
- **INSTITUTO:** recreos Lun-Vie + matrícula flexible por asignatura.
- **UNIVERSIDAD:** sin recreos + matrícula flexible por asignatura.

---

## 6. Lecciones Aprendidas (Monocle)

- `Platform.startup()` directo requiere `glass.platform=Monocle` y `monocle.platform=Headless`.
- TestFX (`ApplicationTest`) solo requiere `testfx.headless=true`.
- `MonoclePropertiesTest` verifica que las properties estén configuradas.
- **Regla de ejecución:** `unset DISPLAY` antes de `mvn clean test` para evitar que el `DISPLAY=:0` del `.envrc` interfiera con Monocle.

---

## 7. Bugs Históricos Cerrados (2026-11-17)

Bugs del informe ejecutivo que han sido resueltos y blindados con tests:

| Bug | Test que lo blinda |
|---|---|
| `UnsupportedOperationException` en `MatriculaMapperImpl` | `GestionMatriculasIT` + `GestionMatriculasContractIT` + `SeccionMatriculaUITest` |
| Matrícula sin grupo ni inscripciones en `listarTodas()` | `MatriculaMapper` defensivo + `LEFT JOIN FETCH` completo |
| `Unable to open DISPLAY` en tests (CI headless) | `MonoclePropertiesTest` + `BaseContractTest` + `pom.xml` |
| `no Session` con proxies CGLIB en `UsuarioMapper` | `Hibernate.unproxy(entity)` + IT |
| Grado duplicado en `GruposUITest.setUp` | `setUp` idempotente + `CatalogCleanupHelper.limpiarGrados` reforzado |
| Periodo duplicado en `SeccionMatriculaUITest.crearPrereqs` | Idempotencia de `crearPrereqs` |
| `@EntityGraph` parcial sobreescribiendo `EAGER` | `@Query` con `LEFT JOIN FETCH` completo en `MatriculaJpaRepository` |

---

## 8. Ver también

- **`docs/ARCHITECTURE.md`** — Principios arquitectónicos.
- **`docs/EVENTBUS.md`** — Contrato B1 y contract tests.
- **`docs/DATABASE.md`** — Esquema y migraciones.
- **`docs/LOGGING.md`** — Namespaces `[SESSION]`, `[SEEDER]`, `[EVENT]`.

---

**WiredAcademy — Ecosistema Educativo Digital**

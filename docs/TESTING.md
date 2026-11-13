# TESTING.md — Estrategia de testing de WiredAcademy

> Documento maestro de la estrategia de tests del proyecto.

**Versión:** 1.2  
**Estado:** Activo  
**Mantenedor:** inici4rsesi0n

---

## 1. Categorías de tests

El proyecto usa **5 categorías** organizadas por alcance y propósito.

| Categoría | Path | Sufijo | Archivos | Ejecuta |
|---|---|---|---:|---|
| **Unitarios de dominio** | `src/test/java/dominio/` | `*Test.java` | ~25 | Instancias puras o mocks |
| **Unitarios de aplicación** | `src/test/java/aplicacion/` | `*Test.java` | ~25 | Mockito (repos + logger + publisher) |
| **Integración (IT)** | `src/test/java/integration/aplicacion/` | `*IT.java` | 22 | BD real (`wiredacademy_test`), rollback por test |
| **Contract tests (EventBus)** | `src/test/java/integration/eventos/` | `*ContractIT.java` | 12 archivos / 55+ tests | End-to-end: use case → EventBus → `TestEventRecorder` |
| **UI tests (TestFX)** | `src/test/java/presentacion/` | `*UITest.java` | 17+ | TestFX + Monocle headless |

**Total:** ~520+ tests en suite completa.

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

---

## 5. Lecciones Aprendidas (Monocle)

- `Platform.startup()` directo requiere `glass.platform=Monocle` y `monocle.platform=Headless`.
- TestFX (`ApplicationTest`) solo requiere `testfx.headless=true`.
- `MonoclePropertiesTest` verifica que las properties estén configuradas.

---

**WiredAcademy — Ecosistema Educativo Digital**

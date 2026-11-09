# EVENTBUS.md — Contrato de eventos de dominio

> Documento de referencia del sistema de eventos. Define el contrato B1 y su enforcement.

**Versión:** 1.0  
**Estado:** Activo  
**Última revisión:** 2026-11-08  
**Mantenedor:** inici4rsesi0n

---

## 1. Contrato B1 (regla de oro)

**Los use cases son la única fuente de eventos de dominio.**

| Capa | Puede `notificar` | Puede `suscribir` |
|---|:---:|:---:|
| Dominio | ❌ | ❌ |
| Aplicación (use cases) | ✅ | ❌ |
| Infraestructura (EventBus) | ✅ (implementa) | ✅ (implementa) |
| Presentación (controllers) | ❌ | ✅ |

**Enforcement:**

- **Código:** `grep -rn "eventBus.notificar" src/main/java/presentacion/` = 0 líneas.
- **Contract tests:** 51 tests blindan el contrato (ver `integration/eventos/`).
- **Bot:** valida en `analyze-logs.sh` (sección 1).

**Motivo:** sin esta regla, cada cambio en un controller debía recordarse manualmente de notificar. Con esta regla, el use case muta → notifica → todos los suscriptores se enteran. Un solo punto de cambio, cero olvidos.

---

## 2. Tipos de evento

Enum `TipoEvento` (`dominio/modelo/TipoEvento.java`):

```java
public enum TipoEvento {
    USUARIOS,
    GRUPOS,
    ASIGNATURAS,
    AULAS,
    GRADOS,
    PERIODOS,
    RECREOS,
    TURNOS,
    CLASES,
    MATRICULAS,
    DOCENTES,
    ESTUDIANTES,
    PADRES,
    COORDINADORES,
    ADMINISTRADORES,
    DIRECTORES,
    SECRETARIOS
}
```

Cada `TipoEvento` corresponde a una entidad de dominio. Algunos son **semilla** (nadie los consume aún, pero están listos para futuros suscriptores): `COORDINADORES`, `ADMINISTRADORES`, `DIRECTORES`, `SECRETARIOS`, `PADRES`.

---

## 3. Flujo end-to-end

```
┌─────────────┐  mutación  ┌──────────────────┐  notificar  ┌──────────┐
│ Controller  │───────────▶│  Use Case        │────────────▶│ EventBus │
│ (UI)        │            │ (GestionUsuarios)│             │ (Spring) │
└─────────────┘            └──────────────────┘             └────┬─────┘
       ▲                                                          │
       │                                                          │ Platform.runLater
       │                                                          ▼
       │                    ┌──────────────────────────┐  onEvent ┌──────────┐
       └────────────────────│ Otros Controllers        │◀─────────│ Listeners│
                            │ (suscritos al TipoEvento)│          │ (WeakRef)│
                            └──────────────────────────┘          └──────────┘
```

1. Controller invoca un caso de uso (`gestionUsuarios.agregarUsuario(cmd)`).
2. Use case valida, persiste y luego `publisher.notificar(TipoEvento.USUARIOS)`.
3. `EventBus` recibe la notificación y despacha a listeners en `Platform.runLater(...)`.
4. Controllers suscritos reciben `onEvent(USUARIOS)` y recargan su UI.

---

## 4. Los 12 use cases publicadores

| Use case | Eventos emitidos | Cuándo |
|---|---|---|
| `GestionAsignaturas` | `ASIGNATURAS` | crear / actualizar / eliminar |
| `GestionAulas` | `AULAS` | crear / actualizar / eliminar |
| `GestionClases` | `CLASES` | crear / actualizar / eliminar |
| `GestionGrados` | `GRADOS` | crear / actualizar / eliminar |
| `GestionPeriodos` | `PERIODOS` | crear / actualizar / eliminar |
| `GestionTurnos` | `TURNOS` | crear / actualizar / eliminar |
| `GestionRecreos` | `RECREOS` | crear / actualizar / eliminar / `aplicarA` / `removerDe` |
| `GestionGrupos` | `GRUPOS` + `DOCENTES` | crear / actualizar / eliminar + asignar / removerTutor |
| `GestionDocentes` | `DOCENTES` + `GRUPOS` | CRUD + agregar / removerAsignatura + asignarTutoria |
| `GestionCoordinadores` | `COORDINADORES` + `GRADOS` | `agregarGrado` / `removerGrado` |
| `GestionUsuarios` | `USUARIOS` + evento de rol | agregar / actualizar / eliminar (doble notificación) |
| `GestionMatriculas` | `MATRICULAS` | matricular / actualizar / actualizarEstado / eliminar |

**Excepciones** (por diseño, no notifican):

- `GestionHorario` — solo lectura.
- `AutenticarUsuario` — login (audit log, no evento).
- `BootstrapUseCase` — corre antes de la UI, no hay listeners aún.

---

## 5. Los 15 controllers consumidores

Suscritos a uno o más `TipoEvento`:

| Controller | Suscribe |
|---|---|
| `AsignacionCoordinadorController` | `USUARIOS`, `GRADOS` |
| `AsignacionDocenteController` | `DOCENTES`, `ASIGNATURAS` |
| `AsignacionTutorController` | `GRUPOS`, `DOCENTES` |
| `GestionHorariosController` | `CLASES`, `GRUPOS`, `DOCENTES`, `AULAS`, `PERIODOS`, `ASIGNATURAS` |
| `VisorHorarioController` | `CLASES`, `RECREOS` |
| `TurnosController` | `TURNOS` |
| `GradosController` | `GRADOS` |
| `PeriodosController` | `PERIODOS` |
| `AsignaturasController` | `ASIGNATURAS` |
| `RecreosController` | `RECREOS`, `PERIODOS` |
| `AulasController` | `AULAS` |
| `GruposController` | `GRUPOS`, `GRADOS` |
| `SeccionGestionUsuariosController` | `USUARIOS` |
| `DashboardController` | `ESTUDIANTES`, `DOCENTES`, `GRUPOS`, `ASIGNATURAS` |
| `SeccionMatriculaController` | `MATRICULAS`, `GRUPOS`, `ESTUDIANTES`, `PERIODOS` |

---

## 6. Contract tests

**Ubicación:** `src/test/java/integration/eventos/`.

**Infraestructura:**

- `TestEventRecorder` — listener de test que acumula eventos.
- `BaseContractTest` — base con `Platform.startup`, recorder y suscripción a todos los eventos.

**Cobertura:** 12 archivos, 51 tests totales. Patrón:

```java
@Test
void crearAula_emiteAULAS() {
    recorder.reset();
    gestionAulas.crearAula("A", 30, "Piso", "Tipo", PRESENCIAL);
    await().atMost(2, SECONDS).until(() -> recorder.contiene(TipoEvento.AULAS));
    assertTrue(recorder.contiene(TipoEvento.AULAS));
}
```

**Doble notificación validada específicamente en:**

- `asignarTutor` (`GRUPOS` + `DOCENTES`)
- `asignarTutoria` (`GRUPOS` + `DOCENTES`)
- `agregarGrado` / `removerGrado` (`COORDINADORES` + `GRADOS`)
- `agregarUsuario` / `actualizarUsuario` / `eliminarUsuario` (`USUARIOS` + evento de rol)

---

## 7. WeakReference y GC

El `EventBus` guarda listeners como `WeakReference<EventBusListener>`. Motivo: evitar memory leaks cuando un controller JavaFX se destruye (ej. cambio de vista) sin desuscribir explícito.

**Ciclo de vida:**

1. `initialize()` de controller → `eventBus.suscribir(evento, this)`.
2. Cuando el controller se GC'd → el `WeakReference` se limpia automáticamente.
3. `notificar()` limpia refs muertas con `removeIf(ref -> ref.get() == null)`.

**Warning en logs:** `SIN SUSCRIPTORES ACTIVOS (GC)` (nivel `WARN`) si un evento se publica pero todos los listeners fueron GC'd. Detectado por el bot en `analyze-logs.sh` (sección 2).

**Comportamiento esperado para eventos semilla** (`COORDINADORES`, `ADMINISTRADORES`, `DIRECTORES`, `SECRETARIOS`, `PADRES`):

- Lista nunca creada → `log.debug("[EVENT] ... SIN SUSCRIPTORES")` — no `WARN`.
- No contamina los logs del bot.

---

## 8. Añadir un nuevo evento

**Protocolo:**

1. Añadir el valor al enum `TipoEvento` en `dominio/modelo/TipoEvento.java`.
2. En el use case relevante: `publisher.notificar(TipoEvento.NUEVO)` tras la mutación.
3. Añadir contract test en `integration/eventos/casosdeuso/.../` (sección 6).
4. Suscribir controllers interesados: `eventBus.suscribir(TipoEvento.NUEVO, this)` en `initialize()`.
5. Añadir `case` al `onEvent` del controller (o `default -> {}` si se ignora).

**Checklist de PR:**

- [ ] Enum `TipoEvento` actualizado.
- [ ] Use case notifica.
- [ ] Contract test añadido (`times(1)` verificado).
- [ ] Controllers interesados suscritos.
- [ ] `mvn test -Dtest='*ContractIT'` verde.
- [ ] `grep -rn "eventBus.notificar" src/main/java/presentacion/` sigue dando 0.

---

## 9. Ver también

- **`ARCHITECTURE.md`** — Visión general arquitectónica.
- **`TESTING.md`** — Estrategia de tests (incluye contract tests).
- **`LOGGING.md`** — Convención de logging (`WiredAcademy.EVENTBUS` logger).
- **`ADR/`** — Decisiones arquitectónicas.

---

**WiredAcademy — Ecosistema Educativo Digital**

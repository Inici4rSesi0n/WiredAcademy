# EVENTBUS.md — Contrato de eventos de dominio

> Documento de referencia del sistema de eventos. Define el contrato B1 y su enforcement.

**Versión:** 1.2  
**Estado:** Activo  
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

---

## 2. Tipos de evento (Actualizado)

Enum `TipoEvento` (`dominio/modelo/TipoEvento.java`):

```java
public enum TipoEvento {
    USUARIOS, GRUPOS, ASIGNATURAS, AULAS, GRADOS, PERIODOS,
    RECREOS, TURNOS, CLASES, MATRICULAS,
    DOCENTES, ESTUDIANTES, PADRES, COORDINADORES, ADMINISTRADORES,
    DIRECTORES, SECRETARIOS, SUBDIRECTORES, TESOREROS, PSICOLOGOS,
    EVALUACIONES, CALIFICACIONES
}
```

Los eventos **semilla** (`SUBDIRECTORES`, `TESOREROS`, etc.) están listos para futuros suscriptores.

---

## 3. Flujo end-to-end

1. Controller invoca un caso de uso.
2. Use case valida, persiste y notifica (`publisher.notificar(...)`).
3. EventBus despacha a listeners en `Platform.runLater(...)`.
4. Controllers suscritos reciben `onEvent(...)` y actualizan la UI.

---

## 4. Use cases publicadores (Actualizado)

| Use case | Eventos emitidos | Cuándo |
|---|---|---|
| `GestionUsuarios` | `USUARIOS` + evento de rol | CRUD de cualquier usuario. |
| `GestionEvaluaciones` | `EVALUACIONES` | Crear o eliminar una evaluación (PC1, Examen). |
| `GestionCalificaciones` | `CALIFICACIONES` | Registrar o actualizar una nota. |
| `GestionMatriculas` | `MATRICULAS` | Matricular, actualizar o eliminar. |
| `GestionClases` | `CLASES` | Crear, actualizar o eliminar horarios. |
| ... | ... | (Los catálogos emiten sus propios eventos). |

**Excepciones (no notifican):** `GestionHorario` (solo lectura), `AutenticarUsuario` (login) y `BootstrapUseCase`.

---

## 5. Controllers consumidores (Actualizado)

| Controller | Suscribe |
|---|---|
| `CalificacionesController` | `CALIFICACIONES`, `EVALUACIONES` |
| `CursoDetalleController` | `CLASES`, `MATRICULAS` (para recalcular notas) |
| `DashboardController` | `ESTUDIANTES`, `DOCENTES`, `GRUPOS`, `ASIGNATURAS` |
| `VisorHorarioController` | `CLASES`, `RECREOS` |
| ... | ... |

---

## 6. Contract tests

- **Ubicación:** `src/test/java/integration/eventos/`.
- **Cobertura:** 12 archivos base, 51+ tests totales.

**Patrón:**

```java
@Test
void crearEvaluacion_emiteEVALUACIONES() {
    recorder.reset();
    gestionEvaluaciones.crearEvaluacion("PC1", 0.2, asignatura);
    await().atMost(2, SECONDS).until(() -> recorder.contiene(TipoEvento.EVALUACIONES));
    assertTrue(recorder.contiene(TipoEvento.EVALUACIONES));
}
```

---

## 7. WeakReference y GC

- El `EventBus` guarda listeners como `WeakReference<EventBusListener>`.
- Si un controller se destruye, el `WeakReference` se limpia.
- `notificar()` limpia refs muertas con `removeIf(ref -> ref.get() == null)`.
- **Warning en logs:** `SIN SUSCRIPTORES ACTIVOS (GC)`.

---

## 8. Añadir un nuevo evento (Protocolo)

1. Añadir el valor al enum `TipoEvento`.
2. En el use case: `publisher.notificar(TipoEvento.NUEVO)`.
3. Añadir contract test (`times(1)` verificado).
4. Suscribir controllers interesados: `eventBus.suscribir(TipoEvento.NUEVO, this)`.
5. `grep -rn "eventBus.notificar" src/main/java/presentacion/` debe dar **0**.# EVENTBUS.md — Contrato de eventos de dominio

> Documento de referencia del sistema de eventos. Define el contrato B1 y su enforcement.

**Versión:** 1.2  
**Estado:** Activo  
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

---

## 2. Tipos de evento (Actualizado)

Enum `TipoEvento` (`dominio/modelo/TipoEvento.java`):

```java
public enum TipoEvento {
    USUARIOS, GRUPOS, ASIGNATURAS, AULAS, GRADOS, PERIODOS,
    RECREOS, TURNOS, CLASES, MATRICULAS,
    DOCENTES, ESTUDIANTES, PADRES, COORDINADORES, ADMINISTRADORES,
    DIRECTORES, SECRETARIOS, SUBDIRECTORES, TESOREROS, PSICOLOGOS,
    EVALUACIONES, CALIFICACIONES
}
```

Los eventos **semilla** (`SUBDIRECTORES`, `TESOREROS`, etc.) están listos para futuros suscriptores.

---

## 3. Flujo end-to-end

1. Controller invoca un caso de uso.
2. Use case valida, persiste y notifica (`publisher.notificar(...)`).
3. EventBus despacha a listeners en `Platform.runLater(...)`.
4. Controllers suscritos reciben `onEvent(...)` y actualizan la UI.

---

## 4. Use cases publicadores (Actualizado)

| Use case | Eventos emitidos | Cuándo |
|---|---|---|
| `GestionUsuarios` | `USUARIOS` + evento de rol | CRUD de cualquier usuario. |
| `GestionEvaluaciones` | `EVALUACIONES` | Crear o eliminar una evaluación (PC1, Examen). |
| `GestionCalificaciones` | `CALIFICACIONES` | Registrar o actualizar una nota. |
| `GestionMatriculas` | `MATRICULAS` | Matricular, actualizar o eliminar. |
| `GestionClases` | `CLASES` | Crear, actualizar o eliminar horarios. |
| ... | ... | (Los catálogos emiten sus propios eventos). |

**Excepciones (no notifican):** `GestionHorario` (solo lectura), `AutenticarUsuario` (login) y `BootstrapUseCase`.

---

## 5. Controllers consumidores (Actualizado)

| Controller | Suscribe |
|---|---|
| `CalificacionesController` | `CALIFICACIONES`, `EVALUACIONES` |
| `CursoDetalleController` | `CLASES`, `MATRICULAS` (para recalcular notas) |
| `DashboardController` | `ESTUDIANTES`, `DOCENTES`, `GRUPOS`, `ASIGNATURAS` |
| `VisorHorarioController` | `CLASES`, `RECREOS` |
| ... | ... |

---

## 6. Contract tests

- **Ubicación:** `src/test/java/integration/eventos/`.
- **Cobertura:** 12 archivos base, 51+ tests totales.

**Patrón:**

```java
@Test
void crearEvaluacion_emiteEVALUACIONES() {
    recorder.reset();
    gestionEvaluaciones.crearEvaluacion("PC1", 0.2, asignatura);
    await().atMost(2, SECONDS).until(() -> recorder.contiene(TipoEvento.EVALUACIONES));
    assertTrue(recorder.contiene(TipoEvento.EVALUACIONES));
}
```

---

## 7. WeakReference y GC

- El `EventBus` guarda listeners como `WeakReference<EventBusListener>`.
- Si un controller se destruye, el `WeakReference` se limpia.
- `notificar()` limpia refs muertas con `removeIf(ref -> ref.get() == null)`.
- **Warning en logs:** `SIN SUSCRIPTORES ACTIVOS (GC)`.

---

## 8. Añadir un nuevo evento (Protocolo)

1. Añadir el valor al enum `TipoEvento`.
2. En el use case: `publisher.notificar(TipoEvento.NUEVO)`.
3. Añadir contract test (`times(1)` verificado).
4. Suscribir controllers interesados: `eventBus.suscribir(TipoEvento.NUEVO, this)`.
5. `grep -rn "eventBus.notificar" src/main/java/presentacion/` debe dar **0**.

---

**WiredAcademy — Ecosistema Educativo Digital**

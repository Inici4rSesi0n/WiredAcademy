# EVENTBUS.md — Contrato de eventos de dominio

> Documento de referencia del sistema de eventos. Define el contrato B1 y su enforcement.

**Versión:** 1.3  
**Estado:** Activo  
**Mantenedor:** inici4rsesi0n  
**Última revisión:** 2026-11-17

---

## 1. Contrato B1 (regla de oro)

**Los use cases son la única fuente de eventos de dominio.**

| Capa | Puede `notificar` | Puede `suscribir` |
|---|:---:|:---:|
| Dominio | ❌ | ❌ |
| Aplicación (use cases) | ✅ | ❌ |
| Infraestructura (EventBus) | ✅ (implementa) | ✅ (implementa) |
| Presentación (controllers) | ❌ | ✅ |

**Enforcement:** `grep -rn "eventBus.notificar" src/main/java/presentacion/` debe dar **0 resultados** en cada release.

---

## 2. Tipos de evento (Actualizado)

Enum `TipoEvento` (`dominio/modelo/TipoEvento.java`):

```java
public enum TipoEvento {
    USUARIOS, GRUPOS, ASIGNATURAS, AULAS, GRADOS, PERIODOS,
    RECREOS, TURNOS, CLASES, MATRICULAS,
    DOCENTES, ESTUDIANTES, PADRES, COORDINADORES, ADMINISTRADORES,
    DIRECTORES, SECRETARIOS, SUBDIRECTORES, TESOREROS, PSICOLOGOS,
    EVALUACIONES, CALIFICACIONES,
    PUBLICACIONES, ASISTENCIAS
}
```

**Total:** 24 eventos.

Los eventos **semilla** (`SUBDIRECTORES`, `TESOREROS`, `PSICOLOGOS`, etc.) están listos para futuros suscriptores sin coste.

`PUBLICACIONES` y `ASISTENCIAS` se añadieron en las fases LMS / FASE 2 para cubrir el CMS y el módulo de asistencia del Aula Virtual.

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
| `GestionUsuarios` | `USUARIOS` + evento de rol específico | CRUD de cualquier usuario. |
| `GestionDocentes` | `DOCENTES` (+ `GRUPOS` al asignar/remover tutoría) | CRUD de docentes + asignación de asignaturas. |
| `GestionEstudiantes` | `ESTUDIANTES` | CRUD de estudiantes. |
| `GestionPadres` | `PADRES` | CRUD y vinculación con estudiantes. |
| `GestionAsignaturas` | `ASIGNATURAS` | CRUD de asignaturas. |
| `GestionAulas` | `AULAS` | CRUD de aulas. |
| `GestionGrados` | `GRADOS` | CRUD de grados. |
| `GestionGrupos` | `GRUPOS` (+ `DOCENTES` al asignar tutor) | CRUD de grupos + asignación de tutor. |
| `GestionPeriodos` | `PERIODOS` | CRUD de periodos académicos. |
| `GestionRecreos` | `RECREOS` | CRUD de recreos. |
| `GestionTurnos` | `TURNOS` | CRUD de turnos. |
| `GestionClases` | `CLASES` | CRUD de horarios. |
| `GestionMatriculas` | `MATRICULAS` | Matricular, actualizar, eliminar. |
| `GestionEvaluaciones` | `EVALUACIONES` | Crear o eliminar una evaluación. |
| `GestionCalificaciones` | `CALIFICACIONES` | Registrar o actualizar una nota. |
| `GestionPublicaciones` | `PUBLICACIONES` | Crear o eliminar material, tarea, evaluación, foro o anuncio del CMS. |
| `GestionAsistencias` | `ASISTENCIAS` | Registrar asistencia (presente, ausente, tarde, justificado). |

**Excepciones (no notifican):** `GestionHorario` (solo lectura), `AutenticarUsuario` (login) y `BootstrapUseCase`.

---

## 5. Controllers consumidores (Actualizado)

| Controller | Suscribe |
|---|---|
| `DashboardController` | `ESTUDIANTES`, `DOCENTES`, `GRUPOS`, `ASIGNATURAS` |
| `CalificacionesController` | `CALIFICACIONES`, `EVALUACIONES` |
| `CursoDetalleController` | `CLASES`, `MATRICULAS` (para recalcular notas) |
| `AsistenciaController` | `ASISTENCIAS` |
| `PublicacionesPanelController` | `PUBLICACIONES` |
| `SubirMaterialController` | `PUBLICACIONES` |
| `VisorHorarioController` | `CLASES`, `RECREOS` |
| `AsignaturasController` | `ASIGNATURAS` |
| `AulasController` | `AULAS` |
| `GradosController` | `GRADOS` |
| `GruposController` | `GRUPOS`, `GRADOS` |
| `PeriodosController` | `PERIODOS` |
| `RecreosController` | `RECREOS`, `PERIODOS` |
| `TurnosController` | `TURNOS` |
| `SeccionGestionUsuariosController` | `USUARIOS` |
| `AsignacionDocenteController` | `DOCENTES`, `ASIGNATURAS` |
| `AsignacionTutorController` | `GRUPOS`, `DOCENTES` |
| `AsignacionCoordinadorController` | `USUARIOS`, `GRADOS` |
| `GestionHorariosController` | `CLASES`, `GRUPOS`, `DOCENTES`, `AULAS`, `PERIODOS`, `ASIGNATURAS` |
| `SeccionMatriculaController` | `MATRICULAS`, `GRUPOS`, `ESTUDIANTES`, `PERIODOS` |

> **Warning esperado (no bug):** `SIN SUSCRIPTORES` cuando se publica un evento sin que nadie esté suscrito (ej. dashboard no abierto aún). Se loggea como `DEBUG`, no como error.

---

## 6. Contract tests

- **Ubicación:** `src/test/java/integration/eventos/`.
- **Base:** `BaseContractTest` + `TestEventRecorder`.
- **Cobertura actual:** 12 archivos, 55+ tests.

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

> **Deuda conocida:** los use cases `GestionPublicaciones` y `GestionAsistencias` aún no tienen contract test. Sus eventos (`PUBLICACIONES`, `ASISTENCIAS`) están implementados y disparándose correctamente, pero falta blindaje end-to-end. Prioridad: medio (Fase 3).

---

## 7. WeakReference y GC

- El `EventBus` guarda listeners como `WeakReference<EventBusListener>`.
- Si un controller se destruye, el `WeakReference` se limpia.
- `notificar()` limpia refs muertas con `removeIf(ref -> ref.get() == null)`.

> **Warning en logs:** `SIN SUSCRIPTORES ACTIVOS (GC)` — solo cuando había listeners pero fueron recolectados. Es un aviso de posible bug de ciclo de vida, no un error.

---

## 8. Añadir un nuevo evento (Protocolo)

1. Añadir el valor al enum `TipoEvento`.
2. En el use case: `publisher.notificar(TipoEvento.NUEVO)`.
3. Añadir contract test (`times(1)` verificado).
4. Suscribir controllers interesados: `eventBus.suscribir(TipoEvento.NUEVO, this)`.
5. Ejecutar `grep -rn "eventBus.notificar" src/main/java/presentacion/` → debe dar **0**.
6. Actualizar este documento (§2 y §4).

---

**WiredAcademy — Ecosistema Educativo Digital**

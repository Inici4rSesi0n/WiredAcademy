# Módulo: Mis Cursos (Aula Virtual LMS)

## 1. Visión General

Hub central del usuario. Al seleccionar un curso, se abre el **Aula Virtual** con pestañas dinámicas que consumen el contenido creado en el CMS (Gestor de Publicaciones).

**Estado:** funcional (FASE 2 completada). Pestaña Asistencia operativa para Docente. Sistema de Entregas (EntregaTarea) pendiente — ver §6.

---

## 2. Control de Acceso (RBAC)

- `ESTUDIANTE` ✅
- `DOCENTE` ✅
- `PADRE` (acceso indirecto, asume la vista del hijo)

---

## 3. Modelo Compartido

**7 pestañas** en el Aula Virtual (`CursoDetalleController`):

| Pestaña | Visible para | Backend |
|---|---|---|
| Contenido | Estudiante + Docente | `PublicacionesPanelController` (tipo `MATERIAL`) |
| Tareas | Estudiante + Docente | `PublicacionesPanelController` (tipo `TAREA`) |
| Evaluaciones | Estudiante + Docente | `PublicacionesPanelController` (tipo `EVALUACION`) |
| Foros | Estudiante + Docente | `PublicacionesPanelController` (tipo `FORO`) |
| Anuncios | Estudiante + Docente | `PublicacionesPanelController` (tipo `ANUNCIO`) |
| Calificaciones | Estudiante + Docente | `CalificacionesController` (FASE LMS) |
| **Registrar Asistencia** | **Solo Docente** | **`AsistenciaController` (FASE 2)** |

La pestaña Asistencia se elimina automáticamente del `TabPane` cuando el rol no es `DOCENTE`.

**Navegación:**
- **Docente:** Mis Cursos → Asignatura → Secciones → Aula Virtual
- **Estudiante:** Mis Cursos → Asignatura → Aula Virtual (directo)

**Botón "Volver" dinámico:**
- Docente: `← Volver a Mis Secciones`
- Estudiante: `← Volver a Mis Cursos`

---

## 4. Perspectiva DOCENTE

### Cabecera
- Filtro de periodos dinámico (lee de `GestionPeriodos.listarTodos()`, filtra por estado `Activo`).

### Contenido
- Ve el acordeón por semanas.
- Puede saltar al CMS (`SubirMaterialController`) para crear nuevas publicaciones.

### Tareas / Evaluaciones
- Vista de tabla responsiva (pendiente de pulido: hoy usa el mismo panel genérico que el Estudiante, sin tabla de calificación masiva — ver §6 deuda).

### Foros
- Modera discusiones (solo lectura + respuesta).

### Registrar Asistencia (FASE 2)
- **Selector de fecha** con `DatePicker` (por defecto hoy).
- **Tabla** con los estudiantes del grupo (`grupo_estudiantes` del grupo actual).
- **Botones por fila:** `P` (Presente), `T` (Tarde), `A` (Ausente).
- Cada clic persiste vía `GestionAsistencias.registrarAsistencia(grupo, asignatura, estudiante, fecha, estado)`.
- Los cambios emiten `TipoEvento.ASISTENCIAS`.
- **Idempotente:** re-registrar el mismo estudiante/fecha actualiza el estado existente (UNIQUE constraint).

---

## 5. Perspectiva ESTUDIANTE

### Cabecera
- Ve el docente asignado y la modalidad de la sección (Presencial / Virtual / Remoto).

### Contenido
- Acordeón por semanas.
- Descarga de archivos adjuntos (link `url_archivo`).

### Tareas
- **Banner de días restantes** (implementado):
  - 🟢 **Cian (`#00D2FF`)**: quedan ≥ 1 día → "Quedan N días"
  - 🟠 **Naranja (`#F59E0B`)**: vence hoy → "Vence hoy"
  - 🔴 **Rojo (`#EF4444`)**: fecha pasada → "Atrasado por N días"
- **Badges de estado** (parcialmente implementado):
  - ✅ `Por entregar` (si `fecha_limite >= now`)
  - ✅ `Atrasado` (si `fecha_limite < now`)
  - ⏳ `Entregado` (pendiente — depende de FASE 4, `EntregaTarea`)
  - ⏳ `Calificado` (pendiente — depende de FASE 4 + `Calificacion`)
- ⏳ Botón "Subir archivo" (pendiente FASE 4).
- ⏳ Feedback del docente (pendiente FASE 4).

### Evaluaciones
- Mismos badges que Tareas, más la nota final vía `CalificacionesController`.
- Criterio de nota (última / más alta / promedio): implementado en `GestionCalificaciones.calcularPromedioPonderado`.

### Foros / Anuncios
- Solo lectura (por diseño).

### Calificaciones
- Tabla de evaluaciones con peso (%) y nota.
- Promedio ponderado calculado (`GestionCalificaciones.calcularPromedioPonderado`).

---

## 6. Deuda Técnica Conocida

| Item | Impacto | Fase objetivo |
|---|---|---|
| Sistema de Entregas (`EntregaTarea`) | Estudiante no puede subir archivos; badge `Entregado`/`Calificado` no se activa | FASE 4 |
| Tabla responsiva de Docente (Tareas/Evaluaciones) | Hoy usa el mismo panel genérico que el Estudiante | FASE 4 |
| Feedback del docente al estudiante | No implementado | FASE 4 |
| Registro de asistencia sin auto-save | El docente debe pulsar P/T/A por cada estudiante (no hay "guardar todos") | FASE 3 (opcional) |

---

## 7. Eventos del Bus relacionados

| Evento | Emisor | Consumidores |
|---|---|---|
| `PUBLICACIONES` | `GestionPublicaciones` | `PublicacionesPanelController`, `SubirMaterialController` |
| `CALIFICACIONES`, `EVALUACIONES` | `GestionCalificaciones`, `GestionEvaluaciones` | `CalificacionesController` |
| `CLASES`, `MATRICULAS` | `GestionClases`, `GestionMatriculas` | `CursoDetalleController` (recalcular) |
| `ASISTENCIAS` (nuevo FASE 2) | `GestionAsistencias` | `AsistenciaController` |

---

**WiredAcademy — Ecosistema Educativo Digital**

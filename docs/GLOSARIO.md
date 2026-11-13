# Glosario de Términos del Dominio (WiredAcademy)

Este documento define el lenguaje ubicuo utilizado en el sistema, alineando los conceptos de negocio con las entidades del código fuente.

---

## 1. Estructura Institucional y Académica

| Término | Definición |
| :--- | :--- |
| **Usuario** | Entidad abstracta base para todos los actores. Tiene 10 roles jerárquicos. |
| **TIPO_INSTITUCION** | Configuración global (COLEGIO, INSTITUTO, UNIVERSIDAD) que altera la lógica de matrícula y catálogos. |
| **PeriodoAcademico** | Bloque de tiempo (ej. "2026-I") que agrupa matrículas, clases y calendarios. |
| **Grado** | Nivel educativo (ej. "1er Grado" en Colegio, "Ciclo I" en Universidad). |
| **Grupo / Sección** | Subdivisión de un Grado. Es la unidad operativa que agrupa estudiantes y un tutor. **Define la Modalidad** (Presencial, Virtual, Remoto). |
| **Aula** | Espacio físico (ej. "Aula 101", "Laboratorio"). No tiene modalidad, es un recurso de infraestructura. |
| **Turno** | Bloque horario general (Mañana, Tarde, Noche). |
| **FranjaHoraria** | Rango de tiempo específico (ej. Lunes de 08:00 a 10:00). |
| **Clase** | Evento de enseñanza que une Asignatura, Docente, Grupo y (opcionalmente) Aula en una FranjaHoraria. |

---

## 2. Procesos Operativos

| Término | Definición |
| :--- | :--- |
| **Matrícula** | Vinculación formal del Estudiante con un Periodo y un Grupo/Sección. |
| **Recreo** | Evento de descanso (aplica principalmente a Colegios e Institutos). |
| **Pago** | Transacción financiera (Pensión, Matrícula) registrada por el Tesorero o realizada por el Estudiante. |

---

## 3. Ecosistema LMS (Aula Virtual)

| Término | Definición |
| :--- | :--- |
| **Semana** | Unidad de tiempo dentro del curso (ej. "Semana 01"). Creada por coordinación. |
| **Sesión / Contenido** | Material de estudio (PDF, Video) organizado dentro de una Semana. |
| **Tarea** | Actividad que el Estudiante debe entregar (subir archivo) con fecha límite e intentos. |
| **Evaluación** | Prueba (PC1, Examen) con reglas de negocio (intentos, criterios de nota). |
| **Foro** | Espacio de discusión asíncrona entre docente y alumnos. |
| **Anuncio** | Comunicado publicado por el docente o la administración. Se muestra en el curso o en el Dashboard global. |

---

## 4. Arquitectura y Calidad

| Término | Definición |
| :--- | :--- |
| **Contrato B1** | Regla de oro que dicta que los Casos de Uso son la única fuente de eventos de dominio (EventBus). |
| **Publicación Centralizada** | Patrón de diseño donde el Docente crea el contenido (Tareas, Foros, etc.) en un solo módulo (CMS) y se distribuye a las vistas de los estudiantes. |

---

**WiredAcademy — Ecosistema Educativo Digital**

# Módulo: Gestión Académica

---

## 1. Visión General

Centraliza la administración de la estructura educativa. Permite gestionar catálogos, asignaciones y horarios.

---

## 2. Control de Acceso (RBAC)

- **Roles con visibilidad:** `ADMINISTRADOR`, `DIRECTOR`, `COORDINADOR`.
- **Enum asociado:** `SeccionMenu.GESTION_ACADEMICA`.

---

## 3. Requisitos Funcionales (RF)

- **RF-AC-01:** CRUD de Asignaturas, Aulas, Grados, Grupos, Periodos, Recreos y Turnos.
- **RF-AC-02:** Asignación de docentes a materias y tutores a grupos.
- **RF-AC-03:** Visor de horarios en GridPane adaptativo (Filtros por Grupo, Docente, Aula).
- **RF-AC-04:** Exportación de horarios a PDF y PNG.

---

## 4. Entidades y Eventos

- **Entidades:** `Asignatura`, `Aula`, `Grado`, `Grupo`, `PeriodoAcademico`, `Recreo`, `Turno`, `Clase`.
- **Eventos:** `ASIGNATURAS`, `AULAS`, `GRADOS`, `GRUPOS`, `PERIODOS`, `RECREOS`, `TURNOS`, `CLASES`, `DOCENTES`.

# Módulo: Mis Cursos

---

## 1. Visión General

Es el catálogo personal de asignaturas. Proporciona al usuario un acceso rápido a los cursos en los que participa y funciona como el **consumidor principal del material académico** subido en el sistema.

---

## 2. Control de Acceso (RBAC)

- **Roles con visibilidad:** `ESTUDIANTE`, `DOCENTE`.
- **Acceso de Padre:** Indirecto (asume la vista del estudiante seleccionado).
- **Enum asociado:** `SeccionMenu.MIS_CURSOS`.

---

## 3. Requisitos Funcionales (RF)

- **RF-MC-01:** Listar las asignaturas del estudiante (matriculadas) o del docente (asignadas).
- **RF-MC-02:** Mostrar un resumen del curso (Nombre, Docente, Aula).
- **RF-MC-03 (Visualización de Material):** Al entrar al detalle de un curso, mostrar una lista de los recursos (Sílabos, Prácticas, Lecturas) que el docente o coordinador haya subido específicamente para esa asignatura.
- **RF-MC-04 (Descarga):** Permitir al estudiante descargar estos recursos.
- **RF-MC-05:** El docente verá en esta vista el material que él mismo ha publicado para su curso, sirviéndole como confirmación visual.

---

## 4. Flujo de Trabajo (Workflow)

1. El usuario entra a "Mis Cursos".
2. Selecciona una tarjeta (ej. "Matemática").
3. El sistema despliega el detalle del curso, mostrando en una sección inferior la lista de materiales académicos vinculados a esta asignatura.

# Módulo: Calificaciones

---

## 1. Visión General

Es el registro académico cuantitativo. Se divide en dos vistas principales: la vista del Docente (ingreso de notas) y la vista del Estudiante.

---

## 2. Control de Acceso (RBAC)

- **Roles con visibilidad:** `ESTUDIANTE`, `DOCENTE`.
- **Acceso de Padre:** Indirecto (ve las notas de su hijo seleccionado).
- **Restricción:** ADMINISTRADOR, DIRECTOR y COORDINADOR no acceden a esta vista operativa. Consultan promedios generales en "Reportes".
- **Enum asociado:** `SeccionMenu.CALIFICACIONES`.

---

## 3. Requisitos Funcionales (RF)

- **RF-CA-01 (Estructura - Vista Docente):** El docente visualiza la "Plantilla de Evaluación" creada por Coordinación (ej. PC1 20%, PC2 20%, Examen Final 30%).
- **RF-CA-02 (Registro - Vista Docente):** El docente ingresa la nota (0-20) de cada estudiante para cada evaluación.
- **RF-CA-03 (Consolidado - Vista Estudiante):** El estudiante ve la tabla de notas (peso, nota obtenida y estado "En proceso" si falta alguna).
- **RF-CA-04:** Cálculo automático del Promedio Ponderado (PP) en tiempo real.
- **RF-CA-05:** Bloqueo de edición si el periodo está "Culminado".

---

## 4. Flujo de Trabajo (Workflow)

1. **Coordinador** define la plantilla en el módulo de Gestión Académica/Subir Material.
2. **Docente** entra a Calificaciones, selecciona el curso y ve la tabla.
3. **Docente** asigna el instrumento (examen/tarea) a cada Slot (ej. PC1) y llena las notas.
4. **Estudiante** entra a Calificaciones y visualiza su progreso con el cálculo del PP.

---

## 5. Entidades Involucradas (A Desarrollar)

- `EstructuraEvaluacion` (definida por Coordinador: nombre y peso).
- `Calificacion` (nota asignada por Docente a Estudiante).
- `Estudiante`, `Asignatura`.

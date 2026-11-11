# Módulo: Portal de Matrícula (Front-Office - Autogestión)

---

## 1. Visión General

Es el asistente de matrícula autogestionado por el Estudiante. Su objetivo es guiar al alumno para que, basándose en su progreso académico, seleccione su grupo y horario para el próximo periodo.

---

## 2. Control de Acceso (RBAC)

- **Roles con visibilidad:** `ESTUDIANTE`.
- **Acceso de Padre:** Indirecto (matricula al hijo seleccionado).

---

## 3. Reglas de Negocio Curriculares (Progresión)

El sistema **no permite** al estudiante elegir libremente su grado o ciclo. Este se calcula automáticamente:

- **Colegios:** El sistema verifica que aprobó el grado anterior y le asigna el siguiente (ej. de 4to a 5to de Secundaria).
- **Universidades:** El sistema valida los créditos y prerrequisitos aprobados. Si el alumno debe llevar un curso de 2do ciclo y tres de 3er ciclo, solo se le muestran esas materias.
- **Institutos:** Se valida el avance por Módulos.

---

## 4. Requisitos Funcionales (RF)

- **RF-PM-01 (Validación de Deudas):** El sistema bloquea el portal si el estudiante tiene deudas vencidas en el módulo de Pagos.
- **RF-PM-02 (Malla Curricular):** Mostrar las asignaturas correspondientes al grado/ciclo calculado.
- **RF-PM-03 (Selección de Horario/Grupo):** El estudiante **sí elige** entre las secciones disponibles (Grupo A, Grupo B, Turno Mañana/Tarde) para las asignaturas que le tocan.
- **RF-PM-04 (Confirmación):** Generar la matrícula (`EstadoMatricula.ACTIVA`) y emitir la constancia.

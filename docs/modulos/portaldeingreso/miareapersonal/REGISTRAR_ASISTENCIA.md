# Módulo: Registrar Asistencia

---

## 1. Visión General

Herramienta para que los docentes tomen asistencia diaria a sus estudiantes durante una clase.

---

## 2. Control de Acceso (RBAC)

- **Roles con visibilidad:** Exclusivo para `DOCENTE`.
- **Enum asociado:** `SeccionMenu.REGISTRAR_ASISTENCIA`.

---

## 3. Requisitos Funcionales (RF)

- **RF-RA-01:** Seleccionar el Grupo/Asignatura y la fecha de la clase.
- **RF-RA-02:** Mostrar la lista de estudiantes matriculados en ese grupo.
- **RF-RA-03:** Permitir marcar estados: PRESENTE, AUSENTE, TARDE, JUSTIFICADO.
- **RF-RA-04:** Guardar el registro histórico. Evitar duplicados (una sola asistencia por estudiante por clase/fecha).
- **RF-RA-05:** Cálculo de porcentaje de asistencia por estudiante.

---

## 4. Flujo de Trabajo (Workflow)

1. El docente ingresa a "Registrar Asistencia".
2. Selecciona la Asignatura y el Grupo.
3. El sistema carga la lista de estudiantes.
4. El docente marca a los presentes (por defecto todos inician como PRESENTE).
5. Presiona "Guardar Asistencia".

---

## 5. Entidades Involucradas (A Desarrollar)

- `Asistencia` (nueva entidad), `Clase`, `Estudiante`.

# REGLAS_DE_NEGOCIO.md — Reglas de Negocio Transversales (WiredAcademy)

---

## 1. Integridad Referencial y Borrado

- No se puede eliminar un `Usuario` si tiene `Matriculas` activas, `Clases` asignadas o `Pagos` registrados.
- No se puede eliminar un `Grupo` si tiene `Estudiantes` matriculados.
- No se puede eliminar una `Asignatura` si está vinculada a un `Docente` o `Grupo`.

---

## 2. Periodos Académicos

- Un `PeriodoAcademico` con estado "Culminado" bloquea la edición de `Calificaciones`, `Asistencias` y `Matrículas`.
- Solo puede existir un periodo con estado "Activo" a la vez.

---

## 3. Horarios y Modalidad (Nuevo)

- **Unicidad de Docente:** Un Docente no puede tener dos clases solapadas en el tiempo, independientemente de la modalidad.
- **Unicidad de Sección:** Una Sección (Grupo) no puede tener dos clases solapadas.
- **Unicidad de Aula:** Un Aula física no puede albergar dos clases simultáneas. (Las clases en modalidad VIRTUAL/REMOTO no consumen Aula física).
- **Detección de Solapamiento:** El sistema bloquea el guardado si `(NuevaHoraInicio < HoraFinExistente) AND (NuevaHoraFin > HoraInicioExistente)`.

---

## 4. Evaluaciones y Tareas (LMS)

- Una `Evaluacion` tiene un peso (0.01 a 1.0). La suma de pesos de un curso debería ser 1.0 (el sistema lo advierte, pero no lo bloquea).
- Si un estudiante entrega una `Tarea` después de la fecha límite, el estado cambia a "Atrasado", pero el archivo se guarda.
- La nota final de una evaluación se calcula según el criterio definido por el docente (Último intento, Nota más alta, Promedio).

---

## 5. Seguridad y Jerarquía

- Un Administrador no puede eliminarse a sí mismo si es el único en el sistema.
- Las contraseñas siempre viajan encriptadas con BCrypt.
- El DNI y el Código de Usuario son inmutables una vez creados.

---

**WiredAcademy — Ecosistema Educativo Digital**

# Reglas de Negocio Transversales (WiredAcademy)

Estas reglas aplican a múltiples módulos del sistema y deben ser respetadas en la capa de Dominio o Aplicación.

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

## 3. Seguridad y Jerarquía

- Un Administrador no puede eliminarse a sí mismo si es el único en el sistema.
- Las contraseñas siempre viajan encriptadas con BCrypt (Fase de Dominio).
- El DNI y el Código de Usuario son inmutables una vez creados (solo se pueden modificar datos personales).

---

## 4. Flujos Financieros

- Un alumno con deuda mayor a 2 cuotas vencidas no puede matricularse en el siguiente periodo.

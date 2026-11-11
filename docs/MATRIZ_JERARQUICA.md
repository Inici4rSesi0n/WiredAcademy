# Jerarquía y Roles del Sistema

WiredAcademy maneja un modelo de **1 Usuario = 1 Rol**. Si una persona necesita dos roles (ej. Director y Docente), se recomienda crear cuentas separadas o asignar el rol de mayor jerarquía, según la política de la institución.

---

## 1. Niveles de Jerarquía

### Nivel 1: Estratégico (Visión Global)

- **ADMINISTRADOR:** Único rol con acceso a la configuración del sistema (Tipo de Institución, Base de datos). Gestiona usuarios y respaldos.
- **DIRECTOR:** Máxima autoridad académica. Ve todos los reportes financieros y académicos. No puede alterar la configuración del sistema.
- **SUBDIRECTOR:** Liderazgo operativo. Supervisa a coordinadores, gestiona la disciplina institucional. Tiene los mismos permisos de visualización que el Director, pero sin acceso a configuración.

### Nivel 2: Táctico (Gestión y Operaciones)

- **COORDINADOR ACADÉMICO:** Gestiona horarios, asignaciones, evalúa el desempeño docente y aprueba el plan de evaluaciones.
- **SECRETARIO (Admisión/Registro):** Responsable de matrículas (Back-Office) y trámites documentarios.
- **TESORERO (Finanzas):** Responsable del módulo de pagos (Back-Office), recaudación en caja y reportes de morosidad.
- **PSICOLOGO (Bienestar):** Acceso exclusivo al expediente socioemocional de los estudiantes. Confidencialidad absoluta. No ve notas ni finanzas.

### Nivel 3: Operativo (Aula)

- **DOCENTE:** Gestiona sus asignaturas, registra asistencia, ingresa calificaciones y sube material didáctico.

### Nivel 4: Usuario Final (Autogestión)

- **ESTUDIANTE:** Accede a sus portales de matrícula, pagos, trámites, mis cursos, mi horario y calificaciones.
- **PADRE:** Acceso indirecto. Selecciona a un hijo y asume su vista (excepto escritura crítica).

---

## 2. Restricciones Clave

- **PSICOLOGO:** Sus registros deben ser cifrados. No emite eventos de dominio (`EventBus`) para no filtrar datos sensibles en los logs generales.
- **TESORERO:** No puede modificar matrículas ni notas, solo estados financieros.
- **SUBDIRECTOR:** Puede vetar decisiones académicas del Coordinador en casos disciplinarios.

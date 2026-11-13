# Jerarquía y Roles del Sistema

WiredAcademy maneja un modelo de **1 Usuario = 1 Rol**.

---

## Niveles de Jerarquía

### Nivel 1: Estratégico

- **ADMINISTRADOR:** Único rol con acceso a la configuración del sistema. Gestiona usuarios y respaldos.
- **DIRECTOR:** Máxima autoridad académica. Ve todos los reportes financieros y académicos.
- **SUBDIRECTOR:** Liderazgo operativo. Supervisa a coordinadores. Puede vetar decisiones del Coordinador en casos disciplinarios.

### Nivel 2: Táctico

- **COORDINADOR ACADÉMICO:** Gestiona horarios, asignaciones y define el calendario de semanas (LMS).
- **SECRETARIO (Admisión/Registro):** Responsable de matrículas (Back-Office) y trámites documentarios.
- **TESORERO (Finanzas):** Responsable del módulo de pagos (Back-Office) y reportes de morosidad.
- **PSICOLOGO (Bienestar):** Acceso exclusivo al expediente socioemocional. Confidencialidad absoluta. No ve notas ni finanzas.

### Nivel 3: Operativo

- **DOCENTE:** Gestiona sus asignaturas, registra asistencia, ingresa calificaciones y publica contenido en el LMS.

### Nivel 4: Usuario Final (Autogestión)

- **ESTUDIANTE:** Accede a sus portales, mis cursos, tareas y evaluaciones.
- **PADRE:** Acceso indirecto. Selecciona a un hijo y asume su vista.

---

## Restricciones Clave

- **PSICOLOGO:** Sus registros deben ser cifrados. No emite eventos de dominio (`EventBus`) para no filtrar datos sensibles en los logs generales.
- **TESORERO:** No puede modificar matrículas ni notas, solo estados financieros.
- **DOCENTE:** No puede modificar las semanas del calendario (eso es competencia del Coordinador).

---

**WiredAcademy — Ecosistema Educativo Digital**

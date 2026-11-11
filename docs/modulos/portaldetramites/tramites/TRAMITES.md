# (Front-Office - Autogestión del Estudiante)

# Módulo: Trámites

---

## 1. Visión General

Es un centro de solicitudes en línea que permite a los estudiantes realizar gestiones académicas y administrativas sin acudir presencialmente a la institución.

---

## 2. Control de Acceso (RBAC)

- **Roles con visibilidad:** `ESTUDIANTE`, `DOCENTE`, `DIRECTOR`, `SECRETARIO`, `COORDINADOR`, `ADMINISTRADOR`.
- **Acceso de Padre:** Indirecto (gestiona trámites del hijo seleccionado).
- **Enum asociado:** `SeccionMenu.TRAMITES`.

---

## 3. Tipos de Trámites (Lógica de Negocio)

- **Autogestionables (Generación Automática):** Documentos que el sistema genera en PDF al instante sin revisión (ej. Constancia de Matrícula, Certificado de Estudios, Constancia de Notas).
- **Becas y Convenios (Evaluación):** Solicitudes de descuento (Socioeconómica, Convenios corporativos) que requieren aprobación del área de bienestar.
- **Solicitudes SAE (Tickets):** Peticiones personalizadas (ej. Rectificación de nota, Retiro de curso) que requieren que el usuario adjunte evidencia (máx. 5MB) y que pasan por un flujo de revisión.

---

## 4. Requisitos Funcionales (RF)

- **RF-TR-01:** Buscador de trámites por nombre.
- **RF-TR-02:** Visualización de tarjetas por categoría (Autogestionables, Becas, SAE).
- **RF-TR-03:** Formulario de solicitud con carga de archivos y casilla de aceptación de términos.
- **RF-TR-04:** Panel de "Estado de Trámites" con seguimiento visual (Pendiente, En Proceso, Procede, Rechazado).
- **RF-TR-05:** Historial de trámites finalizados.

---

## 5. Entidades Involucradas (A Desarrollar)

- `SolicitudTramite` (tipo, estado, fecha, archivo_evidencia, estudiante_id), `TipoTramite`.

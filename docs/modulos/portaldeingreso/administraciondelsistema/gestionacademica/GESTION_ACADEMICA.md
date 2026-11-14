# Módulo: Gestión Académica
## 1. Visión General
Administra la estructura educativa y el calendario.
## 2. Control de Acceso
- `ADMINISTRADOR`, `DIRECTOR`, `SUBDIRECTOR`, `COORDINADOR`.
## 3. Requisitos Funcionales
- **Catálogos:** CRUD de Aulas (espacios físicos), Grados, Grupos, Periodos, Recreos, Turnos.
- **Grupos — Modalidad:** El formulario de Grupos expone un `ComboBox` con `PRESENCIAL | VIRTUAL | REMOTO`. La tabla muestra la modalidad como chip de color: PRESENCIAL (cian `#00D2FF`), VIRTUAL (morado `#8B5CF6`), REMOTO (naranja `#F59E0B`). La modalidad vive en `Grupo` (migración V6) y se persiste en `grupos.modalidad`.
- **Calendario:** El Coordinador define las Semanas del periodo.
- **Asignaciones:** Docentes a asignaturas y tutores a grupos.
- **Horarios:** Visor GridPane con validación de solapamiento.

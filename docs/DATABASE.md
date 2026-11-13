# DATABASE.md — Base de datos de WiredAcademy

> Esquema, migraciones y convenciones de la capa de persistencia.

**Versión:** 1.2  
**Estado:** Activo  
**Mantenedor:** inici4rsesi0n

---

## 1. Motor

- **PostgreSQL 16** en Podman (contenedor `wiredacademy-postgres`, puerto `5433→5432`).
- **Volumen persistente:** `wiredacademy-pgdata`.
- **Autenticación:** `scram-sha-256`.

---

## 2. Migraciones (Flyway)

- **Fuente de verdad:** `src/main/resources/db/migration/`.
- **Hibernate `ddl-auto=validate`:** Hibernate valida contra el esquema existente.

### Migraciones actuales

| Versión | Archivo | Propósito |
|---|---|---|
| V1 | `V1__init_schema.sql` | Esquema inicial (Usuarios, Catálogos, Clases, Matrículas) |
| V2 | `V2__widen_dni_column.sql` | `usuarios.dni` VARCHAR(8) → VARCHAR(20) |
| V3 | `V3__add_configuracion_sistema.sql` | Tabla `configuracion_sistema` (TIPO_INSTITUCION) |
| V4 | `V4__matricula_asignatura_grupo.sql` | `matricula_asignaturas` gana ID y `grupo_id`. |
| V5 | `V5__calificaciones.sql` | Tablas `evaluaciones` y `calificaciones`. |
| V6 | `V6__modalidad_en_grupo.sql` | Mueve `modalidad` de `aulas` a `grupos`. |

---

## 3. Esquema

### 3.1. Configuración del Sistema

- `configuracion_sistema` (id, `tipo_institucion` [COLEGIO, INSTITUTO, UNIVERSIDAD]).

### 3.2. Tabla `usuarios` (SINGLE_TABLE)

Discriminator: `rol` (10 roles: ADMINISTRADOR, DIRECTOR, SUBDIRECTOR, COORDINADOR, SECRETARIO, TESORERO, PSICOLOGO, DOCENTE, ESTUDIANTE, PADRE).

- **Columnas:** id, codigo, hash_contrasena, dni, nombre, apellido, edad, rol, especialidad, grupo_tutoria_id.

### 3.3. Catálogos y Estructura Académica

- `turnos` (nombre).
- `grados` (nombre, nivel).
- `periodos_academicos` (nombre, fechas, estado).
- `aulas` (nombre, capacidad, ubicacion, tipo). **Nota:** Ya no tiene modalidad, es un espacio físico.
- `grupos` (nombre, grado_id, tutor_id, **modalidad**). La modalidad ahora vive aquí (PRESENCIAL, VIRTUAL, REMOTO).
- `asignaturas` (nombre).

### 3.4. Eventos y Horarios

- `clases` (periodo_id, asignatura_id, docente_id, grupo_id, aula_id, dia, horas).
- `recreos` + `recreo_periodos`.

### 3.5. Matrículas

- `matriculas` (estudiante_id, periodo_id, grupo_id, fecha, estado).
- `matricula_asignaturas` (id, matricula_id, asignatura_id, **grupo_id**). Permite que un alumno esté en diferentes grupos por asignatura.

### 3.6. Evaluaciones y Calificaciones (LMS)

- `evaluaciones` (id, nombre, peso, asignatura_id).
- `calificaciones` (id, evaluacion_id, estudiante_id, nota).

### 3.7. Sesiones

- `sesiones` (UUID PK, usuario_id, fechas). Preparada para futura persistencia.

---

## 4. Convenciones

- Tablas en **plural** (`usuarios`, `grupos`), columnas en **snake_case**.
- FKs `<tabla_singular>_id`.
- Índices case-insensitive `uq_*_lower` sobre `LOWER(nombre)` para evitar duplicados por mayúsculas.
- `ON DELETE CASCADE` en tablas M:N.
- Checks: `edad > 0`, `hora_inicio < hora_fin`, `peso > 0 AND peso <= 1`.

---

## 5. Diagnóstico y Backups

- Script `scripts/dev/db_diag.sh` genera un reporte completo (índices, FKs, huérfanos, drift).
- Backups manuales con `pg_dump`. El volumen persiste entre reinicios de Podman.

---

## 6. Deuda técnica conocida

| Deuda | Impacto |
|---|---|
| Sesiones in-memory (`SesionUsuario`) | No persisten entre reinicios. |
| Doble FK tutoría (`grupos.tutor_id` + `usuarios.grupo_tutoria_id`) | Sincronización manual. |
| Fechas/horas como String | Validación frágil. |

---

**WiredAcademy — Ecosistema Educativo Digital**

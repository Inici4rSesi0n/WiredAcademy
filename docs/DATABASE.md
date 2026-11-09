# DATABASE.md — Base de datos de WiredAcademy

> Esquema, migraciones y convenciones de la capa de persistencia.

**Versión:** 1.0  
**Estado:** Activo  
**Última revisión:** 2026-11-08  
**Mantenedor:** inici4rsesi0n

---

## 1. Motor

- **PostgreSQL 16** en Podman (contenedor `wiredacademy-postgres`, puerto `5433→5432`).
- **Volumen persistente:** `wiredacademy-pgdata` (sobrevive a `podman rm`).
- **Autenticación:** `scram-sha-256` en todas las conexiones (`--auth-host=scram-sha-256 --auth-local=scram-sha-256`).

**Motivación:** SQLite o archivos locales (`.bin` cifrados, sistema anterior) no soportan concurrencia real ni integridad referencial. PostgreSQL garantiza ACID, permite migraciones versionadas vía Flyway y prepara el terreno para RLS (Row-Level Security) cuando el proyecto escale a LAN.

---

## 2. Migraciones (Flyway)

- **Fuente de verdad:** `src/main/resources/db/migration/`.
- **Hibernate `ddl-auto=validate`:** Hibernate valida contra el esquema existente, no crea ni modifica tablas.
- **`baseline-on-migrate=true`:** primera migración se aplica automáticamente.

**Regla de oro:** Flyway es la única fuente de verdad del esquema. Nunca ejecutes `DROP`, `ALTER` o `CREATE` manualmente contra la BD. Toda modificación va por migración.

### Migraciones actuales

| Versión | Archivo | Propósito |
|---|---|---|
| V1 | `V1__init_schema.sql` | Esquema completo inicial (18 tablas + índices + constraints) |
| V2 | `V2__widen_dni_column.sql` | `usuarios.dni` VARCHAR(8) → VARCHAR(20) |

### Cómo añadir una migración

1. Crear `V<N>__<descripcion_snake_case>.sql` en `src/main/resources/db/migration/`.
2. **Nunca modificar migraciones ya aplicadas** (rompe el checksum de Flyway → `Migration checksum mismatch`).
3. Probar en local: la próxima ejecución de `mvn test` (o `mvn javafx:run`) la aplica automáticamente.
4. Documentar la decisión en un ADR si la migración altera el contrato del dominio (ej. ADR-007 para V2).

### Rollback de migraciones

Flyway Community no incluye `undo`. Para revertir un cambio de esquema:

1. Crear una migración nueva que revierta el cambio (ej. `V3__revert_<algo>.sql`).
2. Nunca borrar entradas de `flyway_schema_history`.
3. Si es urgente en local: `podman exec -it wiredacademy-postgres psql -U wiredacademy_app -d wiredacademy` y ejecutar el `ALTER` inverso manualmente. **Solo en local, nunca en CI.**

---

## 3. Esquema

### 3.1. Tabla `usuarios` (SINGLE_TABLE)

Discriminator: `rol` (`ADMINISTRADOR`, `DIRECTOR`, `DOCENTE`, `ESTUDIANTE`, `SECRETARIO`, `COORDINADOR`, `PADRE`).

| Columna | Tipo | Constraint |
|---|---|---|
| `id` | BIGSERIAL | PK |
| `codigo` | VARCHAR(20) | UNIQUE, NOT NULL |
| `hash_contrasena` | VARCHAR(100) | NOT NULL |
| `dni` | VARCHAR(20) | UNIQUE, NOT NULL, alfanumérico 1-20 |
| `nombre` | VARCHAR(100) | NOT NULL |
| `apellido` | VARCHAR(100) | NOT NULL |
| `edad` | INT | NOT NULL, CHECK > 0 |
| `rol` | VARCHAR(20) | NOT NULL (discriminator) |
| `especialidad` | VARCHAR(100) | solo `DOCENTE` |
| `grupo_tutoria_id` | BIGINT | solo `DOCENTE` (FK a `grupos`) |

**Estrategia `SINGLE_TABLE`:** una sola tabla para toda la jerarquía `Usuario` + 7 subclases. Ventaja: consultas rápidas sin joins. Desventaja: columnas nullable según rol.

**Índices:** `idx_usuarios_rol` (discriminator), `usuarios_codigo_key` y `usuarios_dni_key` (ambos UNIQUE).

### 3.2. Catálogos

Tablas independientes sin jerarquía:

- `turnos` (VARCHAR nombre + UNIQUE lower).
- `grados` (nombre + nivel + UNIQUE lower sobre ambos).
- `periodos_academicos` (nombre + fechas + estado + UNIQUE lower).
- `aulas` (nombre + capacidad + ubicación + tipo + modalidad + UNIQUE lower).
- `asignaturas` (nombre + UNIQUE lower).

**Patrón común:** índice UNIQUE case-insensitive sobre `nombre` (o `nombre+nivel`) para evitar duplicados por diferencias de capitalización.

### 3.3. Relaciones académicas

- `grupos` (nombre + `grado_id` FK + `tutor_id` FK opcional a usuarios).
- `docentes_asignaturas` (M:N entre usuarios DOCENTE y asignaturas, con `ON DELETE CASCADE`).
- `grupo_estudiantes` (M:N entre grupos y usuarios ESTUDIANTE, con `ON DELETE CASCADE`).
- `coordinadores_grados` (M:N entre usuarios COORDINADOR y grados).
- `padres_estudiantes` (M:N entre usuarios PADRE y ESTUDIANTE, con `ON DELETE CASCADE`).

**Nota sobre doble FK de tutoría:** `grupos.tutor_id` y `usuarios.grupo_tutoria_id` apuntan a la misma relación bidireccional (tutor ↔ grupo). Motivo: consultas rápidas desde ambos lados. Deuda: unificar en V3 con `mappedBy`.

### 3.4. Eventos y horarios

- `clases` (FK a `periodo_id` + `asignatura_id` + `docente_id` + `grupo_id` + `aula_id`).
- `recreos` + `recreo_periodos` (M:N entre recreos y periodos académicos).
- Índices para consultas frecuentes: `idx_clases_docente`, `idx_clases_grupo`, `idx_clases_aula`.

### 3.5. Matrículas

- `matriculas` (UNIQUE `estudiante_id + periodo_id`, FK a `grupo_id`, CHECK `estado IN ('ACTIVA', 'RETIRADA', 'CULMINADA')`).
- `matricula_asignaturas` (M:N entre matrículas y asignaturas, con `ON DELETE CASCADE`).
- Índice: `idx_matriculas_grupo`.

### 3.6. Sesiones

- `sesiones` (UUID PK, `usuario_id`, `creada_en`, `expira_en`, `revocada_en`).
- **Preparada pero no usada aún** — `SesionUsuario` es in-memory (deuda diferida D9).
- Índices: `idx_sesiones_usuario`, `idx_sesiones_token` (parcial sobre `revocada_en IS NULL`).

---

## 4. Convenciones

### 4.1. Nombres

- Tablas en **plural** (`usuarios`, `grupos`, `matriculas`).
- Columnas en **snake_case** (`hash_contrasena`, `grupo_tutoria_id`).
- PK siempre `id BIGSERIAL`.
- FKs `<tabla_singular>_id` (`estudiante_id`, `periodo_id`).
- Índices con prefijo por tipo: `idx_` (búsqueda), `uq_` (unique), `ck_` (check), `fk_` (foreign key).

### 4.2. Índices case-insensitive

Índices UNIQUE sobre `LOWER(columna)` para nombres:

```sql
CREATE UNIQUE INDEX uq_aulas_nombre_lower ON aulas (LOWER(nombre));
CREATE UNIQUE INDEX uq_grados_nombre_nivel ON grados (LOWER(nombre), LOWER(nivel));
```

**Motivo:** evitar duplicados por mayúsculas/minúsculas.

**Contrapartida en código:** los JPA repos usan `@Query` con `LOWER(...)` para coincidir con el índice (Fase 11.1). No basta con `findByNombre` (case-sensitive).

### 4.3. Cascadas

- `ON DELETE CASCADE` en tablas M:N cuando se borra el padre (ej. `docentes_asignaturas`).
- **NO CASCADE** en FKs de catálogos (ej. `grupos.grado_id`) → protege contra borrados accidentales de catálogos con dependencias.

### 4.4. Check constraints

- `edad > 0`, `capacidad > 0`.
- `hora_inicio < hora_fin` (comparación de strings `HH:mm` funciona lexicográficamente).
- `estado IN ('ACTIVA', 'RETIRADA', 'CULMINADA')`.

---

## 5. Conexión desde local

### Variables de entorno (en `.env`, NO versionado)

```
WIREDACADEMY_DB_HOST=localhost
WIREDACADEMY_DB_PORT=5433
WIREDACADEMY_DB_NAME=wiredacademy
WIREDACADEMY_DB_USER=wiredacademy_app
WIREDACADEMY_DB_PASSWORD=<tu_password>
```

**Nota:** `.env` está en `.gitignore`. `.env.example` está versionado con placeholders.

### Acceso `psql` directo

```bash
podman exec -it wiredacademy-postgres psql -U wiredacademy_app -d wiredacademy
```

### Verificar migraciones aplicadas

```bash
podman exec -it wiredacademy-postgres psql -U wiredacademy_app -d wiredacademy \
  -c "SELECT version, script, success FROM flyway_schema_history ORDER BY installed_rank;"
```

---

## 6. Diagnóstico

Existe un script auxiliar `scripts/dev/db_diag.sh` que genera un reporte completo del estado de la BD:

- Versión, encoding, locale, timezone.
- Bases de datos + tamaños.
- Roles + extensiones + schemas.
- `flyway_schema_history` (migraciones aplicadas).
- Tablas + tamaños + conteo exacto de filas.
- Columnas, índices, FKs, constraints.
- Secuencias vs `MAX(id)` (detección de drift).
- Detección de huérfanos (FKs rotas).
- Duplicados case-insensitive.
- Fechas/horas inválidas.
- Cruces `SINGLE_TABLE` (especialidad en no-`DOCENTE`).
- Conexiones activas + locks.

**Uso:** `bash scripts/dev/db_diag.sh` → genera `tmp/DB_DIAGNOSTICO.md`.

---

## 7. Backup y restauración

### Backup (local)

```bash
podman exec wiredacademy-postgres pg_dump -U wiredacademy_app -d wiredacademy \
  > backup_wiredacademy_$(date +%Y%m%d_%H%M%S).sql
```

### Restauración (local)

```bash
cat backup_wiredacademy_20261108_150000.sql | \
  podman exec -i wiredacademy-postgres psql -U wiredacademy_app -d wiredacademy
```

**Nota:** el volumen `wiredacademy-pgdata` persiste entre reinicios del contenedor, pero un `podman volume rm wiredacademy-pgdata` lo destruye. Backup periódico recomendado antes de migraciones críticas.

---

## 8. Deuda técnica conocida

| Deuda | Impacto | Fase futura |
|---|---|---|
| Sesiones in-memory (`SesionUsuario`) | No persisten entre reinicios | ADR-009 pendiente |
| Doble FK tutoría (`grupos.tutor_id` + `usuarios.grupo_tutoria_id`) | Sincronización manual, riesgo de drift | V3 unificar |
| Fechas/horas como `String` | Validación frágil, orden lexicográfico funciona por convención | Migrar a `LocalDate`/`LocalTime` con `AttributeConverter` |
| Sin RLS (Row-Level Security) | Seguridad a nivel de fila futura | PostgreSQL soporta, sin implementar |
| Sin backups automáticos | Manual con `pg_dump` | Cron + rotación |

---

## 9. Ver también

- **`ARCHITECTURE.md`** — Capa de infraestructura y adaptadores JPA.
- **`TESTING.md`** — Tests de integración con BD real (`wiredacademy_test`).
- **`LOGGING.md`** — ADR-005 (migración de `.bin` a PostgreSQL).
- **`ADR/`** — ADR-007 (DNI agnóstico, migración V2).

---

**WiredAcademy — Ecosistema Educativo Digital**

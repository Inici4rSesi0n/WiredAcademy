#!/usr/bin/env bash
set -uo pipefail

OUT="$HOME/ProyectoJava/tmp/DB_DIAGNOSTICO.md"
CONTAINER="wiredacademy-postgres"
DB="wiredacademy"
DB_TEST="wiredacademy_test"

# --- Credenciales extraídas del contenedor (no hardcodeadas) ---
DBUSER="$(podman inspect "$CONTAINER" --format '{{range .Config.Env}}{{println .}}{{end}}' | sed -n 's/^POSTGRES_USER=//p')"
DBPASS="$(podman inspect "$CONTAINER" --format '{{range .Config.Env}}{{println .}}{{end}}' | sed -n 's/^POSTGRES_PASSWORD=//p')"
: "${DBUSER:=wiredacademy_app}"

if [ -z "${DBPASS:-}" ]; then
  echo "ERROR: no pude extraer POSTGRES_PASSWORD del contenedor $CONTAINER" >&2
  exit 1
fi

mkdir -p "$(dirname "$OUT")"
: > "$OUT"

# Wrapper psql: pasa password por env, sin prompts, sin exponer en history
psql_run() {
    local db="$1"; shift
    podman exec -i -e PGPASSWORD="$DBPASS" "$CONTAINER" \
        psql -U "$DBUSER" -d "$db" -w -A -F ' | ' --pset=footer=off "$@"
}

emit()   { echo "" >> "$OUT"; echo "### $1" >> "$OUT"; echo '```' >> "$OUT"; }
finish() { echo '```' >> "$OUT"; }

Q() {
    local title="$1"; local sql="$2"; local db="${3:-$DB}"
    emit "$title"
    psql_run "$db" -c "$sql" >> "$OUT" 2>&1 || echo "(ERROR ejecutando consulta)" >> "$OUT"
    finish
}

B() {
    local title="$1"; local cmd="$2"
    emit "$title"
    bash -c "$cmd" >> "$OUT" 2>&1 || echo "(ERROR ejecutando bloque)" >> "$OUT"
    finish
}

echo "# Diagnóstico PostgreSQL · WiredAcademy" >> "$OUT"
echo "" >> "$OUT"
echo "_Generado: $(date -Iseconds)_" >> "$OUT"
echo "_Credenciales leídas del contenedor (POSTGRES_USER=$DBUSER, password no impresa)_" >> "$OUT"

# ================= CONTENEDOR =================
B "Estado del contenedor"   "podman inspect $CONTAINER --format 'Status={{.State.Status}} | StartedAt={{.State.StartedAt}} | Restarts={{.RestartCount}} | Image={{.Config.Image}}'"
B "Healthcheck"              "podman inspect $CONTAINER --format '{{if .State.Health}}{{.State.Health.Status}}{{else}}sin healthcheck definido{{end}}'"
B "Puertos"                  "podman port $CONTAINER"
B "Mounts (volúmenes)"       "podman inspect $CONTAINER --format '{{json .Mounts}}'"
B "Recursos (snapshot)"      "podman stats --no-stream $CONTAINER"
B "Fecha contenedor vs host" "podman exec $CONTAINER date -u && date -u"

# ================= POSTGRES GENERAL =================
Q "Versión"                   "SELECT version();"
Q "Parámetros clave"          "SHOW server_version; SHOW server_encoding; SHOW lc_collate; SHOW lc_ctype; SHOW timezone; SHOW DateStyle; SHOW search_path; SHOW max_connections; SHOW shared_buffers; SHOW work_mem;"
Q "Usuario/sesión actual"     "SELECT current_user, session_user, current_database(), current_schema();"
Q "Bases de datos con tamaño" "SELECT datname, pg_size_pretty(pg_database_size(datname)) AS tamano FROM pg_database ORDER BY pg_database_size(datname) DESC;"
Q "Roles"                     "SELECT rolname, rolsuper, rolcreaterole, rolcreatedb, rolcanlogin, rolconnlimit FROM pg_roles ORDER BY rolname;"
Q "Extensiones"               "SELECT extname, extversion FROM pg_extension ORDER BY extname;"
Q "Schemas"                   "SELECT nspname FROM pg_namespace WHERE nspname NOT LIKE 'pg_%' AND nspname!='information_schema' ORDER BY nspname;"
Q "pg_hba_file_rules"         "SELECT line_number, type, database, user_name, address, auth_method, error FROM pg_hba_file_rules ORDER BY line_number;"
Q "Rutas config/data"         "SHOW config_file; SHOW data_directory;"

# ================= FLYWAY =================
Q "Flyway · wiredacademy" "SELECT installed_rank, version, description, type, script, checksum, installed_by, installed_on, execution_time, success FROM flyway_schema_history ORDER BY installed_rank;"

# ================= TEST DB (solo si existe) =================
TEST_EXISTS=$(psql_run postgres -t -c "SELECT 1 FROM pg_database WHERE datname='$DB_TEST';" 2>/dev/null | tr -d '[:space:]')
if [ "$TEST_EXISTS" = "1" ]; then
    Q "Flyway · $DB_TEST" "SELECT installed_rank, version, description, type, script, checksum, installed_by, installed_on, execution_time, success FROM flyway_schema_history ORDER BY installed_rank;" "$DB_TEST"
    Q "Tablas en $DB_TEST" "SELECT table_schema||'.'||table_name FROM information_schema.tables WHERE table_schema NOT IN ('pg_catalog','information_schema') AND table_type='BASE TABLE' ORDER BY 1;" "$DB_TEST"
else
    echo "" >> "$OUT"; echo "### Base de datos \`$DB_TEST\`" >> "$OUT"
    echo '```' >> "$OUT"; echo "(no existe — se creará al primer \`mvn test\`)" >> "$OUT"; echo '```' >> "$OUT"
fi

# ================= TABLAS + FILAS =================
Q "Tablas y tamaños" "SELECT relname, pg_size_pretty(pg_total_relation_size(relid)) AS total, n_live_tup, n_dead_tup, last_vacuum, last_autovacuum FROM pg_stat_user_tables ORDER BY relname;"

Q "Conteo exacto por tabla" "
SELECT table_schema||'.'||table_name AS tabla,
       (xpath('/row/c/text()', query_to_xml(format('SELECT COUNT(*) FROM %I.%I', table_schema, table_name), false, true, '')))[1]::text::bigint AS filas
FROM information_schema.tables
WHERE table_schema='public' AND table_type='BASE TABLE'
ORDER BY 1;"

# ================= ESQUEMA =================
Q "Columnas"           "SELECT table_name, ordinal_position, column_name, data_type, is_nullable, column_default FROM information_schema.columns WHERE table_schema='public' ORDER BY table_name, ordinal_position;"
Q "Índices"            "SELECT tablename, indexname, indexdef FROM pg_indexes WHERE schemaname='public' ORDER BY tablename, indexname;"
Q "Foreign Keys"       "SELECT conrelid::regclass AS tabla, conname, pg_get_constraintdef(oid) FROM pg_constraint WHERE contype='f' AND connamespace='public'::regnamespace ORDER BY 1::text, conname;"
Q "Check constraints"  "SELECT conrelid::regclass AS tabla, conname, pg_get_constraintdef(oid) FROM pg_constraint WHERE contype='c' AND connamespace='public'::regnamespace ORDER BY 1::text, conname;"
Q "Unique constraints" "SELECT conrelid::regclass AS tabla, conname, pg_get_constraintdef(oid) FROM pg_constraint WHERE contype='u' AND connamespace='public'::regnamespace ORDER BY 1::text, conname;"

# ================= SECUENCIAS =================
Q "Secuencias y drift vs MAX(id)" "
SELECT t.table_name,
       pg_get_serial_sequence(t.table_name, t.column_name) AS secuencia,
       pg_sequence_last_value(pg_get_serial_sequence(t.table_name, t.column_name)::regclass) AS seq_last,
       (xpath('/row/c/text()', query_to_xml(format('SELECT MAX(%I) FROM %I', t.column_name, t.table_name), false, true, '')))[1]::text::bigint AS max_id
FROM information_schema.columns t
WHERE t.table_schema='public' AND t.column_default LIKE 'nextval%'
ORDER BY t.table_name;"

# ================= INTEGRIDAD =================
Q "MISMATCH tutor grupos→usuarios" "
SELECT g.id, g.nombre AS grupo, g.tutor_id, u.grupo_tutoria_id
FROM grupos g LEFT JOIN usuarios u ON u.id=g.tutor_id
WHERE g.tutor_id IS NOT NULL AND (u.grupo_tutoria_id IS DISTINCT FROM g.id OR u.id IS NULL);"

Q "MISMATCH tutor usuarios→grupos" "
SELECT u.id, u.codigo, u.grupo_tutoria_id, g.tutor_id
FROM usuarios u LEFT JOIN grupos g ON g.id=u.grupo_tutoria_id
WHERE u.grupo_tutoria_id IS NOT NULL AND (g.tutor_id IS DISTINCT FROM u.id OR g.id IS NULL);"

Q "Duplicados CI · aulas"       "SELECT LOWER(nombre) AS k, COUNT(*) FROM aulas GROUP BY LOWER(nombre) HAVING COUNT(*)>1;"
Q "Duplicados CI · asignaturas" "SELECT LOWER(nombre) AS k, COUNT(*) FROM asignaturas GROUP BY LOWER(nombre) HAVING COUNT(*)>1;"
Q "Duplicados CI · turnos"      "SELECT LOWER(nombre) AS k, COUNT(*) FROM turnos GROUP BY LOWER(nombre) HAVING COUNT(*)>1;"
Q "Duplicados CI · periodos"    "SELECT LOWER(nombre) AS k, COUNT(*) FROM periodos_academicos GROUP BY LOWER(nombre) HAVING COUNT(*)>1;"
Q "Duplicados CI · grados"      "SELECT LOWER(nombre) AS n, LOWER(nivel) AS niv, COUNT(*) FROM grados GROUP BY 1,2 HAVING COUNT(*)>1;"
Q "Duplicados CI · grupos"      "SELECT LOWER(nombre) AS n, grado_id, COUNT(*) FROM grupos GROUP BY 1,2 HAVING COUNT(*)>1;"
Q "Duplicados exactos · recreos" "SELECT descripcion, dia_semana, hora_inicio, hora_fin, COUNT(*) FROM recreos GROUP BY 1,2,3,4 HAVING COUNT(*)>1;"

Q "Huérfanos · grupos sin grado"       "SELECT g.id FROM grupos g LEFT JOIN grados r ON r.id=g.grado_id WHERE r.id IS NULL;"
Q "Huérfanos · clases"                  "SELECT c.id FROM clases c LEFT JOIN usuarios d ON d.id=c.docente_id LEFT JOIN asignaturas a ON a.id=c.asignatura_id LEFT JOIN grupos g ON g.id=c.grupo_id LEFT JOIN aulas au ON au.id=c.aula_id LEFT JOIN periodos_academicos p ON p.id=c.periodo_id WHERE d.id IS NULL OR a.id IS NULL OR g.id IS NULL OR au.id IS NULL OR p.id IS NULL;"
Q "Huérfanos · matrículas"              "SELECT m.id FROM matriculas m LEFT JOIN usuarios e ON e.id=m.estudiante_id LEFT JOIN periodos_academicos p ON p.id=m.periodo_id LEFT JOIN grupos g ON g.id=m.grupo_id WHERE e.id IS NULL OR p.id IS NULL OR g.id IS NULL;"
Q "Huérfanos · docentes_asignaturas"    "SELECT da.docente_id, da.asignatura_id FROM docentes_asignaturas da LEFT JOIN usuarios d ON d.id=da.docente_id LEFT JOIN asignaturas a ON a.id=da.asignatura_id WHERE d.id IS NULL OR a.id IS NULL;"
Q "Huérfanos · grupo_estudiantes"       "SELECT ge.grupo_id, ge.estudiante_id FROM grupo_estudiantes ge LEFT JOIN grupos g ON g.id=ge.grupo_id LEFT JOIN usuarios e ON e.id=ge.estudiante_id WHERE g.id IS NULL OR e.id IS NULL;"
Q "Huérfanos · coordinadores_grados"    "SELECT cg.coordinador_id, cg.grado_id FROM coordinadores_grados cg LEFT JOIN usuarios c ON c.id=cg.coordinador_id LEFT JOIN grados g ON g.id=cg.grado_id WHERE c.id IS NULL OR g.id IS NULL;"
Q "Huérfanos · padres_estudiantes"      "SELECT pe.padre_id, pe.estudiante_id FROM padres_estudiantes pe LEFT JOIN usuarios p ON p.id=pe.padre_id LEFT JOIN usuarios e ON e.id=pe.estudiante_id WHERE p.id IS NULL OR e.id IS NULL;"
Q "Huérfanos · recreo_periodos"         "SELECT rp.recreo_id, rp.periodo_id FROM recreo_periodos rp LEFT JOIN recreos r ON r.id=rp.recreo_id LEFT JOIN periodos_academicos p ON p.id=rp.periodo_id WHERE r.id IS NULL OR p.id IS NULL;"
Q "Huérfanos · matricula_asignaturas"   "SELECT ma.matricula_id, ma.asignatura_id FROM matricula_asignaturas ma LEFT JOIN matriculas m ON m.id=ma.matricula_id LEFT JOIN asignaturas a ON a.id=ma.asignatura_id WHERE m.id IS NULL OR a.id IS NULL;"

Q "Fechas inválidas · periodos"   "SELECT id, nombre, fecha_inicio, fecha_fin FROM periodos_academicos WHERE fecha_inicio !~ '^\\d{4}-\\d{2}-\\d{2}\$' OR fecha_fin !~ '^\\d{4}-\\d{2}-\\d{2}\$';"
Q "Fechas inválidas · matrículas" "SELECT id, fecha FROM matriculas WHERE fecha !~ '^\\d{4}-\\d{2}-\\d{2}\$';"
Q "Horas inválidas · clases"      "SELECT id, hora_inicio, hora_fin FROM clases WHERE hora_inicio !~ '^([01]\\d|2[0-3]):[0-5]\\d\$' OR hora_fin !~ '^([01]\\d|2[0-3]):[0-5]\\d\$';"
Q "Horas inválidas · recreos"     "SELECT id, hora_inicio, hora_fin FROM recreos WHERE hora_inicio !~ '^([01]\\d|2[0-3]):[0-5]\\d\$' OR hora_fin !~ '^([01]\\d|2[0-3]):[0-5]\\d\$';"

Q "Usuarios por rol"  "SELECT rol, COUNT(*) FROM usuarios GROUP BY rol ORDER BY rol;"
Q "Cruces SINGLE_TABLE" "
SELECT id, codigo, rol, especialidad, grupo_tutoria_id
FROM usuarios
WHERE (rol <> 'DOCENTE' AND (especialidad IS NOT NULL OR grupo_tutoria_id IS NOT NULL))
   OR (rol = 'DOCENTE' AND especialidad IS NULL);"

# ================= SALUD =================
Q "Conexiones activas"        "SELECT pid, usename, application_name, client_addr, state, now()-backend_start AS duracion, LEFT(query,80) AS query FROM pg_stat_activity ORDER BY backend_start;"
Q "Locks no-propios"          "SELECT locktype, mode, granted, relation::regclass, pid FROM pg_locks WHERE pid <> pg_backend_pid() ORDER BY pid;"
Q "Estadísticas de la BD"     "SELECT numbackends, xact_commit, xact_rollback, blks_read, blks_hit, tup_inserted, tup_updated, tup_deleted, conflicts, deadlocks FROM pg_stat_database WHERE datname=current_database();"

echo "" >> "$OUT"
echo "## Fin del diagnóstico" >> "$OUT"
echo
echo "Listo: $OUT"
wc -l "$OUT"

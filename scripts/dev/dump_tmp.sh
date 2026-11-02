#!/usr/bin/env bash
set -eo pipefail

PROYECTO="${HOME}/ProyectoJava"
TMP="${PROYECTO}/tmp"
rm -rf "${TMP}"
mkdir -p "${TMP}"
cd "${PROYECTO}"

LIST="$(mktemp)"
trap 'rm -f "$LIST"' EXIT

find . -type d \( \
    -name target -o -name logs -o -name .git -o -name .idea \
    -o -name .vscode -o -name node_modules -o -name .mvn -o -name tmp \
  \) -prune -o \
  -type f \( \
    -name '*.java' -o -name '*.fxml' -o -name '*.css' -o \
    -name '*.xml'  -o -name '*.sql'  -o -name '*.properties' -o \
    -name '*.md'   -o -name '*.sh'   -o -name '*.yml' -o \
    -name '*.yaml' -o -name '*.json' -o -name '*.txt' \
  \) ! -name '*.gz' ! -name '*.log' -print | sort > "$LIST"

echo "Total archivos incluidos: $(wc -l < "$LIST")"
echo

dump_list() {
    local outfile="$1"; shift
    local title="$1"; shift
    {
        echo "# ${title}"
        echo
        echo "_Proyecto WiredAcademy — volcado filtrado para revisión_"
        echo
        if [ "$#" -eq 0 ]; then
            echo "_(sin archivos en este grupo)_"
        else
            for f in "$@"; do
                [ -f "$f" ] || continue
                rel="${f#./}"
                ext="${rel##*.}"
                echo
                echo "---"
                echo
                echo "## \`${rel}\`"
                echo
                echo "\`\`\`${ext}"
                cat -- "$f"
                echo
                echo '```'
            done
        fi
    } > "${TMP}/${outfile}"
}

mapfile -t G_META < <(
    grep -E '^\./(pom\.xml|LOGGING\.md)$' "$LIST" || true
    grep -E '^\./scripts/.*\.sh$' "$LIST" || true
    grep -E '^\./src/main/resources/(application\.properties|logback-spring\.xml)$' "$LIST" || true
    grep -E '^\./src/main/resources/db/' "$LIST" || true
    grep -E '^\./src/test/resources/' "$LIST" || true
)
dump_list "00_META.md" "00 — META (build, config, logging, SQL, scripts)" "${G_META[@]}"

mapfile -t G_DOM < <(grep -E '^\./src/main/java/dominio/' "$LIST" || true)
dump_list "01_DOMINIO.md" "01 — Dominio (modelo + puertos)" "${G_DOM[@]}"

mapfile -t G_APP < <(grep -E '^\./src/main/java/(aplicacion|boot)/' "$LIST" || true)
dump_list "02_APLICACION.md" "02 — Aplicación (casos de uso, DTOs, puertos, servicio, boot)" "${G_APP[@]}"

mapfile -t G_IP < <(grep -E '^\./src/main/java/infraestructura/persistencia/' "$LIST" || true)
dump_list "03_INFRA_PERSISTENCIA.md" "03 — Infraestructura · Persistencia (adaptadores, entidades, JPA, mappers)" "${G_IP[@]}"

mapfile -t G_IR < <(grep -E '^\./src/main/java/infraestructura/' "$LIST" | grep -Ev '^\./src/main/java/infraestructura/persistencia/' || true)
dump_list "04_INFRA_RESTO.md" "04 — Infraestructura · resto (seguridad, eventos, log, config, i18n)" "${G_IR[@]}"

mapfile -t G_PRES < <(grep -E '^\./src/main/java/presentacion/' "$LIST" || true)
dump_list "05_PRESENTACION.md" "05 — Presentación (controladores, DTOs, diálogos, utilidades, animaciones)" "${G_PRES[@]}"

mapfile -t G_UI < <(grep -E '^\./src/main/resources/(fxml|css)/' "$LIST" || true)
dump_list "06_RECURSOS_FXML_CSS.md" "06 — Recursos UI (FXML + CSS)" "${G_UI[@]}"

mapfile -t G_TU < <(grep -E '^\./src/test/java/' "$LIST" | grep -Ev '^\./src/test/java/(integration|presentacion)/' || true)
dump_list "07_TESTS_UNITARIOS.md" "07 — Tests unitarios" "${G_TU[@]}"

mapfile -t G_TI < <(grep -E '^\./src/test/java/(integration|presentacion)/' "$LIST" || true)
dump_list "08_TESTS_INTEGRACION_UI.md" "08 — Tests de integración y UI" "${G_TI[@]}"

{
    for arr in G_META G_DOM G_APP G_IP G_IR G_PRES G_UI G_TU G_TI; do
        eval "printf '%s\n' \"\${${arr}[@]:-}\""
    done
} | sed '/^$/d' | sort -u > /tmp/assigned.$$
comm -23 <(sort -u "$LIST") /tmp/assigned.$$ > /tmp/unassigned.$$

echo "=== Verificación ==="
if [ -s /tmp/unassigned.$$ ]; then
    echo "⚠ Archivos sin asignar:"
    cat /tmp/unassigned.$$
else
    echo "✓ Todos los archivos asignados a un grupo (0 huérfanos)."
fi
rm -f /tmp/assigned.$$ /tmp/unassigned.$$

echo
echo "=== Resultado en ${TMP}/ ==="
ls -lhS "${TMP}"

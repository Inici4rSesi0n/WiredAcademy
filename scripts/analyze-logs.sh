#!/usr/bin/env bash
# analyze-logs.sh — Analiza logs generados por mvn test buscando bugs/regresiones.
# Uso: ./scripts/analyze-logs.sh [LOGS_DIR] [BASELINE]
set -uo pipefail

LOGS_DIR="${1:-./logs}"
BASELINE="${2:-./scripts/dev/log-baseline.txt}"

RED='\033[0;31m'; GREEN='\033[0;32m'; YELLOW='\033[1;33m'; NC='\033[0m'
ERRORES=0

# Helper: cuenta lineas que matchean un patron, sin output doble
count_matches() {
    local file="$1" pattern="$2"
    [ -f "$file" ] || { echo 0; return; }
    local n
    n=$(grep -c "$pattern" "$file" 2>/dev/null | head -1)
    echo "${n:-0}"
}

echo "================================================================"
echo " Analisis de logs"
echo " LOGS_DIR: $LOGS_DIR"
echo " BASELINE: $BASELINE"
echo "================================================================"
echo

# [1/4] Errores no whitelisted en error.log
echo "[1/4] Errores no conocidos en error.log"
if [ -f "$LOGS_DIR/wiredacademy-error.log" ]; then
    # 1a. Extraer todas las lineas ERROR (deduplicadas)
    ERROR_LINES=$(grep "ERROR" "$LOGS_DIR/wiredacademy-error.log" 2>/dev/null | sort -u)
    TOTAL_ERRORES=$(echo -n "$ERROR_LINES" | grep -c '^' 2>/dev/null | head -1 || true)
    TOTAL_ERRORES=${TOTAL_ERRORES:-0}

    # 1b. Filtrar patrones del baseline (busqueda por substring, no exacta)
    if [ -f "$BASELINE" ] && [ -n "$ERROR_LINES" ]; then
        while IFS= read -r pattern; do
            [ -z "$pattern" ] && continue
            [[ "$pattern" == \#* ]] && continue
            ERROR_LINES=$(echo "$ERROR_LINES" | grep -vF "$pattern" || true)
        done < "$BASELINE"
    fi

    NUEVOS=$(echo -n "$ERROR_LINES" | grep -c '^' 2>/dev/null | head -1 || true)
    NUEVOS=${NUEVOS:-0}

    if [ "$NUEVOS" -eq 0 ]; then
        echo -e "${GREEN}  OK: 0 errores nuevos (de $TOTAL_ERRORES totales, todos whitelisted)${NC}"
    else
        echo -e "${RED}  FALLO: $NUEVOS errores nuevos (de $TOTAL_ERRORES totales)${NC}"
        echo "$ERROR_LINES" | head -20 | while IFS= read -r line; do
            echo -e "${RED}  x ${line:0:180}${NC}"
        done
        ERRORES=$((ERRORES + NUEVOS))
    fi
else
    echo -e "${YELLOW}  AVISO: archivo no existe (sin tests recientes)${NC}"
fi

# [2/4] EventBus: listeners GC'd
echo
echo "[2/4] EventBus: SIN SUSCRIPTORES ACTIVOS"
N=$(count_matches "$LOGS_DIR/wiredacademy-eventbus.log" "SIN SUSCRIPTORES ACTIVOS")
if [ "$N" -gt 0 ]; then
    echo -e "${RED}  FALLO: $N listeners GC'd${NC}"
    ERRORES=$((ERRORES + N))
else
    echo -e "${GREEN}  OK: 0 listeners GC'd${NC}"
fi

# [3/4] Bugs historicos reincidentes
echo
echo "[3/4] Bugs historicos reincidentes"
HIST_OK=true
for patron in "Query did not return a unique result" "value too long for type character varying"; do
    N=0
    for logf in "$LOGS_DIR"/*.log; do
        [ -f "$logf" ] || continue
        n=$(count_matches "$logf" "$patron")
        N=$((N + n))
    done
    if [ "$N" -gt 0 ]; then
        echo -e "${RED}  FALLO: '$patron' x$N${NC}"
        ERRORES=$((ERRORES + N))
        HIST_OK=false
    fi
done
[ "$HIST_OK" = true ] && echo -e "${GREEN}  OK: 0 bugs historicos${NC}"

# [4/4] Resumen de actividad
echo
echo "[4/4] Resumen de actividad"
MUT=$(count_matches "$LOGS_DIR/wiredacademy-audit.log" "op=\(CREATE\|UPDATE\|DELETE\|LINK\|UNLINK\)")
EV=$(count_matches "$LOGS_DIR/wiredacademy-eventbus.log" "publicar evento=")
echo "  Mutaciones (audit):       $MUT"
echo "  Eventos publicados (bus): $EV"
echo "  (EVENTBUS=WARN en test profile -> eventos no se loguean en DEBUG)"

echo
echo "================================================================"
if [ "$ERRORES" -eq 0 ]; then
    echo -e "${GREEN}RESULTADO: 0 bugs detectados. Analisis limpio.${NC}"
    exit 0
else
    echo -e "${RED}RESULTADO: $ERRORES bugs detectados. Revisar logs.${NC}"
    exit 1
fi

#!/usr/bin/env bash
# run-bot.sh — Orquesta: archivar logs previos -> tests -> analizar logs -> veredicto.
# Uso: ./scripts/run-bot.sh [fast|ui|full]
set -uo pipefail

MODE="${1:-fast}"
case "$MODE" in
    fast|ui|full) ;;
    *) echo "Uso: $0 {fast|ui|full}"; exit 2 ;;
esac

mkdir -p logs/test

# 1. Archivar logs de runtime previos (dev/prod)
if ls logs/*.log 1>/dev/null 2>&1; then
    ARCHIVE="logs/_archive/$(date +%Y%m%d_%H%M%S)_runtime"
    mkdir -p "$ARCHIVE"
    mv logs/*.log "$ARCHIVE/"
    echo "Logs runtime previos archivados en: $ARCHIVE"
fi

# 2. Archivar logs de test previos
if ls logs/test/*.log 1>/dev/null 2>&1; then
    ARCHIVE="logs/_archive/$(date +%Y%m%d_%H%M%S)_test"
    mkdir -p "$ARCHIVE"
    mv logs/test/*.log "$ARCHIVE/"
    echo "Logs test previos archivados en: $ARCHIVE"
fi

# 3. Ejecutar tests
echo
echo "================================================================"
echo " Ejecutando tests: $MODE"
echo "================================================================"
bash scripts/test.sh "$MODE"
TEST_EXIT=$?

# 4. Analizar logs (preferir logs/test/ si tiene contenido; si no, logs/)
LOGS_TARGET="logs"
if [ -d "logs/test" ] && [ -n "$(ls -A logs/test 2>/dev/null)" ]; then
    LOGS_TARGET="logs/test"
fi

echo
echo "================================================================"
echo " Analizando logs generados en: $LOGS_TARGET"
echo "================================================================"
bash scripts/analyze-logs.sh "$LOGS_TARGET" scripts/dev/log-baseline.txt
ANALYZE_EXIT=$?

# 5. Veredicto final
echo
echo "================================================================"
if [ "$TEST_EXIT" -ne 0 ]; then
    echo "VEREDICTO: Tests FALLARON (exit=$TEST_EXIT). Bot aborta analisis."
    echo "================================================================"
    exit "$TEST_EXIT"
fi
if [ "$ANALYZE_EXIT" -ne 0 ]; then
    echo "VEREDICTO: Tests OK pero analisis detecto bugs (exit=$ANALYZE_EXIT)."
    echo "================================================================"
    exit "$ANALYZE_EXIT"
fi
echo "VEREDICTO: Todo OK (tests verdes + logs limpios)."
echo "================================================================"
exit 0

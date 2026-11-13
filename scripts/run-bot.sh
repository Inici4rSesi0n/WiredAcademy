#!/usr/bin/env bash
set -uo pipefail
MODE="${1:-fast}"
case "$MODE" in
    fast|ui|full) ;;
    *) echo "Uso: $0 {fast|ui|full}"; exit 2 ;;
esac
mkdir -p logs/test
if ls logs/*.log 1>/dev/null 2>&1; then
    ARCHIVE="logs/_archive/$(date +%Y%m%d_%H%M%S)_runtime"
    mkdir -p "$ARCHIVE"
    mv logs/*.log "$ARCHIVE/"
    echo "Logs runtime previos archivados en: $ARCHIVE"
fi
if ls logs/test/*.log 1>/dev/null 2>&1; then
    ARCHIVE="logs/_archive/$(date +%Y%m%d_%H%M%S)_test"
    mkdir -p "$ARCHIVE"
    mv logs/test/*.log "$ARCHIVE/"
    echo "Logs test previos archivados en: $ARCHIVE"
fi
echo
echo "================================================================"
echo " Ejecutando tests: $MODE"
echo "================================================================"
bash scripts/test.sh "$MODE"
TEST_EXIT=$?
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

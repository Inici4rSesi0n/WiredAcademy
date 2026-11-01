#!/bin/bash
set -e

MODE="${1:-fast}"

case "$MODE" in
    fast)
        echo "Tests rápidos (unitarios + integración, sin UI)"
        echo "   Tiempo esperado: ~2 min"
        mvn clean test
        ;;
    ui)
        echo "Tests de UI (TestFX)"
        echo "   Tiempo esperado: ~5 min"
        mvn clean test -Pui
        ;;
    full)
        echo "Suite completa (unitarios + integración + UI)"
        echo "   Tiempo esperado: ~10 min"
        mvn clean test -Pfull
        ;;
    *)
        echo "Modo desconocido: $MODE"
        echo "Uso: ./scripts/test.sh {fast|ui|full}"
        exit 1
        ;;
esac

echo "✅ Completado"

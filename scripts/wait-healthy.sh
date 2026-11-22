#!/usr/bin/env bash
set -euo pipefail

echo "=========================================="
echo "[CHECK] Contrôle de santé de l'environnement Staging"
echo "=========================================="

for i in $(seq 1 12); do
  echo "[TENTATIVE $i/12] Requête GET http://localhost:8080/health..."
  if curl -fsS http://localhost:8080/health; then
    echo ""
    echo "=========================================="
    echo "staging sain"
    echo "=========================================="
    exit 0
  fi
  sleep 5
done

echo "==========================================" >&2
echo "staging non sain" >&2
echo "==========================================" >&2
exit 1

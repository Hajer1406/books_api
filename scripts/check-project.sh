#!/usr/bin/env bash
set -u

errors=0
required=(
  README.md
  .gitignore
  .env.example
  docs/architecture.md
  pom.xml
)

echo "=========================================="
echo "[CHECK] Démarrage des contrôles du projet"
echo "=========================================="

echo "[INFO] Vérification des fichiers obligatoires..."
for file in "${required[@]}"; do
  if [[ -f "$file" ]]; then
    echo "  [OK] $file est présent"
  else
    echo "  [ERREUR] Fichier obligatoire manquant : $file" >&2
    errors=$((errors + 1))
  fi
done

echo "[INFO] Vérification de l'absence de secrets ou fichiers .env suivis par Git..."
if git ls-files | grep -Eq '(^|/)\.env(\..+)?$' | grep -v '\.env\.example$'; then
  echo "  [ERREUR] ALERTE SÉCURITÉ : Un fichier .env contenant potentiellement des secrets est suivi par Git !" >&2
  errors=$((errors + 1))
else
  echo "  [OK] Aucun fichier .env n'est suivi par Git"
fi

echo "[INFO] Vérification de la configuration d'exemple..."
if grep -q "livres_db" .env.example; then
  echo "  [OK] .env.example contient bien la base de données livres_db"
else
  echo "  [ERREUR] .env.example ne référence pas la base attendue livres_db" >&2
  errors=$((errors + 1))
fi

echo "=========================================="
if [[ $errors -gt 0 ]]; then
  echo "[ECHEC] $errors erreur(s) détectée(s)" >&2
  exit 1
fi

echo "[SUCCÈS] Tous les contrôles du projet sont validés !"
exit 0

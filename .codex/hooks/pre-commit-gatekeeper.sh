#!/usr/bin/env bash
# pre-commit-gatekeeper.sh
# Commit oncesi calisir: console.log/System.out.println, yakalanmamis
# exception ve prod-guard ihlallerini arar. Herhangi biri bulunursa
# commit engellenir.

set -euo pipefail

PROJECT_ROOT="$(git rev-parse --show-toplevel 2>/dev/null || pwd)"
STAGED_FILES=$(git diff --cached --name-only --diff-filter=ACM || true)

if [[ -z "${STAGED_FILES}" ]]; then
  echo "[pre-commit-gatekeeper] Staged dosya yok, atlaniyor."
  exit 0
fi

VIOLATIONS=0

echo "[pre-commit-gatekeeper] console.log / System.out.println taramasi..."
while IFS= read -r f; do
  [[ -f "${f}" ]] || continue
  case "${f}" in
    *.java)
      if grep -nE 'System\.(out|err)\.println' "${f}"; then
        echo "  -> ${f} icinde System.out/err.println bulundu." >&2
        VIOLATIONS=1
      fi
      ;;
    *.ts|*.tsx|*.js|*.jsx)
      if grep -nE 'console\.(log|debug|info)\(' "${f}"; then
        echo "  -> ${f} icinde console.log bulundu." >&2
        VIOLATIONS=1
      fi
      ;;
  esac
done <<< "${STAGED_FILES}"

echo "[pre-commit-gatekeeper] Genel catch-all (yakalanmamis exception riski) taramasi..."
while IFS= read -r f; do
  [[ -f "${f}" ]] || continue
  if [[ "${f}" == *.java ]] && grep -nE 'catch\s*\(\s*Exception\s+\w+\s*\)\s*\{\s*\}' "${f}"; then
    echo "  -> ${f} icinde bos catch(Exception) blogu bulundu." >&2
    VIOLATIONS=1
  fi
done <<< "${STAGED_FILES}"

if [[ "${VIOLATIONS}" -eq 1 ]]; then
  echo "[pre-commit-gatekeeper] HATA: Ihlaller bulundu. Commit engellendi." >&2
  exit 1
fi

echo "[pre-commit-gatekeeper] Temiz. Commit'e izin veriliyor."
exit 0

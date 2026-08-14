#!/usr/bin/env bash
# post-code-generation.sh
# Kod uretildikten hemen sonra calisir: static analysis / linter calistirir.
# Backend (Java/Spring) ve Frontend (TS/React) icin ayri kontrol yapar.

set -euo pipefail

PROJECT_ROOT="$(git rev-parse --show-toplevel 2>/dev/null || pwd)"
FAILED=0

echo "[post-code-generation] Static analysis basliyor..."

# --- Backend: Checkstyle / Spotless (varsa) ---
if [[ -f "${PROJECT_ROOT}/unifeed-backend/pom.xml" ]]; then
  echo "[post-code-generation] Backend: mvn spotless:check calistiriliyor..."
  (cd "${PROJECT_ROOT}/unifeed-backend" && mvn -q spotless:check) || FAILED=1
fi

# --- Frontend: ESLint + TypeScript check ---
if [[ -f "${PROJECT_ROOT}/unifeed-frontend/package.json" ]]; then
  echo "[post-code-generation] Frontend: eslint + tsc calistiriliyor..."
  (cd "${PROJECT_ROOT}/unifeed-frontend" && npx eslint . --max-warnings=0) || FAILED=1
  (cd "${PROJECT_ROOT}/unifeed-frontend" && npx tsc --noEmit) || FAILED=1
fi

if [[ "${FAILED}" -eq 1 ]]; then
  echo "[post-code-generation] HATA: Static analysis basarisiz. Ajan kodu duzeltmeli." >&2
  exit 1
fi

echo "[post-code-generation] Static analysis basarili."
exit 0

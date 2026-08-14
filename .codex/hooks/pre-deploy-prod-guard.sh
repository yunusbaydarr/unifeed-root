#!/usr/bin/env bash
# pre-deploy-prod-guard.sh
# ARCHITECT.md #4 - Test modu guvenlik kilidi.
# application-prod.yml icinde allow-test-domains:true varsa deploy'u
# durdurur. Bu hook CI/CD pipeline'inin prod deploy adimindan HEMEN
# once calistirilmalidir.

set -euo pipefail

PROJECT_ROOT="$(git rev-parse --show-toplevel 2>/dev/null || pwd)"
PROD_CONFIG="${PROJECT_ROOT}/unifeed-backend/src/main/resources/application-prod.yml"

echo "[pre-deploy-prod-guard] Prod konfigurasyonu taraniyor: ${PROD_CONFIG}"

if [[ ! -f "${PROD_CONFIG}" ]]; then
  echo "[pre-deploy-prod-guard] HATA: application-prod.yml bulunamadi. Deploy durduruldu." >&2
  exit 1
fi

if grep -Eq '^\s*allow-test-domains:\s*true\s*$' "${PROD_CONFIG}"; then
  echo "[pre-deploy-prod-guard] KRITIK HATA: allow-test-domains:true prod'a sizmis!" >&2
  echo "[pre-deploy-prod-guard] Bkz. ARCHITECT.md paragraf 4. Deploy DURDURULDU." >&2
  exit 1
fi

# Ek kontrol: test hesaplarinin admin rolune atanmadigini dogrula (DB check
# CI adiminda ayrica calisir, burada sadece config seviyesi kontrol edilir)
if grep -Eiq 'gmail\.com' "${PROD_CONFIG}"; then
  echo "[pre-deploy-prod-guard] UYARI: prod config icinde 'gmail.com' referansi bulundu, manuel incele." >&2
fi

echo "[pre-deploy-prod-guard] Prod guard basarili. Deploy'a devam edilebilir."
exit 0

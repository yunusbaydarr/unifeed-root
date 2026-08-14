#!/usr/bin/env bash
# on-contract-violation.sh
# OpenAPI/AsyncAPI kontratina uymayan DTO/payload tespit edildiginde
# ilgili ajani durdurur ve docs/DECISIONS.md'de acik bir karar olup
# olmadigini kontrol eder.

set -euo pipefail

PROJECT_ROOT="$(git rev-parse --show-toplevel 2>/dev/null || pwd)"
CONTRACT_DIR="${PROJECT_ROOT}/contracts"
DECISIONS_FILE="${PROJECT_ROOT}/docs/DECISIONS.md"

echo "[on-contract-violation] Kontrat dogrulama basliyor..."

if [[ ! -d "${CONTRACT_DIR}" ]]; then
  echo "[on-contract-violation] UYARI: contracts/ dizini bulunamadi, atlaniyor."
  exit 0
fi

# contract-validator.skill ciktisini bekler (JSON: {"violations": [...]})
VIOLATIONS_FILE="${PROJECT_ROOT}/.codex/.scratch/contract_violations.json"

if [[ -f "${VIOLATIONS_FILE}" ]]; then
  COUNT=$(grep -o '"code"' "${VIOLATIONS_FILE}" | wc -l | tr -d ' ')
  if [[ "${COUNT}" -gt 0 ]]; then
    echo "[on-contract-violation] ${COUNT} kontrat ihlali bulundu:" >&2
    cat "${VIOLATIONS_FILE}" >&2

    if ! grep -q "TODO-CONTRACT-EXCEPTION" "${DECISIONS_FILE}"; then
      echo "[on-contract-violation] HATA: docs/DECISIONS.md'de gerekce yok. Ajan durduruluyor." >&2
      exit 1
    fi
    echo "[on-contract-violation] Gerekce docs/DECISIONS.md'de bulundu, devam ediliyor."
  fi
fi

echo "[on-contract-violation] Kontrat ihlali yok."
exit 0

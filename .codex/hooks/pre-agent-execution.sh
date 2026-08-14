#!/usr/bin/env bash
# pre-agent-execution.sh
# Bir ajan calismaya baslamadan once bellek/baglam temizligi yapar ve
# gerekli referans belgelerin (ARCHITECT.md, PROMPT.md) mevcut oldugunu
# dogrular. Eksikse ajanin baslamasini engeller.

set -euo pipefail

PROJECT_ROOT="$(git rev-parse --show-toplevel 2>/dev/null || pwd)"
REQUIRED_DOCS=("docs/PROMPT.md" "docs/ARCHITECT.md" "docs/DECISIONS.md" "docs/DESIGN_SYSTEM.md" "docs/PROMPT_HISTORY.md")

echo "[pre-agent-execution] Baglam temizligi baslatiliyor..."

# Gecici/agent scratch dizinini temizle (varsa)
rm -rf "${PROJECT_ROOT}/.codex/.scratch"
mkdir -p "${PROJECT_ROOT}/.codex/.scratch"

# Zorunlu belgeler kontrolu
for doc in "${REQUIRED_DOCS[@]}"; do
  if [[ ! -f "${PROJECT_ROOT}/${doc}" ]]; then
    echo "[pre-agent-execution] HATA: ${doc} bulunamadi. Ajan durduruluyor." >&2
    exit 1
  fi
done

echo "[pre-agent-execution] Tum referans belgeler mevcut. Ajan calismaya hazir."
exit 0

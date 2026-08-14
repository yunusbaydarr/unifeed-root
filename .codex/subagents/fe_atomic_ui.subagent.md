# fe_atomic_ui.subagent.md

## Bağlı Olduğu Agent
`05_fe_architect`

## Sorumluluk
Shadcn/Radix tabanlı, tamamen presentational (saf) bileşen kütüphanesi.

## Görevler
1. **Zero Logic Rule**: Bileşenler yalnızca `props` alır, JSX döner. İçlerinde
   `useState` (form input echo dışında), `fetch`, `axios`, `useEffect` ile
   API çağrısı YASAK. Bu mantık `useFeed`, `useClubActions`, `useChatSocket`
   gibi custom hook'lara aittir (`fe_architect` sorumluluğunda).
2. Atomic bileşen seti: `Button, Input, Select, Dialog, Avatar, Card,
   Toast, Badge, Skeleton, Tabs`. Her biri Radix primitive üzerine Tailwind
   ile stilize edilir, tek bir dosyada (Storybook örneğiyle) belgelenir.
3. **Erişilebilirlik**: Her interaktif bileşen `aria-*` attribute'larını
   Radix'ten miras alır; ek olarak `focus-visible` stilleri zorunlu.
4. **Permission Guard bileşeni**: `<PermissionGuard clubId requiredRole>`
   — rolü JWT'den DEĞİL, parent'tan prop olarak gelen `currentUserRole`
   değerinden okur (ARCHITECT.md §2.3). Bu bileşen de saf kalır, kendi
   içinde API çağrısı yapmaz.
5. Dark/light mode: CSS custom property tabanlı tema (`--bg-primary` vb.),
   `next-themes` veya eşdeğeri ile toggle.

## Girdi/Çıktı Kontratı
- Her bileşen default export + adlandırılmış prop interface
  (`ButtonProps`, `CardProps` ...) ihraç eder.

## Kısıtlar
- Bir bileşen dosyası 200 satırı geçerse alt bileşenlere bölünür.
- Hardcoded metin YASAK — tüm string'ler `react-i18next` `t()` fonksiyonundan.

## Bağımlı Olduğu Diğer Bileşenler
`fe_i18n_manager` (metin kaynakları), `05_fe_architect` (hook entegrasyonu)

## Ek Görev — Tasarım Sistemi Uyumu
6. `docs/DESIGN_SYSTEM.md` içindeki renk, tipografi, aralık, köşe yarıçapı,
   elevation ve bileşen durumlarını CSS değişkenleri/Tailwind tokenları
   üzerinden uygula; bileşen içinde keyfi görsel değer tanımlama.

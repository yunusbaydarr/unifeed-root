# fe_i18n_manager.subagent.md

## Bağlı Olduğu Agent
`05_fe_architect`

## Sorumluluk
`react-i18next` dil eşleme ve dinamik dil değiştirme (TR/EN).

## Görevler
1. Dil dosyaları: `src/locales/tr/common.json`, `src/locales/en/common.json`
   — modül bazlı namespace (`auth.json`, `feed.json`, `club.json`, `chat.json`).
2. Hiçbir bileşende hardcoded metin YASAK (`fe_atomic_ui` bunu denetler);
   tüm metinler `t('namespace:key')` ile çekilir.
3. Dil değişimi `localStorage`'da **sadece tercih** olarak saklanır (token
   değil, hassas veri değil — bu saklamak serbesttir), sayfa yenilemesinde
   kalıcı olur.
4. Backend'den gelen RFC 7807 hata mesajları zaten `Accept-Language`'a göre
   lokalize geldiği için frontend bunları tekrar çevirmeye ÇALIŞMAZ, olduğu
   gibi gösterir.
5. Eksik çeviri key'i tespit edilirse (`i18n-key-extractor.skill` çıktısı),
   geliştirme ortamında console'da **warning** (production'da sessiz
   fallback `en`).

## Girdi/Çıktı Kontratı
- Export: `useTranslation()` re-export, `<LanguageSwitcher />` bileşeni

## Kısıtlar
- Namespace'ler arası key çakışması olamaz — `i18n-key-extractor.skill`
  bunu build adımında denetler.

## Bağımlı Olduğu Diğer Bileşenler
`be_i18n_manager` (backend mesaj kaynaklarıyla terminoloji tutarlılığı), `i18n-key-extractor.skill`

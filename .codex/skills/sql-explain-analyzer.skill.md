# sql-explain-analyzer.skill.md

## Amaç
Kritik sorguların (feed pagination, akıllı davet NOT EXISTS sorgusu,
kulüp/etkinlik listeleme) Postgres `EXPLAIN ANALYZE` çıktısını değerlendirip
Seq Scan / eksik index riskini tespit etmek.

## Kullanan Bileşenler
`be_feed_story` (cursor-based pagination sorgusu), `be_club_event` (akıllı
davet SQL'i), `02_db_architect` (index tasarımı doğrulama), `06_qa_validator`.

## Mantık
1. Belirlenen "kritik sorgu listesi" üzerinde `EXPLAIN (ANALYZE, BUFFERS)`
   çalıştırır (postgres-mcp üzerinden, `query-explain` scope'u ile).
2. Şu paternleri **kırmızı bayrak** olarak işaretler:
   - `Seq Scan` üzerinde >10k satırlık tabloda
   - `Nested Loop` içinde beklenmeyen büyük satır sayısı
   - Sort işleminin disk'e taşması (`external merge Disk`)
3. Feed pagination için özel kontrol: `cursor_created_at` + `cursor_id`
   kombinasyonunun composite index'i kullandığını doğrular
   (`idx_posts_created_at_id`), aksi halde ARCHITECT.md §1.6 ihlali olarak
   raporlar.
4. Akıllı davet `NOT EXISTS` sorgusu için: `club_members(club_id, user_id)`
   üzerindeki index'in kullanıldığını doğrular.

## Çıktı
`docs/QUERY_PERFORMANCE_REPORT.md` — her kritik sorgu için PASS/WARN/FAIL

## Kısıtlar
- Bu skill index eklemez, sadece önerir; index ekleme kararı
  `02_db_architect`'e aittir (yeni migration dosyası olarak).

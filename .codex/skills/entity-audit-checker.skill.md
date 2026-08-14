# entity-audit-checker.skill.md

## Amaç
Yeni oluşturulan her JPA entity'sinin ARCHITECT.md §1.1-1.2'deki
`BaseEntity`/`SoftDeletable` şablonuna uyduğunu statik olarak denetlemek.

## Kullanan Bileşenler
`02_db_architect` (entity üretirken kendi kendine doğrular),
`06_qa_validator` (PR onayı öncesi zorunlu kontrol).

## Mantık
1. `src/main/java/**/entity/*.java` altındaki her `@Entity` sınıfını tara.
2. Şu kontrolleri yap:
   - `extends BaseEntity` var mı?
   - `id` alanı `UUID` + `GenerationType.UUID` mi?
   - `deleted_at` alanına karşılık gelen alan var mı (soft-delete
     gerektiren entity'ler için)?
   - `@Version` (optimistic locking) alanı var mı?
   - Junction table ise (örn. `PostLikes`, `Follows`) ilgili `unique
     constraint` annotation'ı (`@Table(uniqueConstraints=...)`) mevcut mu?
3. `Club.status`, `Event.status` gibi enum alanlarının
   `ClubStatusTransitionValidator` gibi bir state machine sınıfı tarafından
   yönetildiğini (doğrudan setter çağrısı olmadığını) doğrula.

## Çıktı
```json
{ "entity": "Comment", "issues": [
  "Missing @Version field",
  "deleted_at not found — required for SoftDeletable"
]}
```

## Kısıtlar
- Bu skill kod değiştirmez, yalnızca raporlar. Düzeltme
  `02_db_architect`/`be_social_graph` tarafından yapılır.

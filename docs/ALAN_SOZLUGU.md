# UniFeed Alan Sözlüğü

## Ortak kurallar

- `UUID`: kaynak kimliği; zorunlu, benzersiz, istemci tarafından tahmin edilmez.
- `Instant/TIMESTAMPTZ`: UTC zaman damgası; zorunlu tarih alanları geçmiş/gelecek iş kuralına tabidir.
- `deleted_at`: soft-delete zamanı; boşsa aktif kayıt.
- `created_at`, `updated_at`: sistem tarafından üretilir; istemciden kabul edilmez.

## Kullanıcı ve profil

| Alan | Tip | Zorunlu | İş kuralı |
|---|---|---:|---|
| `users.id` | UUID | Evet | PK |
| `email` | string(320) | Evet | Benzersiz; doğrulama politikası ve geçerli e-posta biçimi |
| `password` | string | Kayıtta | Düz saklanmaz; BCrypt hash üretilir |
| `display_name` | string(100) | Evet | Boş olamaz |
| `bio` | string(500) | Hayır | Maksimum uzunluk |
| `department` | string(160) | Hayır | Profil metni |
| `academic_year` | string(40) | Hayır | Profil metni |
| `avatar_path` | path | Hayır | Yalnız izinli medya türü ve boyutu |
| `global_role` | enum | Evet | `SYSTEM_ADMIN` veya `STUDENT` |
| `is_email_verified` | boolean | Evet | Korumalı ürün akışları için doğrulama şartı |

## Gönderi, yorum ve hikâye

| Alan | Tip | Zorunlu | İş kuralı |
|---|---|---:|---|
| `posts.author_id` | UUID | Evet | Var olan kullanıcı |
| `posts.content` | text | Koşullu | Metin veya izinli medya içeriğinden biri bulunmalı |
| `like_count`, `comment_count` | integer | Evet | Negatif olamaz; trigger ile ilişki tablolarından güncellenir |
| `comments.content` | text | Evet | Boş olamaz; uzunluk limiti uygulanmalı |
| `comments.parent_comment_id` | UUID | Hayır | Aynı gönderi bağlamında yanıt |
| `stories.media_path` | path | Evet | İzinli görsel türü |
| `stories.expires_at` | timestamp | Evet | Aktif liste yalnızca `expires_at > now()` |

## Kulüp ve etkinlik

| Alan | Tip | Zorunlu | İş kuralı |
|---|---|---:|---|
| `clubs.name` | string(120) | Evet | Benzersiz; boş olamaz |
| `clubs.description` | text | Hayır | Kulüp açıklaması |
| `clubs.category` | string(80) | Hayır | Kategori etiketi |
| `clubs.status` | enum | Evet | `PENDING_APPROVAL`, `ACTIVE`, `SUSPENDED`, `CLOSED` |
| `club_members.role` | enum | Evet | `CLUB_ADMIN` veya `CLUB_MEMBER` |
| `events.club_id` | UUID | Evet | Var olan kulüp |
| `events.title` | string(160) | Evet | Boş olamaz |
| `events.starts_at` | timestamp | Evet | Etkinlik başlangıcı |
| `events.ends_at` | timestamp | Hayır | Varsa başlangıçtan önce olamaz |
| `events.location` | string(255) | Hayır | Fiziksel/online konum |
| `invitations.kind` | enum/string | Evet | `CLUB` veya `EVENT` |
| `invitations.status` | enum | Evet | `PENDING`, `ACCEPTED`, `DECLINED` |

## Mesaj ve bildirim

| Alan | Tip | Zorunlu | İş kuralı |
|---|---|---:|---|
| `thread_participants.thread_id` | UUID | Evet | Konuşma FK |
| `thread_participants.user_id` | UUID | Evet | Kullanıcı FK; bileşik PK |
| `direct_messages.content` | text | Evet | Boş olamaz; yalnız katılımcı gönderebilir |
| `notifications.recipient_id` | UUID | Evet | Bildirim alıcısı |
| `notifications.type` | enum | Evet | Tanımlı bildirim türlerinden biri |
| `notifications.payload` | JSONB | Evet | Hassas veri içermemeli; bağlamsal kaynak bilgisi |
| `notifications.read_at` | timestamp | Hayır | Boşsa okunmamış |

## API form alanları

`RegisterRequest`: `email` zorunlu, geçerli; `password` zorunlu ve parola politikasına uygun; `displayName` zorunlu. `ClubRequest` ve `EventRequest` zorunlu alanları başlık/ad ve etkinlik başlangıç zamanı olan JSON nesneleridir. `DirectThreadRequest.participantId` UUID olmalı ve oturum sahibiyle aynı olmamalıdır. Medya `file` ve `category` multipart alanlarından oluşur; MIME ve dosya boyutu backend'de doğrulanır.

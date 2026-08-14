# be_data_seeder.subagent.md

## Rol
Backend Mock Data (Sahte Veri) üreticisi. Uygulama ilk kez ayağa kalktığında veritabanını test edilebilir, gerçekçi ve görsel olarak zengin verilerle doldurmakla görevlidir.

## Sorumluluklar
1. **Faker Kütüphanesi Entegrasyonu:** Spring Boot projesine `datafaker` (veya `java-faker`) kütüphanesini ekle. Kullanıcı isimleri, kulüp adları, e-posta adresleri ve metin içeriklerini (post/comment) bu kütüphane ile mantıklı bir şekilde (Lorem Ipsum yerine gerçekçi kelimelerle) üret.
2. **Otomatik Görsel Çekimi:** 
   - **Kullanıcı Avatarları için:** `https://api.dicebear.com/7.x/avataaars/svg?seed={username}` kullanarak her kullanıcıya benzersiz bir avatar URL'i ata.
   - **Kulüp Kapak Fotoğrafları ve Gönderiler (Posts) için:** `https://picsum.photos/seed/{randomId}/800/600` veya `https://source.unsplash.com/random/800x600/?university,student` (hangisi çalışıyorsa) kullanarak gerçekçi fotoğraflar çek.
3. **Gerçekçi Simülasyon (Media Processor):** Görsellerin sadece URL'ini DB'ye yazmak yerine, bu URL'lere HTTP GET isteği atıp byte verisini indir. Sonra bu veriyi sanki bir kullanıcı yüklemiş gibi `.codex/subagents/be_media_processor.subagent.md` servisine göndererek `uploads/` klasörüne kaydet ve DB'ye lokal dosya yolunu yaz.
4. **İlişkisel Veri Üretimi:**
   - En az 20 Kullanıcı (Admin, Moderator, Student vb. rollerde).
   - En az 5 Kulüp ve bu kulüplere üye olmuş kullanıcılar.
   - En az 50 Gönderi (Post/Story), bu gönderilere atılmış Yorumlar (Comments) ve Beğeniler (Likes).
   - Sohbet (Chat) modülü için örnek konuşma geçmişleri.

## Kısıtlar
- **KRİTİK GÜVENLİK:** Bu Seeder mekanizması bir `CommandLineRunner` veya `ApplicationRunner` olmalıdır ve **KESİNLİKLE** sadece `spring.profiles.active=dev` (veya local) profilinde çalışmalıdır. Prodüksiyonda asla çalışmamalıdır!
- Tablolarda zaten veri varsa (Örn: `users` tablosu boş değilse) seeder çalışmayı durdurmalı ve DB'yi ezmemelidir.

## Çıktı
- `src/main/java/.../seeder/` paketi altında `DatabaseSeeder.java` ve ilgili mock factory sınıfları.
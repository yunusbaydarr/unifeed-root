# UniFeed

UniFeed, üniversite öğrencileri ve kulüpleri için geliştirilmiş Campus Hub uygulamasıdır. Kullanıcılar doğrulanmış hesaplarıyla sosyal akışı takip edebilir, kulüp ve etkinlikleri keşfedebilir, kulüplere katılabilir, etkinliklere kayıt olabilir, davet gönderebilir, bildirim alabilir ve doğrudan mesajlaşabilir.


## İçindekiler

- [Gereksinimler](#gereksinimler)
- [Hızlı kurulum](#hızlı-kurulum)
- [Servisler ve adresler](#servisler-ve-adresler)
- [Demo verisi](#demo-verisi)
- [Yerel geliştirme](#yerel-geliştirme)
- [Testler](#testler)
- [Sorun giderme](#sorun-giderme)
- [Dokümantasyon haritası](#dokümantasyon-haritası)

## Gereksinimler

1. Docker Desktop güncel bir sürüm.
2. Docker Desktop içindeki Linux containers ve WSL2 backend açık olmalı (Windows için).
3. Git.
4. Boş portlar: `5173`, `8080`, `5432`, `6379`, `9092`, `8025`, `1025`.
5. İlk image indirmeleri için internet bağlantısı.

Docker'ın çalıştığını doğrulayın:

```bash
docker --version
docker compose version
docker info
```

## Hızlı kurulum

### 1. Repoyu klonlayın

```bash
git clone <GITHUB_REPOSITORY_URL> unifeed-root
cd unifeed-root
```

### 2. Ortam dosyasını oluşturun

Linux/macOS/Git Bash:

```bash
cp .env.example .env
```

Windows PowerShell:

```powershell
Copy-Item .env.example .env
```

`.env` dosyasındaki `POSTGRES_PASSWORD` ve `JWT_SIGNING_KEY` değerlerini yerel kullanım için değiştirmeniz önerilir. `.env` `.gitignore` içindedir; bu dosya GitHub'a gönderilmemelidir.

### 3. Uygulamayı başlatın

```bash
docker compose --profile app up -d --build
```

İlk çalıştırmada PostgreSQL, Redis, Kafka, Mailpit, backend ve frontend image'ları indirilir. Backend açılırken Flyway migration'ları (`V1`–`V5`) otomatik uygulanır. Migration dosyaları [unifeed-backend/src/main/resources/db/migration](unifeed-backend/src/main/resources/db/migration) altındadır.

### 4. Durumu kontrol edin

```bash
docker compose --profile app ps
docker compose --profile app logs -f backend
```

Backend loglarında `Started UniFeedApplication` görüldüğünde tarayıcıdan [http://localhost:5173](http://localhost:5173) adresini açın.

## Servisler ve adresler

| Servis | Adres | Açıklama |
|---|---|---|
| Frontend | [http://localhost:5173](http://localhost:5173) | UniFeed web arayüzü |
| Backend API | [http://localhost:8080](http://localhost:8080) | Spring Boot API |
| OpenAPI/Swagger | [http://localhost:8080/swagger-ui/index.html](http://localhost:8080/swagger-ui/index.html) | API inceleme arayüzü |
| Mailpit | [http://localhost:8025](http://localhost:8025) | Geliştirme e-posta kutusu |
| SMTP | `localhost:1025` | Mailpit SMTP |
| PostgreSQL | `localhost:5432` | Veritabanı |
| Redis | `localhost:6379` | Cache/refresh token |
| Kafka | `localhost:9092` | Asenkron olaylar |

Frontend container içindeki Nginx; `/api`, `/uploads` ve `/ws` yollarını backend container'ına yönlendirir. Bu nedenle Docker ile çalıştırılan frontend için ayrıca frontend API URL ayarlamak gerekmez.

## Demo verisi

Varsayılan kurulumda demo seed kapalıdır. Önce kayıt ekranından yeni bir kullanıcı oluşturabilirsiniz. Demo kullanıcıları, kulüpleri, gönderileri, etkinlikleri ve konuşmaları otomatik oluşturmak isterseniz `.env` içine şunu ekleyin:

```dotenv
APP_SEED_ENABLED=true
```

Ardından backend'i yeniden oluşturun:

```bash
docker compose --profile app up -d --build backend
```

Demo kullanıcılarının parolası:

```text
Demo123!
```

Seed yalnızca `dev`/`local` profilinde çalışır ve aynı demo verisini ikinci kez eklemez. Demo hesapları üretim ortamında kullanılmamalıdır.

## Veritabanı ve kalıcı veriler

- PostgreSQL verisi `postgres-data` Docker volume'ünde tutulur.
- Redis verisi `redis-data` volume'ündedir.
- Kafka verisi `kafka-data` volume'ündedir.
- Yüklenen medya dosyaları `unifeed-backend/uploads` altında tutulur.

Migration'ların yeniden çalışması veya veritabanının sıfırlanması gerekirse bunun mevcut verileri sileceğini unutmayın. Geliştirme ortamını tamamen sıfırlamak için:

```bash
docker compose --profile app down -v
docker compose --profile app up -d --build
```

Bu komut yalnızca yerel Docker volume'lerini siler; üretim veritabanında çalıştırılmamalıdır.

## Yerel geliştirme

### Yalnızca Docker ile önerilen akış

```bash
docker compose --profile app up -d --build
docker compose --profile app logs -f backend
```

### Backend'i IDE/Maven ile çalıştırmak

PostgreSQL, Redis, Kafka ve Mailpit'i Docker'da bırakıp backend'i Java 21 ile çalıştırabilirsiniz:

```bash
cd unifeed-backend
mvn spring-boot:run -Dspring-boot.run.profiles=dev
```

Yerel backend için gerekli bağlantı bilgileri `application-dev.yml` içindeki localhost varsayılanlarıyla uyumludur. Maven wrapper projede bulunmadığından `mvn` ve JDK 21 kurulumu gerekir; değerlendirici için bu yöntem yerine Docker Compose önerilir.

### Frontend'i Vite ile çalıştırmak

```bash
cd unifeed-frontend
npm ci
npm run dev
```

Vite geliştirme sunucusu `http://localhost:5173` üzerinde çalışır ve `/api`, `/uploads`, `/ws` yollarını `http://localhost:8080` backend'ine proxy'ler. Node.js 22 önerilir.

## Testler

Frontend:

```bash
cd unifeed-frontend
npm test
npm run build
```

Playwright E2E:

```bash
npm run test:e2e
```

Backend:

```bash
cd unifeed-backend
mvn test
```

Testcontainers ile PostgreSQL, Flyway, JWT ve MockMvc kullanan entegrasyon testi:

```bash
mvn -Dtest=EventsApiIntegrationTest test
```

Bu testin gerçek PostgreSQL container'ı açabilmesi için test çalıştırılan makinede Docker daemon erişilebilir olmalıdır.

## Sorun giderme

### `POSTGRES_PASSWORD` veya `JWT_SIGNING_KEY` eksik

`.env.example` dosyasını `.env` olarak kopyaladığınızdan emin olun:

```bash
cp .env.example .env
```

PowerShell:

```powershell
Copy-Item .env.example .env
```

### Port zaten kullanımda

Hangi işlemin portu kullandığını kontrol edin. Alternatif port vermek için `.env` içinde `FRONTEND_PORT`, `BACKEND_PORT`, `POSTGRES_PORT`, `REDIS_PORT`, `KAFKA_HOST_PORT` veya `MAILPIT_UI_PORT` değerlerini değiştirin. Backend'in CORS izinlerinde frontend adresi değişirse `FRONTEND_ALLOWED_ORIGINS` değerini de güncelleyin.

### Frontend açılıyor fakat API çağrıları başarısız

```bash
docker compose --profile app ps
docker compose --profile app logs backend
docker compose --profile app logs frontend
```

Backend'in `Started UniFeedApplication` mesajını verdiğini ve PostgreSQL/Redis/Kafka health durumlarının `healthy` olduğunu kontrol edin. Browser cache'ini temizleyip `Ctrl+F5` ile yenileyin.

### Migration hatası

```bash
docker compose logs postgres
docker compose --profile app logs backend
```

Migration dosyalarını çalışan veritabanında elle düzenlemeyin. Geliştirme verisi silinebiliyorsa volume'leri kaldırıp yeniden başlatın; önemli veri varsa önce yedek alın.

### Docker Desktop erişim hatası (Windows)

Docker Desktop'ı başlatın, Linux containers/WSL2 backend'i etkinleştirin ve PowerShell'i yeniden açın. `docker info` çalışmadan Compose komutlarını çalıştırmayın.

### E-posta gelmiyor

Geliştirme ortamında e-postalar gerçek alıcıya gönderilmez; [Mailpit](http://localhost:8025) arayüzünden görülür. Backend'in `MAIL_HOST=mailpit` ve `MAIL_PORT=1025` ile çalıştığını kontrol edin.

## Dokümantasyon haritası

| Dosya | İçerik |
|---|---|
| [docs/ANALIZ_DOKUMANI.md](docs/ANALIZ_DOKUMANI.md) | Problem, kapsam, kullanıcı hikâyeleri, kabul kriterleri |
| [docs/TEKNIK_DOKUMAN.md](docs/TEKNIK_DOKUMAN.md) | Mimari, veri modeli, API sözleşmeleri, AI kararları |
| [docs/ALAN_SOZLUGU.md](docs/ALAN_SOZLUGU.md) | Alan tipleri, zorunluluklar ve iş kuralları |
| [docs/GIVEN_WHEN_THEN.md](docs/GIVEN_WHEN_THEN.md) | BDD kabul senaryoları |
| [docs/ARCHITECT.md](docs/ARCHITECT.md) | Mimari ve kodlama standartları |
| [docs/QA_REPORT.md](docs/QA_REPORT.md) | QA ve test değerlendirmesi |
| [docs/INTEGRATION_REPORT.md](docs/INTEGRATION_REPORT.md) | Entegrasyon durumu |
| [docs/asyncapi.yaml](docs/asyncapi.yaml) | WebSocket/AsyncAPI sözleşmesi |
| [docs/ui-mocks](docs/ui-mocks) | Ekran tasarım referansları |

## Mimari özet

```text
React/Vite/Nginx
        │ HTTPS/JSON, STOMP
        ▼
Spring Boot API ─── Redis
        │
        ├── PostgreSQL + Flyway
        ├── Kafka (email/system/audit events)
        ├── Mailpit/SMTP
        └── uploads/
```

Backend endpoint'leri `/api/v1` altında, WebSocket bağlantısı `/ws` altında çalışır. İş kuralları backend'de denetlenir; frontend yalnızca kullanıcı arayüzüdür.

## Katkı ve teslim kontrol listesi

Bir değişiklik göndermeden önce:

```bash
cd unifeed-frontend && npm test && npm run build
cd ../unifeed-backend && mvn test
```

Ardından temiz bir Docker kurulumunu doğrulayın:

```bash
docker compose --profile app down -v
docker compose --profile app up -d --build
docker compose --profile app ps
```

`.env`, `uploads/`, `target/`, `node_modules/` ve `dist/` Git'e gönderilmemelidir. Yeni migration, API veya kullanıcı akışı eklenirse ilgili `docs/` dosyaları da güncellenmelidir.



# UniFeed Analiz Dokümanı

| Alan | Bilgi |
| --- | --- |
| Proje | UniFeed – Campus Hub |
| Doküman türü | Analiz Dokümanı / Yazılım Gereksinimleri Özeti |
| Sürüm | 1.0 |
| Hedef kitle | Üniversite öğrencileri, Üniversite Akademisyenleri, Üniversitenin mezun öğrencileri  |

## 1. Dokümanın amacı

Bu doküman, UniFeed'in çözmeyi amaçladığı problemi, ürün sınırlarını, hedef kullanıcılarını ve doğrulanabilir işlevsel gereksinimlerini tanımlar. Amaç; geliştirme, test ve proje teslim süreçlerinde ortak bir referans oluşturmaktır.

UniFeed, üniversite topluluğundaki öğrencilerin ve kulüplerin duyuru, etkinlik, topluluk ve bire bir iletişim ihtiyaçlarını tek bir dijital ortamda bir araya getiren kampüs odaklı bir sosyal platformdur.

## 2. Problem tanımı

Üniversite kampüslerinde kulüp duyuruları, etkinlikler, öğrenci toplulukları ve bireysel iletişim genellikle farklı ve dağınık kanallardan yürütülmektedir. Sosyal medya grupları, mesajlaşma uygulamaları, fiziksel panolar ve kişisel ağlar birlikte kullanıldığında aşağıdaki sorunlar ortaya çıkar:

- Öğrenciler ilgilerini çeken kulüp ve etkinlikleri zamanında keşfedemeyebilir.
- Kulüpler, duyuru ve etkinliklerini hedef öğrencilere düzenli biçimde ulaştırmakta zorlanır.
- Bir etkinliğe katılım, davet, üyelik ve etkinlik bilgileri tek bir yerde takip edilemez.
- Öğrenciler arasında güvenilir, kampüs bağlamına ait ve kimliği doğrulanmış bir iletişim alanı eksiktir.
- İçerik, mesaj ve bildirim akışları farklı uygulamalara dağıldığı için kullanıcı deneyimi parçalanır.

UniFeed bu parçalanmış yapıyı; doğrulanmış kullanıcı hesapları, sosyal akış, kulüp/etkinlik yönetimi, arama-keşif, bildirim ve mesajlaşma bileşenleri üzerinden bütünleştirmeyi hedefler.

## 3. Çözüm yaklaşımı ve ürün vizyonu

UniFeed, kampüs yaşamını dijital olarak destekleyen bir "Campus Hub" olarak konumlanır. Sistem, öğrencilerin içerik paylaşmasını, kullanıcı ve kulüp keşfetmesini, topluluklara katılmasını, yaklaşan etkinlikleri görüntülemesini ve diğer kullanıcılarla doğrudan iletişim kurmasını sağlar.

Ürünün temel değer önerisi şudur:

> Kampüs içindeki sosyal, akademik ve kulüp faaliyetlerini; güvenilir kimlik doğrulama ve sade bir kullanıcı deneyimiyle tek merkezde erişilebilir hâle getirmek.

### 3.1. Hedefler

- Öğrencilerin kampüs topluluklarına katılımını kolaylaştırmak.
- Kulüp ve etkinlik görünürlüğünü artırmak.
- Kullanıcıların güncel içerik ve etkinlikleri tek noktadan takip etmesini sağlamak.
- Takip, beğeni, yorum, bildirim ve mesajlaşma aracılığıyla etkileşimi desteklemek.
- Türkçe ve İngilizce arayüz desteği ile kullanılabilirliği artırmak.
- Rol ve sahiplik denetimleriyle kullanıcı verisini ve kulüp yönetimini korumak.

### 3.2. Başarı ölçütleri

Proje teslimi açısından sistemin başarılı kabul edilmesi için aşağıdaki gözlemlenebilir sonuçlar hedeflenir:

- Yetkili bir kullanıcı kayıt olup oturum açabilmeli ve oturumunu güvenli biçimde yenileyebilmelidir.
- Kullanıcı, gönderi yayınlayıp sosyal akışta görebilmelidir.
- Kullanıcı; kulüp keşfedebilmeli, bir kulübe katılabilmeli ve yaklaşan etkinlikleri görüntüleyebilmelidir.
- Kulüp yöneticisi, kulüp ve etkinlik oluşturabilmeli; uygun kullanıcıları davet edebilmelidir.
- Davet edilen kullanıcı, daveti kabul veya reddedebilmelidir.
- Kullanıcı, başka bir kullanıcıyla doğrudan konuşma başlatabilmelidir.
- Takip, beğeni, yorum, davet ve mesajlaşma gibi işlemler ilgili bildirim akışına yansımalıdır.

## 4. Paydaşlar ve kullanıcı rolleri

| Rol | Tanım | Temel ihtiyaç |
| --- | --- | --- |
| Öğrenci / standart kullanıcı | Kampüs topluluğuna katılan doğrulanmış kullanıcı | İçerik, kulüp ve etkinlik keşfi; iletişim; kişisel profil yönetimi |
| Kulüp üyesi | Bir veya birden fazla kulübe katılmış kullanıcı | Kulüp içeriklerine, üyelik ve etkinlik bilgilerine erişim |
| Kulüp yöneticisi | Kulüp oluşturmuş veya yönetici yetkisi bulunan kullanıcı | Kulübü, üyeleri, davetleri ve etkinlikleri yönetme |
| Etkinlik katılımcısı | Bir etkinliğe katılan veya davet edilen kullanıcı | Etkinlik bilgisi, katılım durumu ve bildirim takibi |
| Sistem yöneticisi / geliştirici | Uygulamanın teknik işletiminden sorumlu rol | Sistem sağlığı, yapılandırma, veri bütünlüğü ve güvenlik yönetimi |

## 5. Kapsam

### 5.1. Kapsam içi işlevler

#### A. Hesap, oturum ve profil yönetimi

- E-posta tabanlı kullanıcı kaydı ve giriş.
- Üniversite e-posta alanı veya tanımlı doğrulama kuralı ile uygun kullanıcı kaydı.
- E-posta doğrulama / OTP akışı ve şifre sıfırlama.
- JWT tabanlı erişim oturumu ve yenileme mekanizması.
- Kullanıcı profilini görüntüleme ve güncelleme.
- Avatar / profil görseli yükleme.
- Kullanıcıları takip etme veya takibi bırakma.
- Profilde takipçi, takip edilen ve üye olunan kulüp sayılarını görüntüleme; ilgili listeleri modal içinde açma.

#### B. Sosyal akış ve içerik etkileşimi

- Metin ve desteklenen medya içeren gönderi oluşturma, düzenleme ve silme.
- Ana akışta gönderileri listeleme.
- Gönderileri beğenme / beğeniyi geri alma.
- Gönderilere yorum yapma ve yorumları görüntüleme.
- Gönderi paylaşımının kayda alınması.
- Hikâye (story) oluşturma, görüntüleme ve süreli içerik yaşam döngüsü.
- Kullanıcıya ait gönderileri profil sayfasında listeleme.

#### C. Kulüpler ve etkinlikler

- Kulüpleri listeleme, arama ve ayrıntılarını görüntüleme.
- Kulüp oluşturma ve kulüp bilgilerini yönetme.
- Kulübe katılma veya kulüpten ayrılma.
- Kulüp üyelerini ve üyelik rollerini yönetme.
- Etkinlik oluşturma, düzenleme, listeleme ve detay görüntüleme.
- Kullanıcının üye olduğu kulüplere ait yaklaşan etkinlikleri, yalnızca gelecek tarihli olacak biçimde görüntüleme.
- Kulüp yöneticisinin kulübe veya etkinliğe kullanıcı davet edebilmesi.
- Kullanıcının kulüp/etkinlik davetini kabul veya reddetmesi.

#### D. Keşif, arama ve bildirimler

- Kullanıcı, kulüp ve etkinlikler arasında arama yapma.
- Keşfet sayfasında mevcut arama tasarımını kullanarak sonuçlara erişme.
- Beğeni, yorum, takip, davet ve diğer önemli etkileşimler için bildirim oluşturma.
- Bildirimleri listeleme, okunmuş olarak işaretleme ve silme.

#### E. Mesajlaşma

- Kullanıcı profilinden başka bir kullanıcıyla doğrudan konuşma başlatma.
- Mevcut doğrudan konuşmayı tekrar kullanma; aynı iki kullanıcı için gereksiz çoğul konuşma oluşturmama.
- Konuşma listesi, mesaj geçmişi ve yeni mesaj gönderimi.
- Gerçek zamanlı mesaj güncellemesi için WebSocket/STOMP altyapısı.

#### F. Arayüz, kullanılabilirlik ve teknik işletim

- Responsive web arayüzü.
- Türkçe ve İngilizce kullanıcı arayüzü metinleri.
- Kullanıcıya anlamlı yükleniyor, boş durum ve hata mesajları gösterme.
- Docker Compose ile frontend, backend, PostgreSQL, Redis ve gerekli servislerin birlikte çalıştırılması.
- Yerel dosya depolama üzerinden medya sunumu.

### 5.2. Kapsam dışı işlevler

Aşağıdaki maddeler bu teslimin temel kapsamı dışındadır; daha sonraki sürümler için değerlendirilebilir:

- iOS veya Android için yerel (native) mobil uygulama geliştirme.
- Ödeme alma, bilet satışı, ücretli üyelik veya e-ticaret süreçleri.
- Üniversite bilgi sistemiyle ders, not, yoklama veya resmi öğrenci kaydı entegrasyonu.
- Gelişmiş yapay zekâ öneri motoru ve kişiselleştirilmiş sıralama algoritması.
- Dış bulut nesne depolama (S3 vb.), CDN ve kurumsal medya işleme hattı.
- Tam kapsamlı içerik moderasyon paneli, hukuki denetim iş akışları ve otomatik zararlı içerik sınıflandırması.
- Çok kiracılı (multi-tenant), birden çok üniversiteyi merkezi olarak yöneten kurumsal yapı.
- Ödeme, takvim veya üçüncü taraf sosyal ağlarla zorunlu entegrasyon.
- Sesli/görüntülü görüşme, canlı yayın ve uçtan uca şifreli mesajlaşma.
- Gelişmiş analitik, pazarlama otomasyonu ve reklam yönetimi.

## 6. İş kuralları

1. Sistemde işlem yapan kullanıcı, geçerli bir oturumla doğrulanmış olmalıdır.
2. Kullanıcı yalnızca kendi profilini, kendi gönderisini veya kendisine ait yetkili kaynakları düzenleyebilir/silebilir.
3. Kulüp yönetim işlemleri yalnızca kulüp sahibi ya da yetkilendirilmiş kulüp yöneticisi tarafından yapılabilir.
4. Etkinlik oluşturma ve düzenleme yetkisi ilgili kulüp yönetim yetkisine bağlıdır.
5. Bir kullanıcı, aynı kulübe birden fazla kez üye olamaz.
6. Bir kullanıcı, aynı etkinliğe birden fazla kez katılamaz.
7. Davetler beklemede, kabul edildi veya reddedildi durumlarından birinde tutulur; kullanıcı bekleyen davetine karar verebilir.
8. Yaklaşan etkinlikler, mevcut zamandan sonraki başlangıç tarihine sahip etkinliklerdir. Geçmiş etkinlikler bu listede gösterilmez.
9. Doğrudan mesajlaşmada kullanıcı yalnızca katılımcısı olduğu konuşmaların mesajlarını görebilir ve bu konuşmalara mesaj gönderebilir.
10. Arama sonucu, kullanıcının yetkili olduğu ve sistemde görünür olan veri ile sınırlı olmalıdır.

## 7. Kullanıcı hikâyeleri ve kabul kriterleri

Kabul kriterleri, test edilebilir davranışı tanımlamak için "Verildiğinde / Ne zaman / O zaman" biçiminde yazılmıştır.

### 7.1. Hesap ve kimlik doğrulama

#### US-01 — Kayıt olma

**Kullanıcı hikâyesi:** Kampüs topluluğuna katılmak isteyen bir öğrenci olarak, e-posta ve şifre bilgilerimle hesap oluşturmak istiyorum; böylece UniFeed özelliklerine erişebileyim.

**Kabul kriterleri:**

- Verildiğinde geçerli biçimde bir ad, soyad, e-posta ve yeterli güvenlikte şifre girildiğinde, kayıt isteği başarıyla alınmalıdır.
- Ne zaman kayıt e-postası doğrulama gerektiriyorsa, kullanıcı doğrulama tamamlanmadan korumalı alanlara erişememelidir.
- O zaman aynı e-posta ile ikinci kez kayıt olunmaya çalışıldığında sistem açıklayıcı bir hata döndürmelidir.
- O zaman geçersiz e-posta, eksik alan veya geçersiz şifre için kullanıcıya uygun doğrulama mesajı gösterilmelidir.

#### US-02 — Oturum açma ve yenileme

**Kullanıcı hikâyesi:** Kayıtlı bir kullanıcı olarak, güvenli biçimde giriş yapmak ve oturumum süresi dolduğunda yeniden giriş yapmadan devam etmek istiyorum.

**Kabul kriterleri:**

- Verildiğinde doğru e-posta ve şifre ile giriş yapıldığında kullanıcıya geçerli erişim oturumu sağlanmalıdır.
- Ne zaman hatalı kimlik bilgileri kullanılırsa, sistem kullanıcı hesabı hakkında gereksiz bilgi sızdırmadan giriş hatası göstermelidir.
- Ne zaman erişim belirteci süresi dolarsa ve geçerli yenileme belirteci mevcutsa, istemci oturumu yenileyebilmelidir.
- Ne zaman kullanıcı çıkış yaparsa, yenileme oturumu geçersizleştirilmeli ve korumalı istekler yetkisiz kalmalıdır.



### 7.2. Profil ve sosyal ağ

#### US-04 — Profil görüntüleme ve güncelleme

**Kullanıcı hikâyesi:** Kullanıcı olarak, profilimi görüntülemek ve bilgilerini güncellemek istiyorum; böylece diğer kullanıcılar beni tanıyabilsin.

**Kabul kriterleri:**

- Verildiğinde kullanıcı kendi profil sayfasını açtığında ad, kullanıcı adı, biyografi, bölüm, avatar ve sayaç bilgileri görüntülenmelidir.
- Ne zaman kullanıcı profilinde izinli alanları güncellerse, değişiklikler kalıcı olarak saklanmalı ve ekran yenilendiğinde görünmelidir.
- O zaman kullanıcı başka bir kullanıcının profilini görüntüleyebilir; ancak onun profil verisini değiştiremez.
- O zaman geçersiz avatar dosyası veya desteklenmeyen medya türü reddedilmelidir.

#### US-05 — Takip etme ve sosyal listeler

**Kullanıcı hikâyesi:** Kullanıcı olarak, ilgilendiğim kişileri takip etmek ve takip ilişkilerini incelemek istiyorum.

**Kabul kriterleri:**

- Verildiğinde başka bir kullanıcının profilini görüntülediğimde, takip durumuma uygun "Takip Et" veya "Takibi Bırak" eylemi görünmelidir.
- Ne zaman "Takip Et" eylemini seçersem, takip ilişkisi oluşturulmalı ve takipçi/takip sayaçları güncellenmelidir.
- Ne zaman profil üzerindeki "Takipçi" sayacına tıklarsam, ilgili kullanıcıyı takip eden kişilerin listesi modal pencerede açılmalıdır.
- Ne zaman "Takip" sayacına tıklarsam, ilgili kullanıcının takip ettiği kişilerin listesi modal pencerede açılmalıdır.
- Ne zaman "Klüp" sayacına tıklarsam, kullanıcının üye olduğu kulüplerin listesi modal pencerede açılmalıdır.

#### US-06 — Profil üzerinden mesaj başlatma

**Kullanıcı hikâyesi:** Bir kullanıcı olarak, başka bir kullanıcının profilinden doğrudan mesajlaşma başlatmak istiyorum.

**Kabul kriterleri:**

- Verildiğinde başka bir kullanıcının profilini görüntülediğimde, takip butonunun yanında "Mesaj Gönder" butonu görünmelidir.
- Ne zaman bu butona tıklarsam, sistem iki kullanıcı arasındaki mevcut doğrudan konuşmayı bulmalı; yoksa oluşturmalıdır.
- O zaman kullanıcı mesajlaşma ekranına açık ilgili konuşma ile yönlendirilmelidir.
- O zaman kullanıcı kendi profili için kendisine doğrudan mesaj başlatma eylemi görmemelidir.

### 7.3. Gönderiler, yorumlar ve hikâyeler

#### US-07 — Gönderi yayınlama

**Kullanıcı hikâyesi:** Kullanıcı olarak, kampüsle ilgili düşünce, duyuru veya görsel paylaşmak istiyorum.

**Kabul kriterleri:**

- Verildiğinde oturum açmış kullanıcı geçerli metin ve isteğe bağlı medya ile gönderi oluşturduğunda, gönderi başarıyla kaydedilmelidir.
- O zaman yeni gönderi ana akışta ve kullanıcının profilindeki gönderiler arasında görünmelidir.
- Ne zaman kullanıcı kendi gönderisini düzenler veya silerse, değişiklik yalnızca ilgili gönderiye uygulanmalıdır.
- O zaman başka bir kullanıcı, gönderi sahibi olmadığı bir gönderiyi düzenleyemez veya silemez.

#### US-08 — Beğeni, yorum ve paylaşım

**Kullanıcı hikâyesi:** Kullanıcı olarak, gönderilerle etkileşime girmek istiyorum; böylece içerik sahiplerine geri bildirim verebileyim.

**Kabul kriterleri:**

- Ne zaman bir gönderiyi beğenirsem, beğeni durumu ve sayaç güncellenmelidir.
- Ne zaman beğenimi geri alırsam, beğeni durumu geri dönmelidir.
- Ne zaman geçerli metinle yorum yaparsam, yorum ilgili gönderi altında görünmelidir.
- O zaman gönderi sahibi, kendi içeriğine yapılan uygun etkileşimler için bildirim almalıdır.
- O zaman aynı kullanıcı aynı gönderiyi tekrar beğenerek mükerrer kayıt oluşturmamalıdır.

#### US-09 — Hikâye kullanımı

**Kullanıcı hikâyesi:** Kullanıcı olarak, geçici kampüs içeriklerini hikâye biçiminde paylaşmak ve görmek istiyorum.

**Kabul kriterleri:**

- Verildiğinde geçerli hikâye içeriği oluşturulduğunda, hikâye görüntülenebilir olmalıdır.
- Ne zaman bir hikâye görüntülenirse, görüntüleme kaydı ilgili kullanıcı için tutulmalıdır.
- O zaman süresi geçmiş hikâyeler aktif hikâye listesinde gösterilmemelidir.

### 7.4. Kulüpler ve etkinlikler

#### US-10 — Kulüp keşfi ve üyelik

**Kullanıcı hikâyesi:** Öğrenci olarak, ilgime uygun kulüpleri bulmak ve katılmak istiyorum.

**Kabul kriterleri:**

- Verildiğinde "Klüp ve Etkinlikler" sayfası açıldığında, erişilebilir kulüp kartları listelenmelidir.
- Ne zaman kullanıcı bir kulübün detayını açarsa, kulüp adı, kategorisi, açıklaması, üye sayısı ve gerekli üyelik bilgileri görünmelidir.
- Ne zaman kullanıcı üyesi olmadığı kulübe katılırsa, üyelik kaydı oluşmalı ve kulübün üye sayısı güncellenmelidir.
- Ne zaman kullanıcı üyesi olduğu kulüpten ayrılırsa, üyelik kaldırılmalıdır; kulübün en az bir yöneticisi kalmasını engelleyen kurallar korunmalıdır.
- O zaman aynı kullanıcı için aynı kulübe tekrar eden üyelik kaydı oluşmamalıdır.

#### US-11 — Kulüp oluşturma ve yönetme

**Kullanıcı hikâyesi:** Yetkili kullanıcı olarak, bir kampüs kulübü oluşturmak ve yönetmek istiyorum.

**Kabul kriterleri:**

- Verildiğinde zorunlu kulüp bilgileriyle oluşturma işlemi yapıldığında, yeni kulüp ve ilk yönetici üyelik kaydı oluşturulmalıdır.
- Ne zaman kulüp yöneticisi kulüp bilgilerini güncellerse, sadece yetkili kullanıcı bu işlemi yapabilmelidir.
- O zaman yetkisiz kullanıcı kulüp ayarlarını, üyeleri veya yönetici rollerini değiştirememelidir.

#### US-12 — Etkinlik oluşturma ve katılım

**Kullanıcı hikâyesi:** Kulüp yöneticisi olarak, kulübüm için etkinlik düzenlemek; öğrenci olarak da etkinliğe katılmak istiyorum.

**Kabul kriterleri:**

- Verildiğinde yetkili kulüp yöneticisi geçerli başlık, açıklama, tarih, saat ve konumla etkinlik oluşturduğunda etkinlik kaydedilmelidir.
- O zaman bitiş zamanı başlangıç zamanından önce olan etkinlik kaydı kabul edilmemelidir.
- Ne zaman kullanıcı henüz başlamamış bir etkinliğe katılırsa, katılım kaydı oluşturulmalıdır.
- Ne zaman kullanıcı katılımını iptal ederse, katılım kaydı kaldırılmalıdır.
- O zaman aynı kullanıcı ve etkinlik için birden fazla katılım kaydı oluşmamalıdır.

#### US-13 — Yaklaşan etkinlikleri görüntüleme

**Kullanıcı hikâyesi:** Kulüp üyesi olarak, bağlı olduğum kulüplerin yaklaşan etkinliklerini görmek istiyorum.

**Kabul kriterleri:**

- Verildiğinde kullanıcı "Klüp ve Etkinlikler" sayfasını açtığında, "Yaklaşan etkinlikler" bölümü görünmelidir.
- Ne zaman kullanıcının üye olduğu kulüplerde başlangıç tarihi gelecekte olan etkinlikler varsa, bu etkinlikler listelenmelidir.
- O zaman başlangıç tarihi geçmişte kalan etkinlikler listelenmemelidir.
- O zaman kullanıcı hiçbir kulübe üye değilse veya uygun etkinlik yoksa, boş durum mesajı gösterilmelidir; ekran bozuk ya da boş bir kart alanı olarak kalmamalıdır.

#### US-14 — Kulüp ve etkinlik davetleri

**Kullanıcı hikâyesi:** Kulüp yöneticisi olarak uygun kullanıcıları davet etmek; davet edilen kullanıcı olarak da davete karar vermek istiyorum.

**Kabul kriterleri:**

- Verildiğinde kulüp yöneticisi davet seçicisini açtığında, davet için uygun kullanıcılar listelenmelidir.
- Ne zaman yönetici uygun bir kullanıcıyı kulübe veya etkinliğe davet ederse, bekleyen davet oluşturulmalı ve davetli kullanıcıya bildirim gönderilmelidir.
- Ne zaman davetli kullanıcı daveti kabul ederse, ilgili kulüp üyeliği veya etkinlik katılımı oluşturulmalıdır.
- Ne zaman davetli kullanıcı daveti reddederse, üyelik/katılım oluşturulmamalı ve davet durumu reddedildi olmalıdır.
- O zaman kullanıcı zaten üye olduğu kulübe veya katıldığı etkinliğe tekrar davet edilmemelidir.

### 7.5. Keşif, arama ve bildirimler

#### US-15 — Arama ve keşif

**Kullanıcı hikâyesi:** Kullanıcı olarak, kişi, kulüp veya etkinlik aramak istiyorum; böylece ilgilendiğim kampüs kaynaklarına hızlıca erişebileyim.

**Kabul kriterleri:**

- Verildiğinde kullanıcı arama kutusuna bir arama ifadesi girerse, kullanıcı, kulüp ve etkinlik eşleşmeleri gösterilmelidir.
- O zaman boş arama ifadesi sistemin gereksiz genişlikte sorgu çalıştırmasına neden olmamalıdır.
- Ne zaman bir sonuç seçilirse, kullanıcı ilgili profil, kulüp veya etkinlik detayına yönlendirilmelidir.
- O zaman sonuçta hassas veya erişim izni olmayan bilgi gösterilmemelidir.

#### US-16 — Bildirim yönetimi

**Kullanıcı hikâyesi:** Kullanıcı olarak, benimle ilgili önemli işlemlerden haberdar olmak istiyorum.

**Kabul kriterleri:**

- Ne zaman kullanıcıyı ilgilendiren takip, beğeni, yorum, davet veya mesaj olayı gerçekleşirse, uygun bildirim oluşturulmalıdır.
- Verildiğinde kullanıcı bildirim ekranını açtığında, bildirimler güncelten eskiye doğru listelenmelidir.
- Ne zaman kullanıcı bildirimi okursa, bildirim okunmuş duruma geçmelidir.
- Ne zaman davet bildirimiyle ilişkili kabul/red işlemi yapılırsa, işlem sonucu arayüzde anlaşılır biçimde görünmelidir.

### 7.6. Mesajlaşma

#### US-17 — Doğrudan mesajlaşma

**Kullanıcı hikâyesi:** Kullanıcı olarak, başka bir kullanıcıyla özel konuşma yapabilmek istiyorum.

**Kabul kriterleri:**

- Verildiğinde kullanıcı doğrudan konuşmanın katılımcısı olduğunda, konuşmayı ve mesaj geçmişini görebilmelidir.
- Ne zaman kullanıcı geçerli mesaj içeriği gönderirse, mesaj kaydedilmeli ve karşı tarafa iletilmelidir.
- O zaman konuşma katılımcısı olmayan kullanıcı konuşmanın mesajlarını okuyamamalı veya mesaj gönderememelidir.
- Ne zaman yeni mesaj geldiğinde, konuşma listesi ve ilgili ekran uygun şekilde güncellenmelidir.
- O zaman aynı iki kullanıcı için tekrar tekrar başlatılan doğrudan konuşma isteği mevcut konuşmayı kullanmalıdır.

## 8. Fonksiyonel olmayan gereksinimler

### 8.1. Güvenlik

- Korumalı API uçları doğrulanmış kullanıcı oturumu gerektirmelidir.
- Kaynak sahipliği ve kulüp yöneticisi yetkisi, yalnızca arayüzde değil backend tarafında da doğrulanmalıdır.
- Şifreler düz metin olarak saklanmamalı; güçlü tek yönlü parola özeti kullanılmalıdır.
- Gizli anahtarlar ve üretim ortamı erişim bilgileri kaynak kodda yer almamalı; ortam değişkenleri veya güvenli gizli bilgi yönetimiyle sağlanmalıdır.
- Girdi verileri sunucu tarafında doğrulanmalı; veritabanı sorguları parametreli sorgularla çalıştırılmalıdır.
- Tarayıcı tarafında kullanıcı içeriği varsayılan olarak metin olarak işlenmeli ve HTML enjeksiyonuna izin verilmemelidir.
- CORS ayarları izin verilen istemci kökenleriyle sınırlandırılmalıdır.

### 8.2. Performans

- Sık kullanılan listeleme uçları sayfalama veya güvenli istek sınırları ile çalışmalıdır.
- Kulüp üyelikleri, etkinlik tarihleri, takip ilişkileri ve bildirim sorgularında uygun veritabanı indeksleri kullanılmalıdır.
- Yaklaşan etkinlik sorguları yalnızca gerekli tarih aralığını ve kullanıcının ilişkili kulüplerini dikkate almalıdır.
- Tekrarlanan veri erişimleri, gereksiz N+1 sorgu üretmeyecek biçimde tasarlanmalıdır.

### 8.3. Kullanılabilirlik ve erişilebilirlik

- Arayüz masaüstü ve dar ekranlarda kullanılabilir olmalıdır.
- Yüklenme, hata ve boş durumlar kullanıcıya açık metinlerle bildirilmelidir.
- Butonlar, form alanları ve modal pencereler klavye ile erişilebilir ve anlamlı etiketlere sahip olmalıdır.
- Tarih/saat ve metinler seçili dile göre tutarlı biçimde gösterilmelidir.

### 8.4. Güvenilirlik ve işletilebilirlik

- Veritabanı şeması sürümlü migration dosyaları ile oluşturulmalıdır.
- Uygulama servisleri Docker Compose üzerinden tekrarlanabilir biçimde ayağa kaldırılabilmelidir.
- Sağlık kontrolü, anlamlı hata kayıtları ve temel gözlemlenebilirlik mekanizmaları sağlanmalıdır.
- Geliştirme, test ve üretim yapılandırmaları birbirinden ayrılmalıdır.

## 9. Veri gereksinimleri

Sistemin temel veri varlıkları aşağıdaki gibidir:

| Varlık | Örnek temel veriler | İlişkiler |
| --- | --- | --- |
| Kullanıcı | kimlik, ad, kullanıcı adı, e-posta, profil, bölüm | gönderi, takip, üyelik, mesaj, bildirim |
| Gönderi | yazar, metin, medya, tarih, sayaçlar | yorum, beğeni, paylaşım |
| Yorum | gönderi, yazar, metin, tarih | gönderiye bağlıdır |
| Hikâye | yazar, medya, bitiş tarihi | görüntüleme kayıtları |
| Kulüp | ad, kategori, açıklama, görsel, oluşturucu | üyeler, etkinlikler, davetler |
| Kulüp üyeliği | kulüp, kullanıcı, rol, katılım tarihi | kullanıcı-kulüp çoktan çoğa ilişkisi |
| Etkinlik | kulüp, başlık, açıklama, başlangıç/bitiş, konum | katılımcılar, davetler |
| Etkinlik katılımı | etkinlik, kullanıcı, katılım zamanı | kullanıcı-etkinlik çoktan çoğa ilişkisi |
| Konuşma / mesaj | katılımcılar, mesaj içeriği, tarih | bire bir mesajlaşma |
| Bildirim | alıcı, tür, kaynak, okunma durumu, tarih | kullanıcı etkileşimleri |

Veri modeli, tekrar eden ilişkileri ayrı ilişki tablolarında tutarak kullanıcı–kulüp, kullanıcı–etkinlik ve kullanıcı–kullanıcı ilişkilerinde veri bütünlüğünü korumalıdır. Tekrarlı kayıtları engellemek için ilgili yabancı anahtar ve benzersizlik kuralları uygulanmalıdır.

## 10. Varsayımlar ve bağımlılıklar

- Kullanıcıların sisteme erişebileceği bir web tarayıcısı ve internet/ağ bağlantısı vardır.
- E-posta doğrulama ve şifre sıfırlama süreçleri için e-posta gönderim altyapısı kullanılabilir durumdadır.
- PostgreSQL, Redis, Kafka ve uygulama servisleri Docker tabanlı geliştirme ortamında çalıştırılabilir.
- Üniversite e-posta alanı, kayıt politikasında doğrulanabilir veya yapılandırılabilir bir kural olarak tanımlanır.
- Yerel dosya depolama, proje teslimi ve geliştirme ortamı için yeterlidir; üretim ölçeğinde kalıcı nesne depolama değerlendirilmelidir.
- Sistem saatinin doğru olması, etkinlik sıralaması ve hikâye sürelerinin doğru işlemesi için gereklidir.

## 11. Kısıtlar

- Uygulama, öncelikle web tabanlı kullanıcı deneyimi için geliştirilmiştir.
- Gerçek zamanlı iletişim ağ bağlantısına bağlıdır; bağlantı kesildiğinde istemci uygun yeniden deneme veya hata deneyimi sunmalıdır.
- Dosya yükleme işlemleri tür ve boyut sınırlarıyla kısıtlanmalıdır.
- Üretim ortamında varsayılan geliştirme gizli anahtarları, demo verileri veya geniş CORS izinleri kullanılmamalıdır.
- Zaman kısıtları nedeniyle kapsam dışı kalan ileri seviye moderasyon, ödeme ve mobil uygulama özellikleri bu sürümde zorunlu değildir.

## 12. Açık kararlar ve sonraki sürüm iyileştirmeleri

Bu maddeler temel teslim kapsamını genişletmeden, canlı ortam kalitesini artırmak için sonraki iterasyonlarda ele alınmalıdır:

- Giriş, kayıt, OTP, dosya yükleme ve mesaj gönderimi için uygulama seviyesinde hız sınırlama (rate limiting).
- WebSocket aboneliklerinde konuşma katılımcısı doğrulamasının açık biçimde uygulanması.
- Kulüp/etkinlik davet adayı uçlarında yönetici yetkisinin backend tarafında zorunlu denetimi.
- Tüm istek nesneleri için uzunluk, biçim ve iş kuralı doğrulamalarının standartlaştırılması.
- Bildirim sorguları, etkinlik tarihleri ve arama senaryoları için üretim verisi hacmine göre ek bileşik/partial indekslerin değerlendirilmesi.
- Yerel dosya depolamanın nesne depolama ve CDN mimarisine taşınması.
- Merkezi loglama, metrik, alarm ve hata izleme altyapısının genişletilmesi.
- İçerik şikâyet, engelleme ve moderasyon akışlarının eklenmesi.

## 13. İzlenebilirlik özeti

| İş alanı | Başlıca kullanıcı hikâyeleri | Doğrulama yaklaşımı |
| --- | --- | --- |
| Kimlik ve hesap | US-01, US-02, US-03 | API entegrasyon testi, form doğrulama testi, oturum senaryosu |
| Profil ve sosyal ağ | US-04, US-05, US-06 | Yetki testi, UI modal/yönlendirme testi |
| İçerik | US-07, US-08, US-09 | Oluşturma–görüntüleme–silme yaşam döngüsü testi |
| Kulüp ve etkinlik | US-10, US-11, US-12, US-13, US-14 | Rol yetkisi, tarih filtresi, davet kabul/red testi |
| Keşif ve bildirim | US-15, US-16 | Arama sonucu, boş durum ve bildirim durum testi |
| Mesajlaşma | US-17 | Katılımcı yetkisi, doğrudan konuşma, gerçek zamanlı iletim testi |

## 14. Sonuç

UniFeed; üniversite topluluğunda dağınık hâlde bulunan içerik paylaşımı, kulüp yönetimi, etkinlik katılımı ve iletişim ihtiyaçlarını tek bir kampüs platformunda birleştirmeyi amaçlar. Bu doküman, ürünün ilk teslim sınırlarını netleştirir: güvenilir kullanıcı hesabı, sosyal etkileşim, kulüp/etkinlik yaşam döngüsü, keşif, bildirim ve bire bir mesajlaşma bu sürümün merkezindedir.

Belirlenen kullanıcı hikâyeleri ve kabul kriterleri; geliştirme tamamlanma koşullarının, manuel kabul testlerinin ve gelecek sürüm planlamasının temel referansı olarak kullanılacaktır.

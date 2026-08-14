# silent-refresh-simulator.skill.md

## Amaç
ARCHITECT.md §3'te tanımlanan token düşme/yenilenme ve WebSocket süreklilik
senaryolarını otomatik test etmek — bu mekanizma iddialı olduğu için gerçek
davranışı doğrulamak kritik.

## Kullanan Bileşenler
`fe_auth_interceptor`, `fe_socket_manager`, `06_qa_validator`
(regresyon testi olarak).

## Senaryolar
1. **Access token expire olurken açık WS bağlantısı**: Token'ı manuel expire
   et (test ortamında kısa TTL, örn. 5sn), WS bağlantısının kopmadığını
   doğrula (STOMP heartbeat/ping-pong sürüyor mu).
2. **401 sırasında eşzamanlı çoklu istek**: Aynı anda 5 paralel API isteği
   at, hepsi 401 alsın; yalnızca **1 adet** `/auth/refresh` çağrısı
   yapıldığını doğrula (`isRefreshing` kilidi çalışıyor mu).
3. **Refresh token da geçersizse**: Kullanıcının login sayfasına
   yönlendirildiğini ve store'un temizlendiğini doğrula.
4. **30 dakikalık periyodik WS reconnect**: Zamanlayıcıyı hızlandırılmış
   test modunda (örn. 3sn) çalıştırıp, reconnect sırasında mevcut
   subscription'ların (mesaj dinleyicilerinin) kaybolmadığını doğrula —
   reconnect öncesi gönderilen bir test mesajının reconnect sonrası hâlâ
   dinleniyor olması beklenir.

## Çıktı
`docs/WS_SESSION_TEST_REPORT.md` — senaryo bazlı PASS/FAIL

## Kısıtlar
- Bu skill yalnızca test/staging ortamında çalışır, prod'a asla
  bağlanmaz.

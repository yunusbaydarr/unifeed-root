# rfc7807-error-generator.skill.md

## Amaç
Yeni bir exception türü tanımlanırken standart RFC 7807 `ProblemDetail`
yanıtını otomatik üretmek — elle tekrar tekrar aynı boilerplate yazılmasın.

## Kullanan Bileşenler
`03_be_core` (GlobalExceptionHandler iskeleti), her backend subagent'ı
(yeni exception türü eklerken).

## Mantık
1. Girdi olarak `{ exceptionClassName, httpStatus, i18nKey, errorCode }`
   alır. Örn: `{ "exceptionClassName": "ClubAccessDeniedException",
   "httpStatus": 403, "i18nKey": "club.access.denied",
   "errorCode": "AUTH_008_FORBIDDEN_CLUB_ACTION" }`.
2. Şu iki parçayı üretir:
   - Exception sınıfı (`RuntimeException` extend eden, `errorCode` taşıyan).
   - `@ExceptionHandler` metodu (`GlobalExceptionHandler` içine eklenir),
     `MessageSource` üzerinden `i18nKey`'i `Accept-Language`'a göre çözer.
3. Üretilen `ProblemDetail` örneği:
   ```json
   {
     "type": "https://unifeed.app/errors/club-access-denied",
     "title": "Access Denied",
     "status": 403,
     "detail": "<i18n resolved message>",
     "instance": "<request path>",
     "code": "AUTH_008_FORBIDDEN_CLUB_ACTION",
     "timestamp": "<ISO-8601>",
     "validationErrors": null
   }
   ```
4. `messages_tr.properties`/`messages_en.properties`'e karşılık gelen key'in
   var olup olmadığını kontrol eder, yoksa `be_i18n_manager`'ı tetikler.

## Çıktı
- Exception sınıfı dosyası
- `GlobalExceptionHandler`'a eklenen metod diff'i

## Kısıtlar
- `type` URI'si her zaman `https://unifeed.app/errors/{kebab-case-code}`
  formatında olacak, elle farklı bir domain yazılamaz.

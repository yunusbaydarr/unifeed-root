package app.unifeed.seeder;

import java.util.List;
import java.util.Locale;
import net.datafaker.Faker;

final class SeedContentFactory {
    private final Faker faker = new Faker(new Locale("tr", "TR"));
    private final List<String> topics = List.of("kampüs yaşamı", "öğrenci toplulukları", "kariyer", "teknoloji", "sanat ve tasarım", "sosyal sorumluluk");
    String name() { return faker.name().fullName(); }
    String post(int index) { String topic = topics.get(index % topics.size()); return "Bugün " + topic + " üzerine çok keyifli bir buluşma yaptık. " + faker.lorem().sentence(8) + " Katılmak isteyen herkesi bekleriz!"; }
    String comment() { return faker.lorem().sentence(6) + " Çok güzel bir çalışma olmuş."; }
    String message() { return faker.lorem().sentence(7); }
}

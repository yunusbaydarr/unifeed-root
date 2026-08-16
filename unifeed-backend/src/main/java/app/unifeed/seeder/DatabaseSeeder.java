package app.unifeed.seeder;

import app.unifeed.media.MediaStorageService;
import java.sql.Timestamp;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/** Dev/local-only demo data. Existing user data is preserved and the demo set is added at most once. */
@Component
@Profile({"dev", "local"})
public class DatabaseSeeder implements ApplicationRunner {
    private static final String DEMO_PASSWORD = "Demo123!";
    private final JdbcTemplate jdbc;
    private final PasswordEncoder passwords;
    private final SeedMediaFactory media;
    private final SeedContentFactory content = new SeedContentFactory(); private final boolean enabled;

    public DatabaseSeeder(JdbcTemplate jdbc, PasswordEncoder passwords, MediaStorageService storage, @Value("${app.seed.enabled:false}") boolean enabled) {
        this.jdbc = jdbc; this.passwords = passwords; this.media = new SeedMediaFactory(storage); this.enabled = enabled;
    }

    @Override @Transactional
    public void run(ApplicationArguments arguments) {
        if (!enabled) return;
        Boolean demoAlreadySeeded = jdbc.queryForObject("SELECT EXISTS(SELECT 1 FROM users WHERE email=? AND deleted_at IS NULL)", Boolean.class, "ayse.demir@gmail.com");
        if (Boolean.TRUE.equals(demoAlreadySeeded)) return;
        List<UUID> users = createUsers();
        List<UUID> clubs = createClubs(users);
        createPosts(users);
        createEvents(users, clubs);
        createConversations(users);
    }

    private List<UUID> createUsers() {
        List<UUID> users = new ArrayList<>();
        String[] emails = {"ayse.demir@gmail.com", "mehmet.kaya@gmail.com", "zeynep.arslan@gmail.com", "can.yildiz@gmail.com", "elif.ozkan@gmail.com", "berk.koc@gmail.com", "selin.akar@gmail.com", "mert.celik@gmail.com", "deniz.sahin@gmail.com", "irem.kurt@gmail.com", "emre.aydin@gmail.com", "eda.arslan@gmail.com", "kaan.turan@gmail.com", "sude.yilmaz@gmail.com", "alp.ergun@gmail.com", "duru.karaca@gmail.com", "furkan.oz@gmail.com", "nazli.gunes@gmail.com", "tolga.aslan@gmail.com", "melis.kaya@gmail.com"};
        for (int i = 0; i < emails.length; i++) {
            UUID id = UUID.randomUUID(); String name = content.name();
            jdbc.update("INSERT INTO users(id,email,display_name,password_hash,avatar_path,global_role,is_email_verified,is_test_account,bio,department,academic_year) VALUES(?,?,?,?,?,'STUDENT',true,true,?,?,?)",
                id, emails[i], name, passwords.encode(DEMO_PASSWORD), media.avatar("user-" + i), "UniFeed topluluğunda aktif bir öğrenci.", i % 2 == 0 ? "Bilgisayar Mühendisliği" : "Endüstri Mühendisliği", (1 + i % 4) + ". sınıf");
            users.add(id);
        }
        return users;
    }

    private List<UUID> createClubs(List<UUID> users) {
        String[][] definitions = {{"Yazılım ve Teknoloji Kulübü", "Teknoloji", "Yazılım, yapay zekâ ve ürün geliştirme topluluğu."}, {"Fotoğrafçılık Kulübü", "Sanat", "Kampüsün hikâyelerini birlikte kadraja alıyoruz."}, {"Girişimcilik Kulübü", "Kariyer", "Fikirleri sürdürülebilir girişimlere dönüştürüyoruz."}, {"Müzik Kulübü", "Kültür", "Sahne, prova ve canlı performans için buluşuyoruz."}, {"Sosyal Sorumluluk Kulübü", "Gönüllülük", "Kampüs dışındaki topluluklarla dayanışma içindeyiz."}};
        List<UUID> clubs = new ArrayList<>();
        for (int i = 0; i < definitions.length; i++) {
            UUID club = UUID.randomUUID(); UUID admin = users.get(i);
            jdbc.update("INSERT INTO clubs(id,name,description,cover_path,logo_path,status,created_by,category) VALUES(?,?,?,?,?,'ACTIVE',?,?)", club, definitions[i][0], definitions[i][2], media.clubImage("cover-" + i), media.clubImage("logo-" + i), admin, definitions[i][1]);
            jdbc.update("INSERT INTO club_members(club_id,user_id,role) VALUES(?,?,'CLUB_ADMIN')", club, admin);
            for (int member = 1; member <= 7; member++) jdbc.update("INSERT INTO club_members(club_id,user_id,role) VALUES(?,?,'CLUB_MEMBER')", club, users.get((i + member) % users.size()));
            clubs.add(club);
        }
        return clubs;
    }

    private void createPosts(List<UUID> users) {
        List<UUID> posts = new ArrayList<>();
        for (int i = 0; i < 50; i++) {
            UUID post = UUID.randomUUID(); posts.add(post);
            jdbc.update("INSERT INTO posts(id,author_id,content,created_at) VALUES(?,?,?,?)", post, users.get(i % users.size()), content.post(i), Timestamp.from(Instant.now().minus(i * 3L, ChronoUnit.HOURS)));
            if (i % 2 == 0) jdbc.update("INSERT INTO post_media(id,post_id,media_path,order_index,media_type) VALUES(?,?,?,1,'IMAGE')", UUID.randomUUID(), post, media.postImage("post-" + i));
            for (int like = 1; like <= 4; like++) jdbc.update("INSERT INTO post_likes(post_id,user_id) VALUES(?,?) ON CONFLICT DO NOTHING", post, users.get((i + like) % users.size()));
            for (int comment = 1; comment <= 2; comment++) jdbc.update("INSERT INTO comments(id,post_id,user_id,content) VALUES(?,?,?,?)", UUID.randomUUID(), post, users.get((i + comment + 5) % users.size()), content.comment());
        }
    }

    private void createEvents(List<UUID> users, List<UUID> clubs) {
        String[] titles = {"Yapay Zekâ ile Ürün Geliştirme", "Kampüs Fotoğraf Yürüyüşü", "Fikirden Girişime Atölyesi", "Açık Hava Akustik Konseri", "Gönüllülük Proje Günü"};
        for (int i = 0; i < clubs.size(); i++) {
            UUID event = UUID.randomUUID(); Instant starts = Instant.now().plus(2L + i * 2L, ChronoUnit.DAYS);
            jdbc.update("INSERT INTO events(id,club_id,created_by,title,description,poster_path,starts_at,ends_at,location,category) VALUES(?,?,?,?,?,?,?,?,?,?)", event, clubs.get(i), users.get(i), titles[i], "Kulüp üyeleri ve tüm öğrenciler için uygulamalı etkinlik.", media.postImage("event-" + i), Timestamp.from(starts), Timestamp.from(starts.plus(2, ChronoUnit.HOURS)), "Merkez Kampüs", i == 2 ? "Kariyer" : "Topluluk");
            for (int attendee = 0; attendee < 8; attendee++) jdbc.update("INSERT INTO event_attendees(event_id,user_id) VALUES(?,?)", event, users.get((i + attendee) % users.size()));
        }
    }

    private void createConversations(List<UUID> users) {
        for (int i = 0; i < 4; i++) {
            UUID thread = UUID.randomUUID(); UUID first = users.get(i); UUID second = users.get(i + 6);
            jdbc.update("INSERT INTO conversation_threads(id,type) VALUES(?,'DIRECT')", thread);
            jdbc.update("INSERT INTO thread_participants(thread_id,user_id) VALUES(?,?),(?,?)", thread, first, thread, second);
            for (int message = 0; message < 6; message++) jdbc.update("INSERT INTO direct_messages(id,thread_id,sender_id,content,created_at) VALUES(?,?,?,?,?)", UUID.randomUUID(), thread, message % 2 == 0 ? first : second, content.message(), Timestamp.from(Instant.now().minus(30L - message * 3L, ChronoUnit.MINUTES)));
        }
    }
}

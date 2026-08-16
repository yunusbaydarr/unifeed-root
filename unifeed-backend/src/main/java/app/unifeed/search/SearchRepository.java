package app.unifeed.search;

import java.util.List;
import java.util.UUID;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
class SearchRepository {
    private final JdbcTemplate jdbc;
    SearchRepository(JdbcTemplate jdbc) { this.jdbc = jdbc; }
    List<SearchController.UserResult> users(String query, int limit) { return jdbc.query("SELECT id,display_name,avatar_path,department FROM users WHERE deleted_at IS NULL AND lower(display_name) LIKE lower('%'||?||'%') ORDER BY display_name LIMIT ?", (rs,n)->new SearchController.UserResult((UUID)rs.getObject(1),rs.getString(2),rs.getString(3),rs.getString(4)),query,limit); }
    List<SearchController.ClubResult> clubs(String query, int limit) { return jdbc.query("SELECT id,name,logo_path,category FROM clubs WHERE deleted_at IS NULL AND status='ACTIVE' AND lower(name) LIKE lower('%'||?||'%') ORDER BY name LIMIT ?", (rs,n)->new SearchController.ClubResult((UUID)rs.getObject(1),rs.getString(2),rs.getString(3),rs.getString(4)),query,limit); }
    List<SearchController.EventResult> events(String query, int limit) { return jdbc.query("SELECT id,title,poster_path,starts_at,location FROM events WHERE deleted_at IS NULL AND starts_at>=now() AND lower(title) LIKE lower('%'||?||'%') ORDER BY starts_at LIMIT ?", (rs,n)->new SearchController.EventResult((UUID)rs.getObject(1),rs.getString(2),rs.getString(3),rs.getTimestamp(4).toInstant(),rs.getString(5)),query,limit); }
}

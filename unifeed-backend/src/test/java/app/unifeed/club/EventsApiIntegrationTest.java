package app.unifeed.club;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import app.unifeed.UniFeedApplication;
import app.unifeed.security.JwtService;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

/** Exercises Flyway migrations, JWT authentication, the HTTP controller and PostgreSQL query together. */
@SpringBootTest(classes = UniFeedApplication.class, webEnvironment = SpringBootTest.WebEnvironment.MOCK,
    properties = {"spring.task.scheduling.enabled=false", "spring.kafka.listener.auto-startup=false", "spring.kafka.admin.fail-fast=false"})
@AutoConfigureMockMvc
@Testcontainers(disabledWithoutDocker = true)
class EventsApiIntegrationTest {
  @Container static final PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:16-alpine")
      .withDatabaseName("unifeed_it").withUsername("unifeed").withPassword("unifeed");

  @DynamicPropertySource
  static void databaseProperties(DynamicPropertyRegistry registry) {
    registry.add("spring.datasource.url", postgres::getJdbcUrl);
    registry.add("spring.datasource.username", postgres::getUsername);
    registry.add("spring.datasource.password", postgres::getPassword);
    registry.add("spring.data.redis.host", () -> "localhost");
    registry.add("spring.kafka.bootstrap-servers", () -> "localhost:65535");
  }

  @Autowired MockMvc mvc;
  @Autowired JdbcTemplate jdbc;
  @Autowired JwtService jwt;

  private UUID user;
  private UUID futureEvent;

  @BeforeEach
  void setUp() {
    jdbc.execute("TRUNCATE TABLE event_attendees, events, club_members, clubs, users CASCADE");
    user = UUID.randomUUID();
    UUID club = UUID.randomUUID();
    futureEvent = UUID.randomUUID();
    jdbc.update("INSERT INTO users(id,email,display_name,global_role,is_email_verified) VALUES(?,?,?,'STUDENT',true)", user, "integration@unifeed.test", "Integration User");
    jdbc.update("INSERT INTO clubs(id,name,status,created_by,category) VALUES(?,?,'ACTIVE',?,?)", club, "Integration Club", user, "Technology");
    jdbc.update("INSERT INTO events(id,club_id,created_by,title,starts_at,category) VALUES(?,?,?,?,?,?)", futureEvent, club, user, "Future event", Instant.now().plus(1, ChronoUnit.DAYS), "Technology");
    jdbc.update("INSERT INTO events(id,club_id,created_by,title,starts_at,category) VALUES(?,?,?,?,?,?)", UUID.randomUUID(), club, user, "Past event", Instant.now().minus(1, ChronoUnit.DAYS), "Technology");
  }

  @Test
  void eventsEndpointAuthenticatesAndReturnsOnlyFutureEvents() throws Exception {
    String token = jwt.issueAccessToken(user, "integration@unifeed.test", "STUDENT");

    mvc.perform(get("/api/v1/events").header("Authorization", "Bearer " + token))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$[0].id").value(futureEvent.toString()))
        .andExpect(jsonPath("$[0].title").value("Future event"))
        .andExpect(jsonPath("$[1]").doesNotExist());

    Integer flywayVersion = jdbc.queryForObject("SELECT max(version)::int FROM flyway_schema_history WHERE success", Integer.class);
    assertThat(flywayVersion).isGreaterThanOrEqualTo(5);
  }
}

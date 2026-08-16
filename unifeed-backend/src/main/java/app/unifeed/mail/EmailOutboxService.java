package app.unifeed.mail;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.Duration;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
public class EmailOutboxService {
    private static final Logger log = LoggerFactory.getLogger(EmailOutboxService.class);
    private final JdbcTemplate jdbc;
    private final ObjectMapper mapper;
    private final KafkaTemplate<String, Object> kafka;
    private final Duration sendTimeout;

    public EmailOutboxService(JdbcTemplate jdbc, ObjectMapper mapper, KafkaTemplate<String, Object> kafka,
        @Value("${app.outbox.send-timeout:5s}") Duration sendTimeout) {
        this.jdbc = jdbc;
        this.mapper = mapper;
        this.kafka = kafka;
        this.sendTimeout = sendTimeout;
    }

    public void enqueue(String topic, String eventKey, Object event) {
        try {
            jdbc.update("INSERT INTO email_outbox(id,topic,event_key,payload) VALUES(?,?,?,?::jsonb)",
                UUID.randomUUID(), topic, eventKey, mapper.writeValueAsString(event));
        } catch (JsonProcessingException ex) {
            throw new IllegalArgumentException("Email event cannot be serialized", ex);
        }
    }

    @Scheduled(fixedDelayString = "${app.outbox.poll-interval:2s}")
    public void publishPending() {
        List<OutboxRow> rows = jdbc.query("SELECT id,topic,event_key,payload::text FROM email_outbox " +
                "WHERE published_at IS NULL AND next_attempt_at<=now() ORDER BY created_at LIMIT 50",
            (rs, row) -> new OutboxRow((UUID) rs.getObject(1), rs.getString(2), rs.getString(3), rs.getString(4)));
        rows.forEach(this::publish);
    }

    private void publish(OutboxRow row) {
        try {
            Object payload = mapper.readValue(row.payload(), Object.class);
            kafka.send(row.topic(), row.eventKey(), payload).get(sendTimeout.toMillis(), TimeUnit.MILLISECONDS);
            jdbc.update("UPDATE email_outbox SET published_at=now(),last_error=NULL WHERE id=? AND published_at IS NULL", row.id());
        } catch (Exception ex) {
            String error = ex.getMessage() == null ? ex.getClass().getSimpleName() : ex.getMessage();
            jdbc.update("UPDATE email_outbox SET attempt_count=attempt_count+1,next_attempt_at=now()+interval '10 seconds',last_error=? WHERE id=? AND published_at IS NULL",
                error.substring(0, Math.min(error.length(), 500)), row.id());
            log.warn("Email outbox publish deferred: id={}", row.id());
        }
    }

    private record OutboxRow(UUID id, String topic, String eventKey, String payload) {}
}

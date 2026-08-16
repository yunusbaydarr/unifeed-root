package app.unifeed.mail;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.mail.internet.MimeMessage;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.annotation.RetryableTopic;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

@Component
public class EmailEventConsumer {
    private static final Logger log = LoggerFactory.getLogger(EmailEventConsumer.class);
    private static final Set<String> SUPPORTED_LANGUAGES = Set.of("tr", "en");

    private final JdbcTemplate jdbc;
    private final JavaMailSender mail;
    private final TemplateEngine templates;
    private final ObjectMapper mapper;

    public EmailEventConsumer(JdbcTemplate jdbc, JavaMailSender mail, TemplateEngine templates, ObjectMapper mapper) {
        this.jdbc = jdbc;
        this.mail = mail;
        this.templates = templates;
        this.mapper = mapper;
    }

    @Transactional
    @RetryableTopic(attempts = "3", dltTopicSuffix = "-dlq", retryTopicSuffix = "-retry")
    @KafkaListener(topics = "email-events", groupId = "unifeed-email-worker")
    public void consume(ConsumerRecord<String, Object> record) throws Exception {
        Map<String, Object> event = mapper.convertValue(record.value(), new TypeReference<>() {});
        UUID eventId = UUID.fromString(event.get("eventId").toString());
        if (jdbc.update("INSERT INTO processed_events(event_id,event_type) VALUES(?,?) ON CONFLICT DO NOTHING",
            eventId, event.get("eventType")) == 0) {
            return;
        }

        String requestedLanguage = String.valueOf(event.get("language"));
        String language = SUPPORTED_LANGUAGES.contains(requestedLanguage) ? requestedLanguage : "en";
        Context context = new Context(Locale.forLanguageTag(language));
        Object data = event.get("templateData");
        if (data instanceof Map<?, ?> values) {
            values.forEach((key, value) -> context.setVariable(key.toString(), value));
        }

        String eventType = event.get("eventType").toString();
        String html = templates.process("email/" + eventType + "_" + language, context);
        MimeMessage message = mail.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
        helper.setTo(event.get("recipientEmail").toString());
        helper.setSubject("UniFeed");
        helper.setText(html, true);
        mail.send(message);
        log.info("Email event processed: type={}", eventType);
    }
}

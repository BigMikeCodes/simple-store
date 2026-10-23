package dev.michaelfarrant.simplestore.outbox;

import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.util.UUID;

/**
 * Stores a message in the transactional outbox. Must join the transaction making the change the message
 * describes, so the message exists if and only if that change commits; {@link OutboxRelay} sends it later.
 */
@Component
public class OutboxWriter {

    private final JdbcClient jdbcClient;

    public OutboxWriter(JdbcClient jdbcClient) {
        this.jdbcClient = jdbcClient;
    }

    @Transactional(propagation = Propagation.MANDATORY)
    public void write(UUID messageId, String exchange, String routingKey, String messageType, byte[] payload) {
        final String insert = """
                INSERT INTO outbox_message (id, exchange, routing_key, message_type, payload, created)
                VALUES (:id, :exchange, :routingKey, :messageType, :payload, :created)
                """;

        jdbcClient.sql(insert)
                .param("id", messageId)
                .param("exchange", exchange)
                .param("routingKey", routingKey)
                .param("messageType", messageType)
                .param("payload", payload)
                .param("created", OffsetDateTime.now())
                .update();
    }
}

package dev.michaelfarrant.simplestore.outbox;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.AmqpException;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageBuilder;
import org.springframework.amqp.core.MessageDeliveryMode;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.support.TransactionTemplate;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * Relays unsent outbox messages to RabbitMQ. A message is only marked sent once the broker confirms it,
 * so delivery is at-least-once and consumers must be idempotent. Rows are locked with SKIP LOCKED, so
 * several app instances can relay concurrently without sending the same row twice in parallel.
 */
@Component
public class OutboxRelay {

    public static final String CONTENT_TYPE_PROTOBUF = "application/x-protobuf";

    private static final int BATCH_SIZE = 100;
    private static final long CONFIRM_TIMEOUT_SECONDS = 5;

    private final Logger logger = LoggerFactory.getLogger(OutboxRelay.class);
    private final JdbcClient jdbcClient;
    private final RabbitTemplate rabbitTemplate;
    private final TransactionTemplate transactionTemplate;

    public OutboxRelay(JdbcClient jdbcClient, RabbitTemplate rabbitTemplate, TransactionTemplate transactionTemplate) {
        this.jdbcClient = jdbcClient;
        this.rabbitTemplate = rabbitTemplate;
        this.transactionTemplate = transactionTemplate;
    }

    @Scheduled(fixedDelayString = "${outbox.relay.fixed-delay-ms:500}")
    public void relay() {
        Integer sent;
        // Keep going while batches are full and succeeding, so a backlog drains without waiting for the next tick
        do {
            sent = transactionTemplate.execute(_ -> relayBatch());
        } while (sent != null && sent == BATCH_SIZE);
    }

    private int relayBatch() {
        final String selectUnsent = """
                SELECT id, exchange, routing_key, message_type, payload, created
                FROM outbox_message
                WHERE sent IS NULL
                ORDER BY created
                LIMIT :limit
                FOR UPDATE SKIP LOCKED
                """;

        List<OutboxMessage> messages = jdbcClient.sql(selectUnsent)
                .param("limit", BATCH_SIZE)
                .query((rs, i) -> new OutboxMessage(
                        rs.getObject("id", UUID.class),
                        rs.getString("exchange"),
                        rs.getString("routing_key"),
                        rs.getString("message_type"),
                        rs.getBytes("payload"),
                        rs.getObject("created", OffsetDateTime.class)))
                .list();

        if (messages.isEmpty()) {
            return 0;
        }

        List<Pending> pending = new ArrayList<>();
        List<UUID> failed = new ArrayList<>();

        for (OutboxMessage message : messages) {
            if (!failed.isEmpty()) {
                // The broker is unreachable; don't wait on a connection attempt for every remaining row
                failed.add(message.id());
                continue;
            }
            try {
                CorrelationData correlation = new CorrelationData(message.id().toString());
                rabbitTemplate.send(message.exchange(), message.routingKey(), toAmqpMessage(message), correlation);
                pending.add(new Pending(message, correlation));
            } catch (AmqpException exception) {
                logger.warn("Failed to send outbox message {}", message.id(), exception);
                failed.add(message.id());
            }
        }

        List<UUID> sent = new ArrayList<>();
        for (Pending p : pending) {
            if (isConfirmed(p)) {
                sent.add(p.message().id());
            } else {
                failed.add(p.message().id());
            }
        }

        if (!sent.isEmpty()) {
            jdbcClient.sql("UPDATE outbox_message SET sent = :now WHERE id IN (:ids)")
                    .param("now", OffsetDateTime.now())
                    .param("ids", sent)
                    .update();
        }
        if (!failed.isEmpty()) {
            jdbcClient.sql("UPDATE outbox_message SET attempts = attempts + 1 WHERE id IN (:ids)")
                    .param("ids", failed)
                    .update();
        }

        return sent.size();
    }

    private boolean isConfirmed(Pending pending) {
        UUID id = pending.message().id();
        try {
            CorrelationData.Confirm confirm = pending.correlation().getFuture().get(CONFIRM_TIMEOUT_SECONDS, TimeUnit.SECONDS);
            if (!confirm.ack()) {
                logger.warn("Broker rejected outbox message {}: {}", id, confirm.reason());
                return false;
            }
            if (pending.correlation().getReturned() != null) {
                // Unroutable means nothing is subscribed yet; that isn't something a retry would fix
                logger.warn("Outbox message {} was unroutable ({} / {})",
                        id, pending.message().exchange(), pending.message().routingKey());
            }
            return true;
        } catch (TimeoutException | ExecutionException exception) {
            logger.warn("No confirm for outbox message {}", id, exception);
            return false;
        } catch (InterruptedException exception) {
            Thread.currentThread().interrupt();
            return false;
        }
    }

    private Message toAmqpMessage(OutboxMessage message) {
        return MessageBuilder.withBody(message.payload())
                .setContentType(CONTENT_TYPE_PROTOBUF)
                .setType(message.messageType())
                .setMessageId(message.id().toString())
                .setTimestamp(Date.from(message.created().toInstant()))
                .setDeliveryMode(MessageDeliveryMode.PERSISTENT)
                .build();
    }

    private record OutboxMessage(
            UUID id,
            String exchange,
            String routingKey,
            String messageType,
            byte[] payload,
            OffsetDateTime created) {
    }

    private record Pending(OutboxMessage message, CorrelationData correlation) {
    }
}

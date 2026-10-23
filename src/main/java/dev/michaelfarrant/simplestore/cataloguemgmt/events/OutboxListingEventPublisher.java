package dev.michaelfarrant.simplestore.cataloguemgmt.events;

import dev.michaelfarrant.simplestore.config.rabbitmq.Exchanges;
import dev.michaelfarrant.simplestore.outbox.OutboxWriter;
import org.springframework.stereotype.Component;

/**
 * Publishes listing events to RabbitMQ via the transactional outbox: the event is stored in the
 * caller's transaction and relayed once it commits (see {@link dev.michaelfarrant.simplestore.outbox.OutboxRelay}).
 */
@Component
public class OutboxListingEventPublisher implements ListingEventPublisher {

    private final OutboxWriter outboxWriter;

    public OutboxListingEventPublisher(OutboxWriter outboxWriter) {
        this.outboxWriter = outboxWriter;
    }

    @Override
    public void publish(ListingEvent event) {
        outboxWriter.write(
                event.eventId(),
                Exchanges.CATALOGUE_LISTING_EVENTS,
                ListingEventProtoMapper.routingKey(event),
                ListingEventProtoMapper.messageType(),
                ListingEventProtoMapper.toProto(event).toByteArray());
    }
}

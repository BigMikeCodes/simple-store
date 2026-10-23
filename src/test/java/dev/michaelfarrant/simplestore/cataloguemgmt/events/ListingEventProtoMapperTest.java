package dev.michaelfarrant.simplestore.cataloguemgmt.events;

import dev.michaelfarrant.simplestore.contracts.cataloguemgmt.listing.v1.ListingEvent.PayloadCase;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class ListingEventProtoMapperTest {

    private final UUID eventId = UUID.randomUUID();
    private final UUID listingId = UUID.randomUUID();
    private final OffsetDateTime occurredAt = OffsetDateTime.of(2026, 10, 23, 9, 30, 15, 123_000_000, ZoneOffset.ofHours(1));

    @Test
    void publishedRoundTripsThroughWireFormat() throws Exception {
        UUID itemId = UUID.randomUUID();
        ListingPublished event = new ListingPublished(eventId, listingId, 3, occurredAt, "title", "description",
                List.of(new ListingPublished.PublishedItem(itemId, "CODE-1", "item", "item description", 2)));

        var parsed = roundTrip(event);

        assertThat(parsed.getEventId()).isEqualTo(eventId.toString());
        assertThat(parsed.getListingId()).isEqualTo(listingId.toString());
        assertThat(parsed.getListingVersion()).isEqualTo(3);
        assertThat(Instant.ofEpochSecond(parsed.getOccurredAt().getSeconds(), parsed.getOccurredAt().getNanos()))
                .isEqualTo(occurredAt.toInstant());
        assertThat(parsed.getPayloadCase()).isEqualTo(PayloadCase.PUBLISHED);
        assertThat(parsed.getPublished().getTitle()).isEqualTo("title");
        assertThat(parsed.getPublished().getDescription()).isEqualTo("description");
        assertThat(parsed.getPublished().getItemsList()).singleElement().satisfies(item -> {
            assertThat(item.getItemId()).isEqualTo(itemId.toString());
            assertThat(item.getCode()).isEqualTo("CODE-1");
            assertThat(item.getQuantity()).isEqualTo(2);
        });
        assertThat(ListingEventProtoMapper.routingKey(event)).isEqualTo("listing.published");
    }

    @Test
    void updatedRoundTripsThroughWireFormat() throws Exception {
        ListingUpdated event = new ListingUpdated(eventId, listingId, 4, occurredAt, "new title", "new description");

        var parsed = roundTrip(event);

        assertThat(parsed.getPayloadCase()).isEqualTo(PayloadCase.UPDATED);
        assertThat(parsed.getUpdated().getTitle()).isEqualTo("new title");
        assertThat(parsed.getUpdated().getDescription()).isEqualTo("new description");
        assertThat(ListingEventProtoMapper.routingKey(event)).isEqualTo("listing.updated");
    }

    @Test
    void withdrawnRoundTripsThroughWireFormat() throws Exception {
        ListingWithdrawn event = new ListingWithdrawn(eventId, listingId, 5, occurredAt);

        var parsed = roundTrip(event);

        assertThat(parsed.getPayloadCase()).isEqualTo(PayloadCase.WITHDRAWN);
        assertThat(parsed.getListingVersion()).isEqualTo(5);
        assertThat(ListingEventProtoMapper.routingKey(event)).isEqualTo("listing.withdrawn");
    }

    private dev.michaelfarrant.simplestore.contracts.cataloguemgmt.listing.v1.ListingEvent roundTrip(ListingEvent event) throws Exception {
        byte[] bytes = ListingEventProtoMapper.toProto(event).toByteArray();
        return dev.michaelfarrant.simplestore.contracts.cataloguemgmt.listing.v1.ListingEvent.parseFrom(bytes);
    }
}

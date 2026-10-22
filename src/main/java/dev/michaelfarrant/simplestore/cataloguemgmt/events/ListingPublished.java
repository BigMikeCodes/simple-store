package dev.michaelfarrant.simplestore.cataloguemgmt.events;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

public record ListingPublished(
        UUID eventId,
        UUID listingId,
        OffsetDateTime occurredAt,
        String title,
        String description,
        List<PublishedItem> items) implements ListingEvent {

    public record PublishedItem(UUID itemId, String code, String title, String description, int quantity) {
    }

}

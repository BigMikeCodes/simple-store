package dev.michaelfarrant.simplestore.cataloguemgmt.events;

import java.time.OffsetDateTime;
import java.util.UUID;

/**
 * A minor correction to an already published listing.
 */
public record ListingUpdated(
        UUID eventId,
        UUID listingId,
        OffsetDateTime occurredAt,
        String title,
        String description) implements ListingEvent {
}

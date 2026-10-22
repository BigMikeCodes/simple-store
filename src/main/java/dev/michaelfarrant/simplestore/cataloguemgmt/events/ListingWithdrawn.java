package dev.michaelfarrant.simplestore.cataloguemgmt.events;

import java.time.OffsetDateTime;
import java.util.UUID;

public record ListingWithdrawn(
        UUID eventId,
        UUID listingId,
        OffsetDateTime occurredAt) implements ListingEvent {
}

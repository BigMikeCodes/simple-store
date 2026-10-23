package dev.michaelfarrant.simplestore.cataloguemgmt.features.listlistings;

import dev.michaelfarrant.simplestore.cataloguemgmt.ListingStatus;

import java.time.OffsetDateTime;
import java.util.UUID;

public record ListingSummaryProjection(
        UUID id,
        String title,
        ListingStatus status,
        int itemCount,
        OffsetDateTime created,
        OffsetDateTime updated) {
}

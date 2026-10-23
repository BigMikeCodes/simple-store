package dev.michaelfarrant.simplestore.cataloguemgmt.features.getlisting;

import dev.michaelfarrant.simplestore.cataloguemgmt.ListingStatus;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

public record ListingProjection(
        UUID id,
        String title,
        String description,
        ListingStatus status,
        boolean canPublish,
        OffsetDateTime created,
        OffsetDateTime updated,
        OffsetDateTime published,
        OffsetDateTime withdrawn,
        List<Item> items) {

    public record Item(UUID itemId, String code, String title, int quantity) {
    }
}

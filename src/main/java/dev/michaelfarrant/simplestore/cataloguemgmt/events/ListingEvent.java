package dev.michaelfarrant.simplestore.cataloguemgmt.events;

import java.time.OffsetDateTime;
import java.util.UUID;

/**
 * Integration events published by catalogue management for other bounded contexts (e.g. storefront)
 * to consume. Events are self-contained so consumers don't need to call back into this context.
 * {@code eventId} lets consumers de-duplicate.
 */
public sealed interface ListingEvent permits ListingPublished, ListingUpdated, ListingWithdrawn {

    UUID eventId();

    UUID listingId();

    OffsetDateTime occurredAt();

}

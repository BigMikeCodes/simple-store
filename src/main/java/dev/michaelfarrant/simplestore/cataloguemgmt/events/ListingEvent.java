package dev.michaelfarrant.simplestore.cataloguemgmt.events;

import java.time.OffsetDateTime;
import java.util.UUID;

/**
 * Integration events published by catalogue management for other bounded contexts (e.g. storefront)
 * to consume. Events are self-contained so consumers don't need to call back into this context.
 * {@code eventId} lets consumers de-duplicate, and {@code listingVersion} (the listing's version after the
 * change) lets them discard events that arrive out of order.
 */
public sealed interface ListingEvent permits ListingPublished, ListingUpdated, ListingWithdrawn {

    UUID eventId();

    UUID listingId();

    long listingVersion();

    OffsetDateTime occurredAt();

}

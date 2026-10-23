package dev.michaelfarrant.simplestore.cataloguemgmt.events;

import com.google.protobuf.Timestamp;
import dev.michaelfarrant.simplestore.contracts.cataloguemgmt.listing.v1.PublishedItem;

import java.time.OffsetDateTime;

/**
 * Maps listing events to their protobuf wire contract
 * ({@code src/main/proto/simplestore/cataloguemgmt/listing/v1/listing_events.proto}), keeping generated
 * classes out of the rest of catalogue management.
 */
public final class ListingEventProtoMapper {

    public static final String ROUTING_KEY_PUBLISHED = "listing.published";
    public static final String ROUTING_KEY_UPDATED = "listing.updated";
    public static final String ROUTING_KEY_WITHDRAWN = "listing.withdrawn";

    private ListingEventProtoMapper() {
    }

    public static String messageType() {
        return dev.michaelfarrant.simplestore.contracts.cataloguemgmt.listing.v1.ListingEvent.getDescriptor().getFullName();
    }

    public static String routingKey(ListingEvent event) {
        return switch (event) {
            case ListingPublished _ -> ROUTING_KEY_PUBLISHED;
            case ListingUpdated _ -> ROUTING_KEY_UPDATED;
            case ListingWithdrawn _ -> ROUTING_KEY_WITHDRAWN;
        };
    }

    public static dev.michaelfarrant.simplestore.contracts.cataloguemgmt.listing.v1.ListingEvent toProto(ListingEvent event) {
        var builder = dev.michaelfarrant.simplestore.contracts.cataloguemgmt.listing.v1.ListingEvent.newBuilder()
                .setEventId(event.eventId().toString())
                .setListingId(event.listingId().toString())
                .setListingVersion(event.listingVersion())
                .setOccurredAt(toTimestamp(event.occurredAt()));

        switch (event) {
            case ListingPublished published -> builder.setPublished(
                    dev.michaelfarrant.simplestore.contracts.cataloguemgmt.listing.v1.ListingPublished.newBuilder()
                            .setTitle(published.title())
                            .setDescription(published.description())
                            .addAllItems(published.items().stream().map(ListingEventProtoMapper::toProto).toList()));
            case ListingUpdated updated -> builder.setUpdated(
                    dev.michaelfarrant.simplestore.contracts.cataloguemgmt.listing.v1.ListingUpdated.newBuilder()
                            .setTitle(updated.title())
                            .setDescription(updated.description()));
            case ListingWithdrawn _ -> builder.setWithdrawn(
                    dev.michaelfarrant.simplestore.contracts.cataloguemgmt.listing.v1.ListingWithdrawn.getDefaultInstance());
        }

        return builder.build();
    }

    private static PublishedItem toProto(ListingPublished.PublishedItem item) {
        return PublishedItem.newBuilder()
                .setItemId(item.itemId().toString())
                .setCode(item.code())
                .setTitle(item.title())
                .setDescription(item.description())
                .setQuantity(item.quantity())
                .build();
    }

    private static Timestamp toTimestamp(OffsetDateTime dateTime) {
        return Timestamp.newBuilder()
                .setSeconds(dateTime.toEpochSecond())
                .setNanos(dateTime.getNano())
                .build();
    }
}

package dev.michaelfarrant.simplestore.cataloguemgmt.events;

/**
 * Abstraction over the transport used to publish catalogue management events. Must be called inside
 * the transaction that changes the listing; delivery is asynchronous and only happens once it commits.
 */
public interface ListingEventPublisher {

    void publish(ListingEvent event);

}

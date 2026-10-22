package dev.michaelfarrant.simplestore.cataloguemgmt.events;

/**
 * Abstraction over the transport used to publish catalogue management events. Delivery is
 * asynchronous and only happens once the publishing transaction commits.
 */
public interface ListingEventPublisher {

    void publish(ListingEvent event);

}

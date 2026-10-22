package dev.michaelfarrant.simplestore.cataloguemgmt.events;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

/**
 * In-process implementation backed by Spring application events. Stands in for a message broker
 * until one is introduced; see {@link LoggingListingEventListener} for the async, after-commit delivery.
 */
@Component
public class ApplicationListingEventPublisher implements ListingEventPublisher {

    private final ApplicationEventPublisher applicationEventPublisher;

    public ApplicationListingEventPublisher(ApplicationEventPublisher applicationEventPublisher) {
        this.applicationEventPublisher = applicationEventPublisher;
    }

    @Override
    public void publish(ListingEvent event) {
        applicationEventPublisher.publishEvent(event);
    }
}

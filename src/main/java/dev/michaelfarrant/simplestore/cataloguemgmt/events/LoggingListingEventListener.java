package dev.michaelfarrant.simplestore.cataloguemgmt.events;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionalEventListener;

/**
 * Placeholder consumer: receives events asynchronously once the publishing transaction has
 * committed (events from rolled back transactions are dropped). Replace with a broker or
 * storefront consumer when one exists.
 */
@Component
public class LoggingListingEventListener {

    private final Logger logger = LoggerFactory.getLogger(LoggingListingEventListener.class);

    @Async
    @TransactionalEventListener(fallbackExecution = true)
    public void on(ListingEvent event) {
        logger.info("Listing event published: {}", event);
    }
}

package dev.michaelfarrant.simplestore.cataloguemgmt.events;

import dev.michaelfarrant.simplestore.config.rabbitmq.Exchanges;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Declares the exchange catalogue management publishes listing events to. Consumers own their queues.
 */
@Configuration
public class ListingEventsAmqpConfig {

    @Bean
    public TopicExchange catalogueListingEventsExchange() {
        return new TopicExchange(Exchanges.CATALOGUE_LISTING_EVENTS, true, false);
    }
}

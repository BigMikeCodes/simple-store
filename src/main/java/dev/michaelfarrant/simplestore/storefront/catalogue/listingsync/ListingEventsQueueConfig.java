package dev.michaelfarrant.simplestore.storefront.catalogue.listingsync;

import dev.michaelfarrant.simplestore.config.rabbitmq.Exchanges;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.QueueBuilder;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * The storefront's subscription to catalogue management listing events. Messages that can't be processed
 * are dead-lettered to {@value #DEAD_LETTER_QUEUE}.
 */
@Configuration
public class ListingEventsQueueConfig {

    public static final String QUEUE = "storefront.catalogue.listing-events";
    public static final String DEAD_LETTER_QUEUE = QUEUE + ".dlq";
    public static final String DEAD_LETTER_EXCHANGE = "storefront.dlx";

    @Bean
    public Queue storefrontListingEventsQueue() {
        return QueueBuilder.durable(QUEUE)
                .quorum()
                .deadLetterExchange(DEAD_LETTER_EXCHANGE)
                .deadLetterRoutingKey(DEAD_LETTER_QUEUE)
                .build();
    }

    @Bean
    public Binding storefrontListingEventsBinding() {
        // Declared here as well as by catalogue management, as the binding can't be created without it
        TopicExchange exchange = new TopicExchange(Exchanges.CATALOGUE_LISTING_EVENTS, true, false);
        return BindingBuilder.bind(storefrontListingEventsQueue()).to(exchange).with("listing.#");
    }

    @Bean
    public DirectExchange storefrontDeadLetterExchange() {
        return new DirectExchange(DEAD_LETTER_EXCHANGE, true, false);
    }

    @Bean
    public Queue storefrontListingEventsDeadLetterQueue() {
        return QueueBuilder.durable(DEAD_LETTER_QUEUE).quorum().build();
    }

    @Bean
    public Binding storefrontListingEventsDeadLetterBinding() {
        return BindingBuilder.bind(storefrontListingEventsDeadLetterQueue())
                .to(storefrontDeadLetterExchange())
                .with(DEAD_LETTER_QUEUE);
    }
}

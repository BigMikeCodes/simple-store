package dev.michaelfarrant.simplestore.config.rabbitmq;

/**
 * RabbitMQ exchanges shared between bounded contexts. Exchanges are named
 * {@code <context>.<aggregate>.events} and declared by the publishing context; each consuming context
 * declares its own queues ({@code <consumer-context>.<feature>.<...>}) and bindings.
 */
public class Exchanges {

    // Topic exchange for catalogue management listing events, routing keys listing.<event>
    public final static String CATALOGUE_LISTING_EVENTS = "catalogue.listing.events";

}

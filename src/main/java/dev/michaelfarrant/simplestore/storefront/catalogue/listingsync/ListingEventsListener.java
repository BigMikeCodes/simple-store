package dev.michaelfarrant.simplestore.storefront.catalogue.listingsync;

import com.google.protobuf.InvalidProtocolBufferException;
import com.google.protobuf.TextFormat;
import dev.michaelfarrant.simplestore.config.opensearch.Indexes;
import dev.michaelfarrant.simplestore.contracts.cataloguemgmt.listing.v1.ListingEvent;
import dev.michaelfarrant.simplestore.storefront.catalogue.ProductDocument;
import org.opensearch.client.opensearch.OpenSearchClient;
import org.opensearch.client.opensearch._types.OpenSearchException;
import org.opensearch.client.opensearch._types.VersionType;
import org.opensearch.client.opensearch.core.GetResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.AmqpRejectAndDontRequeueException;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.UUID;

/**
 * Projects catalogue management listing events into the products index. Writes use the listing version
 * as an OpenSearch external version, so duplicate and out-of-order events are rejected as conflicts and
 * acknowledged without effect.
 */
@Component
public class ListingEventsListener {

    private final Logger logger = LoggerFactory.getLogger(ListingEventsListener.class);
    private final OpenSearchClient openSearchClient;

    public ListingEventsListener(OpenSearchClient openSearchClient) {
        this.openSearchClient = openSearchClient;
    }

    @RabbitListener(queues = ListingEventsQueueConfig.QUEUE)
    public void on(Message message) throws IOException {
        ListingEvent event = parse(message);
        logger.debug("Received listing event: {}", TextFormat.shortDebugString(event));

        UUID listingId = UUID.fromString(event.getListingId());
        try {
            switch (event.getPayloadCase()) {
                case PUBLISHED -> index(ListingProductMapper.toProduct(listingId, event.getPublished()), event.getListingVersion());
                case UPDATED -> index(ListingProductMapper.withDetails(getProduct(listingId), event.getUpdated()), event.getListingVersion());
                case WITHDRAWN -> delete(listingId, event.getListingVersion());
                case PAYLOAD_NOT_SET -> throw new AmqpRejectAndDontRequeueException(
                        "Listing event %s has no payload (sent by a newer producer?)".formatted(event.getEventId()));
            }
        } catch (OpenSearchException exception) {
            if (exception.status() != 409) {
                throw exception;
            }
            logger.debug("Ignoring stale listing event {} (version {})", event.getEventId(), event.getListingVersion());
        }
    }

    private ListingEvent parse(Message message) {
        try {
            return ListingEvent.parseFrom(message.getBody());
        } catch (InvalidProtocolBufferException exception) {
            // Retrying won't help a malformed message; once the listener's retries give up it is dead-lettered
            throw new AmqpRejectAndDontRequeueException("Malformed listing event", exception);
        }
    }

    private ProductDocument getProduct(UUID listingId) throws IOException {
        GetResponse<ProductDocument> response = openSearchClient.get(
                builder -> builder.index(Indexes.PRODUCTS).id(listingId.toString()), ProductDocument.class);
        if (!response.found()) {
            // Most likely the ListingPublished event hasn't been processed yet; fail so the message is retried
            throw new IllegalStateException("Product %s not found for listing update".formatted(listingId));
        }
        return response.source();
    }

    private void index(ProductDocument product, long version) throws IOException {
        openSearchClient.index(builder -> builder
                .index(Indexes.PRODUCTS)
                .id(product.id().toString())
                .document(product)
                .version(version)
                .versionType(VersionType.External));
    }

    private void delete(UUID listingId, long version) throws IOException {
        openSearchClient.delete(builder -> builder
                .index(Indexes.PRODUCTS)
                .id(listingId.toString())
                .version(version)
                .versionType(VersionType.External));
    }
}

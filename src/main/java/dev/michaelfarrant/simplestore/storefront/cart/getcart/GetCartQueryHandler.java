package dev.michaelfarrant.simplestore.storefront.cart.getcart;

import dev.michaelfarrant.simplestore.config.opensearch.Indexes;
import dev.michaelfarrant.simplestore.storefront.catalogue.ProductDocument;
import dev.michaelfarrant.simplestore.storefront.cart.CartResponse;
import dev.michaelfarrant.simplestore.storefront.cart.Item;
import org.opensearch.client.opensearch.OpenSearchClient;
import org.opensearch.client.opensearch.core.MgetResponse;
import org.opensearch.client.opensearch.core.get.GetResult;
import org.opensearch.client.opensearch.core.mget.MultiGetResponseItem;
import org.slf4j.Logger;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@Service
public class GetCartQueryHandler {

    private final Logger logger = org.slf4j.LoggerFactory.getLogger(GetCartQueryHandler.class);
    private final RedisTemplate<String, Integer> redisTemplate;
    private final OpenSearchClient openSearchClient;

    public GetCartQueryHandler(
            RedisTemplate<String, Integer> redisTemplate,
            OpenSearchClient openSearchClient) {
        this.redisTemplate = redisTemplate;
        this.openSearchClient = openSearchClient;
    }

    private static String cartHashKey(UUID cartId) {
        return "cart:" + cartId.toString();
    }

    private List<ProductDocument> getItemsFromOpenSearch(Set<String> productIds) {

        List<String> ids = productIds.stream().toList();

        try {
            MgetResponse<ProductDocument> response = openSearchClient
                    .mget(builder -> builder.index(Indexes.PRODUCTS).ids(ids), ProductDocument.class);

            return response.docs().stream()
                    .filter(MultiGetResponseItem::isResult)
                    .map(MultiGetResponseItem::result)
                    .filter(GetResult::found)
                    .map(GetResult::source)
                    .toList();

        } catch (IOException exception) {
            logger.error("Error fetching products from OpenSearch", exception);
            return List.of();
        }
    }

    public CartResponse handle(GetCartQuery query) {

        String cartHashKey = cartHashKey(query.userId());
        HashOperations<String, String, Integer> hashOps = redisTemplate.opsForHash();
        var entries = hashOps.entries(cartHashKey);

        Set<String> productIds = entries.keySet();
        List<ProductDocument> documents = getItemsFromOpenSearch(productIds);

        List<Item> items = documents.stream()
                .map(doc -> new Item(doc.name(), doc.id(), entries.get(doc.id().toString())))
                .toList();

        return new CartResponse(items);
    }

}

package dev.michaelfarrant.simplestore.cart.getcart;

import dev.michaelfarrant.simplestore.cart.CartResponse;
import dev.michaelfarrant.simplestore.cart.Item;
import org.opensearch.client.opensearch.OpenSearchClient;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;
import java.util.UUID;

@Service
public class GetCartQueryHandler {

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


    public CartResponse handle(GetCartQuery query) {

        String cartHashKey = cartHashKey(query.userId());
        HashOperations<String, String, Integer> hashOps = redisTemplate.opsForHash();
        var entries = hashOps.entries(cartHashKey);

        Set<String> keys = entries.keySet();

        // get products from opensearch


        // join everything together & return


        var items = entries.entrySet().stream().map(entry -> {
            String productIdStr = entry.getKey();
            Integer quantity = entry.getValue();

            return new Item("Some product", UUID.fromString(productIdStr), quantity);
        }).toList();

        return new CartResponse(items);
    }

}

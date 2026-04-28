package dev.michaelfarrant.simplestore.cart.setitemquantity;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class SetItemQuantityCommandHandler {

    private final RedisTemplate<String, Integer> redisTemplate;

    public SetItemQuantityCommandHandler(RedisTemplate<String, Integer> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    private static String cartHashKey(UUID cartId) {
        return "cart:" + cartId.toString();
    }

    public void handle(SetItemQuantityCommand command) {

        String cartHashKey  = cartHashKey(command.userId());
        String productIdStr = command.productId().toString();
        redisTemplate.opsForHash().put(cartHashKey, productIdStr, String.valueOf(command.quantity()));

    }

}

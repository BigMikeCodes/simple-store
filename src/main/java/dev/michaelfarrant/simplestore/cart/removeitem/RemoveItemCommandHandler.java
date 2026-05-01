package dev.michaelfarrant.simplestore.cart.removeitem;

import dev.michaelfarrant.simplestore.cart.CartKeyUtils;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

@Service
public class RemoveItemCommandHandler {

    private final RedisTemplate<String, Integer> redisTemplate;

    public RemoveItemCommandHandler(RedisTemplate<String, Integer> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    public void handleCommand(RemoveItemCommand command) {
        String cartKey = CartKeyUtils.toCartKey(command.userId());
        String productId = command.productId().toString();
        redisTemplate.opsForHash().delete(cartKey, productId);
    }
}

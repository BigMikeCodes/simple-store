package dev.michaelfarrant.simplestore.storefront.cart.removeitem;

import java.util.UUID;

public record RemoveItemCommand(UUID productId, UUID userId) {
}

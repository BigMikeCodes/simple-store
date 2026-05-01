package dev.michaelfarrant.simplestore.storefront.cart.setitemquantity;

import java.util.UUID;

public record SetItemQuantityCommand(UUID userId, UUID productId, int quantity) {
}

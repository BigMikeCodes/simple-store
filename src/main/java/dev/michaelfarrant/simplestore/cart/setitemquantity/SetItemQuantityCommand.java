package dev.michaelfarrant.simplestore.cart.setitemquantity;

import java.util.UUID;

public record SetItemQuantityCommand(UUID userId, UUID productId, int quantity) {
}

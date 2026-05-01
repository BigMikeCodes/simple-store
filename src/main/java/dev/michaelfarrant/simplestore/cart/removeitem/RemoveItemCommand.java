package dev.michaelfarrant.simplestore.cart.removeitem;

import java.util.UUID;

public record RemoveItemCommand(UUID productId, UUID userId) {
}

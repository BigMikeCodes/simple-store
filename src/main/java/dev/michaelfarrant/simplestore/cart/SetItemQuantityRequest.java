package dev.michaelfarrant.simplestore.cart;

import java.util.UUID;

public record SetItemQuantityRequest(UUID productId, int quantity) {
}

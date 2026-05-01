package dev.michaelfarrant.simplestore.storefront.cart;

import java.util.UUID;

public record Item(String name, UUID id, int quantity) {
}

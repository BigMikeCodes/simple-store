package dev.michaelfarrant.simplestore.cart;

import java.util.UUID;

public record Item(String name, UUID id, int quantity) {
}

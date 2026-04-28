package dev.michaelfarrant.simplestore.cart;

import java.util.List;

public record CartResponse(List<Item> items) {
}

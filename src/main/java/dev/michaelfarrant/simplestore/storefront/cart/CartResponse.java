package dev.michaelfarrant.simplestore.storefront.cart;

import java.util.List;

public record CartResponse(List<Item> items) {
}

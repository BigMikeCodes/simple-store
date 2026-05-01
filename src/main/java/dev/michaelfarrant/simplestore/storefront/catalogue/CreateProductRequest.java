package dev.michaelfarrant.simplestore.storefront.catalogue;

import java.util.Set;

public record CreateProductRequest(
        String name,
        String description,
        Set<String> tags) {
}

package dev.michaelfarrant.simplestore;

import java.util.Set;

public record CreateProductRequest(
        String name,
        String description,
        Set<String> tags) {
}

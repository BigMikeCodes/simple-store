package dev.michaelfarrant.simplestore;

import java.util.Set;
import java.util.UUID;

public record ProductResource(
        UUID id,
        String name,
        String description,
        Set<String> tags) {
}
